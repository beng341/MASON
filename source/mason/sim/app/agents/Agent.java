package agents;
import java.awt.Color;

import ec.util.MersenneTwisterFast;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.util.*;

public class Agent implements Steppable{
	public int xdir;
	public int ydir;
	public Stoppable event;
	public double energy;
	public boolean walk;
	public double mixed_p;

	public Int2D type;
	public boolean played;
	public Int2D loc;
	
	public MersenneTwisterFast random;
	public Bag recycledAgents;

	public double getenergy(){return energy;}
	public double getmixed_p(){return mixed_p;}
	public int getStrategy(){return type.x; }
	
	
	/***Variables  and Methods for probabilistic movement **/
	public int orientation;
	public double[][] movementProbs = null;
	public int[][] orientationLookUp = null;
	public Int2D[][] directionLookUp = null;
	public Int2D[] randomMovement = null;


	public final int lookUpOrientation (final int xdir, final int ydir){
		return orientationLookUp[xdir+1][ydir+1];
	}

	public final Int2D changeDirection(final int orientation, final int direction) {
		return directionLookUp[orientation][direction];
	}

	public final int findDirction(){
		final double x = random.nextDouble();
		if(x < movementProbs[0][1]) //north
		return 0;
		else if(x < movementProbs[0][2]) //northeast                      
			return 1;
		else if(x < movementProbs[1][2]) //east
			return 2;
		else if(x < movementProbs[2][2]) //southeast
			return 3;
		else if(x < movementProbs[2][1]) //south
			return 4;
		else if(x < movementProbs[2][0]) //southwest
			return 5;
		else if(x < movementProbs[1][0]) //west
			return 6;
		else
			return 7; //northwest by default
	}

	public final Int2D newDirection(){
		final int direction = findDirction();
		final Int2D dir = changeDirection(orientation, direction);
		orientation =lookUpOrientation (dir.x,dir.y);
		return dir;
	}

	/*
	 * End of new data and methods for probabilistic movement.
	 */

	public Agent(int xdir, int ydir){
		this.xdir = xdir;
		this.ydir = ydir;
	}

	public final int findMove(final AgentsSimulation as, final int m){
		int move = 0;
		if (m == AgentsSimulation.COOPERATOR)
			move = AgentsSimulation.COOPERATE;
		else if(m == AgentsSimulation.DEFECTOR)
			move = AgentsSimulation.DEFECT;
		else if(m == AgentsSimulation.MIXED){
			final boolean b = as.random.nextBoolean(mixed_p);
			if(b)
				move = AgentsSimulation.COOPERATE;
			else
				move = AgentsSimulation.DEFECT;
		}
		else if(m == AgentsSimulation.TIT_FOR_TAT){
			//this is going to take some doing. I'll come back to this.

		}
		final boolean useError = as.useError;
		if(useError) {//"error"
			final double errorRate = as.errorRate;
			final boolean error = as.random.nextBoolean(errorRate);
			if(error)
				return Math.abs(move - 1); //return the opposite of the intended move.
			else
				return move;
		}
		else
			return move;
	}
	// TODO Create TFT, PAVLOV, and Grim agents. This will require imbuing the agents
	// with memory. We will have to give all agents IDs, and allow other agents
	// to remember these.

	/**
	 * Play the game
	 */
	public void playPD(final AgentsSimulation as){
		final Agent b = findPlayer(as);
		if(b != null){
			final int moveA = findMove(as, this.type.x);
			final int moveB = findMove(as, b.type.x);
			this.energy += as.payoffs[moveA][moveB];
			b.energy += as.payoffs[moveB][moveA];
			this.played = true;
			b.played = true;
			//determine if they walkaway
			final boolean alwaysWalk = as.alwaysWalk;
			if(alwaysWalk || (this.type.y == AgentsSimulation.WALK_AWAY && moveB == AgentsSimulation.DEFECT))
				this.walk = true;
			else 
				this.walk = false;


			if(alwaysWalk || (b.type.y == AgentsSimulation.WALK_AWAY && moveA == AgentsSimulation.DEFECT))
				b.walk = true;
			else 
				b.walk = false;
		}
	}


	/**
	 * Find a neighboring player who hasn't already played
	 */
	public  final Agent findPlayer(AgentsSimulation as){
		final ObjectGrid2D agentsSpace = as.agentsSpace;
		//final Int2D loc = as.agentsSpace.getObjectLocation(this);

		int distance = 1;
		final boolean occupySameSpace = as.occupySameSpace;
		if(occupySameSpace)
			distance = 0;
		Bag neighbors = new Bag();
		if(as.mooreNeighborhood || occupySameSpace){
			neighbors = agentsSpace.getNeighborsMaxDistance(loc.x, loc.y, distance, as.toroidal, neighbors, null, null);
		}
		else
			neighbors = vonNeumannNeighbors(as);
		
		if(neighbors != null){
			Bag newNeighbors = null;
			if(as.multipleInteractions)
				newNeighbors = findNotPlayedMultiple(neighbors);
			else 
				newNeighbors = findNotPlayed(neighbors);
			final int numObjs = newNeighbors.numObjs;
			if(numObjs > 0){
				if(numObjs == 1)
					return (Agent)newNeighbors.objs[0];
				else
					return (Agent)newNeighbors.objs[as.random.nextInt(numObjs)];
			}
			else return null;
		}
		
/*		if(neighbors != null){ //if there are neighbors
			if(as.multipleInteractions){
				final int numObjs = neighbors.numObjs;
				Bag newNeighbors = findNotPlayed(neighbors);
				final int numObjs2 = newNeighbors.numObjs;
				System.out.println("Totalneighbors: " + numObjs + ", Unplayed neighbors: " + numObjs2);
				
				if(numObjs > 0){
					if(numObjs == 1)
						return (Agent)neighbors.objs[0];
					else
						return (Agent)neighbors.objs[as.random.nextInt(numObjs)];
				}
				else return null;
			}
			else {//no multiple interactions
				Bag newNeighbors = findNotPlayed(neighbors);
				final int numObjs = newNeighbors.numObjs;
				if(numObjs > 0){
					if(numObjs == 1)
						return (Agent)newNeighbors.objs[0];
					else
						return (Agent)newNeighbors.objs[as.random.nextInt(numObjs)];
				}
				else return null;
			}
		}*/
		else
			return null;
	}

	/**
	 * Find a neighboring player who hasn't already played from the bag of neighbors
	 */
	public  final Bag findNotPlayed(final Bag n){		
		final Bag neighbors = new Bag();
		for(int i = 0; i < n.numObjs; i++){
			Object obj = n.objs[i];
			if(obj != null){
				Agent a = (Agent)n.objs[i];
				if(!a.played && !a.equals(this))
					neighbors.add(a);
			}
		}
		return neighbors;
	}
	
	/**
	 * Returns a bag with all neighboring agents, whether or not they've already played. 
	 */
	public  final Bag findNotPlayedMultiple(final Bag n){		
		final Bag neighbors = new Bag();
		for(int i = 0; i < n.numObjs; i++){
			Object obj = n.objs[i];
			if(obj != null){
				Agent a = (Agent)n.objs[i];
				if(!a.equals(this))
					neighbors.add(a);
			}
		}
		return neighbors;
	}
	
	
	/**
	 * Play the game with all neighbors simultaneously.
	 */
	public void playPDWithAll(final AgentsSimulation as){
		
		Bag neighbors = new Bag();
		if(as.mooreNeighborhood){
			neighbors = as.agentsSpace.getNeighborsMaxDistance(loc.x, loc.y, 1, as.toroidal, neighbors, null, null);
		}
		else
			neighbors = vonNeumannNeighbors(as);
		Bag newNeighbors = findNotPlayedMultiple(neighbors); //get all neighbors who are agents.
		
		if(newNeighbors != null && newNeighbors.numObjs > 0){
			for(int i = 0; i < newNeighbors.numObjs; i++){
				Agent b = (Agent)newNeighbors.get(i);
				final int moveA = findMove(as, this.type.x);
				final int moveB = findMove(as, b.type.x);
				this.energy += as.payoffs[moveA][moveB];
				b.energy += as.payoffs[moveB][moveA];
				this.played = true;
				b.played = true;
				//determine if they walkaway
				final boolean alwaysWalk = as.alwaysWalk;
				if(alwaysWalk || (this.type.y == AgentsSimulation.WALK_AWAY && moveB == AgentsSimulation.DEFECT))
					this.walk = true;
				else 
					this.walk = false;


				if(alwaysWalk || (b.type.y == AgentsSimulation.WALK_AWAY && moveA == AgentsSimulation.DEFECT))
					b.walk = true;
				else 
					b.walk = false;
			}	
		}
	}
	
	
	
	public Bag vonNeumannNeighbors(AgentsSimulation as){
		Int2D[] players;
		final ObjectGrid2D agentsSpace = as.agentsSpace;
		//final Int2D loc = agentsSpace.getObjectLocation(this);
		if(as.toroidal || (loc.x > 0 && loc.x < (as.gridWidth - 1) && loc.y > 0 && loc.y < (as.gridHeight - 1))){
			players = new Int2D[4];
			players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
			players[1] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
			players[2] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
			players[3] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
			}
			else{
				if(loc.x == 0){
					if(loc.y == 0){
						players = new Int2D[2];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
						players[1] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
					}
					else if(loc.y == as.gridHeight - 1){
						players = new Int2D[2];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
						players[1] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
					}
					else{
						players = new Int2D[3];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
						players[1] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
						players[2] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
					}
				}
				else if(loc.x == (as.gridWidth - 1)){
					if(loc.y == 0){
						players = new Int2D[2];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
						players[1] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
					}
					else if(loc.y == as.gridHeight - 1){
						players = new Int2D[2];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
						players[1] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
					}
					else{
						players = new Int2D[3];
						players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
						players[1] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
						players[2] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
					}	
				}
				else if(loc.y == 0){
					players = new Int2D[3];
					players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y + 1)); //up
					players[1] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
					players[2] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
				}
				else{//loc.y == gridHeight - 1
					players = new Int2D[3];
					players[0] = new Int2D(loc.x, as.agentsSpace.sty(loc.y - 1)); //down
					players[1] = new Int2D(as.agentsSpace.stx(loc.x - 1), loc.y); //left
					players[2] = new Int2D(as.agentsSpace.stx(loc.x + 1), loc.y); //right
				}
			}
		Bag neighbors = new Bag();
		for(int i = 0; i < players.length; i++){
			int x = players[i].x;
			int y = players[i].y;
			Object obj = as.agentsSpace.get(x, y);
			if(obj != null){//if there is an agent there
				Agent b = (Agent)obj;
				//Bag temp = as.agentsSpace.getObjectsAtLocation(x, y);
				//Agent b = (Agent)temp.get(0);
				neighbors.add(b);
			}
		}
	return neighbors;
	}
	
	

	/**
	 * Reproduce the agent
	 */
	public void reproduce(AgentsSimulation as){
		final Bag bag = as.allAgents; // get all  agents    
		final long maxAgents = as.maxAgents;
		if(bag.numObjs < maxAgents){ // if not too many, reproduce one agent
			Int2D newLoc = newLocation(as);	
			//if(this is an OK location
			//final Bag here = as.agentsSpace.getObjectsAtLocation(newLoc);
			final Object obj = as.agentsSpace.get(newLoc.x, newLoc.y);
			final int wallType = as.wallType;
			final int wallGrid = as.wallGrid.field[newLoc.x][newLoc.y]; 
			final boolean occupySameSpace = as.occupySameSpace;
			if((wallType == as.NO_WALL || wallGrid == 0) && (occupySameSpace || obj == null)){
				
				
				Int2D dir = null; 
				if(as.mooreNeighborhood){
					final int m =random.nextInt(8);
					dir = this.randomMovement[m];
				}
				else
					dir = this.randomMovement[0];
				Agent o;
				if(recycledAgents.isEmpty())
					o = new Agent(dir.x, dir.y);
				else {
					o = (Agent)recycledAgents.objs[0];
					recycledAgents.remove(0); // remove the reused agent
				}
				reInitAgent(o);
				
				o.orientation = lookUpOrientation(dir.x,dir.y);
				if(as.offSpringGetsHalf){
					o.energy = this.energy/2.0;
					this.energy = this.energy/2.0;
				}
				else{
					o.energy = 50.0;
					this.energy -= 50.0;
				}
				o.type = new Int2D(this.type.x, this.type.y);
				o.mixed_p = this.mixed_p;
				if(as.mutation){	// this mutation is for continuous, probabilistic strategies
					//System.out.println("Mutation!");
					if(random.nextBoolean(as.mutationProb)){
						final double p = random.nextGaussian()*.2;
						double temp = o.mixed_p + p;
						if(temp < 0)
							o.mixed_p = 0;
						else if(temp > 1)
							o.mixed_p = 1;
						else
							o.mixed_p = temp;
					}
				}
				//final double epsteinMutation = as.epsteinMutation;
				//final boolean eMutation =random.nextBoolean(epsteinMutation);
				if(random.nextBoolean(as.epsteinMutation)){	//this mutation is a C begetting a D or vice versa
					//System.out.println("Epstein Mutation!");
					mutate(o);
				}
				setPortrayal(as, o);
				//as.agentsSpace.setObjectLocation(o, newLoc);
				as.agentsSpace.set(newLoc.x, newLoc.y, o);
				o.loc = new Int2D(newLoc.x, newLoc.y);
				o.event = as.schedule.scheduleRepeating(o);
				as.allAgents.add(o);
			}
		}
	}

	public void mutate(Agent a){
		int y = a.type.y;
		if(a.type.x == AgentsSimulation.COOPERATOR){
			a.type = new Int2D(AgentsSimulation.DEFECTOR, y);
			a.mixed_p = 0.0;
		}
		else if(a.type.x == AgentsSimulation.DEFECTOR){
			a.type = new Int2D(AgentsSimulation.COOPERATOR, y);
			a.mixed_p = 1.0;
		}
	}

	public void reInitAgent(final Agent a){
		a.walk = false;
		a.played = false;
		a.movementProbs = this.movementProbs;
		a.orientationLookUp = this.orientationLookUp;
		a.directionLookUp = this.directionLookUp;
		a.randomMovement = this.randomMovement;
		a.random = this.random;
		a.recycledAgents = this.recycledAgents;
	}

	/*
	 * Find a new location for the new agent to be placed
	 */
	public Int2D newLocation(final AgentsSimulation as){
		int x,y;
		final boolean notrandomReproduction = !as.randomReproduction;
		if(notrandomReproduction){
			int i = random.nextInt(3) - 1;
			final int reproductionRadius = as.reproductionRadius;
			int chx = i*reproductionRadius;  
			i = random.nextInt(3) - 1;
			int chy = i*reproductionRadius; 
			while(chx == chy && chx == 0){
				i = random.nextInt(3) - 1;
				chx = i*reproductionRadius; 
				i = random.nextInt(3) - 1;
				chy = i*reproductionRadius; 
			}
			if(!as.mooreNeighborhood){//if use von Neumann neighborhood
				chx = reproductionRadius;
				chy = reproductionRadius;
				final boolean b3 = random.nextBoolean(.5);
				if(b3)
					chx = 0;
				else
					chy = 0;
			}
			
			final boolean b1 = random.nextBoolean(.5);
			if(b1)
				chx = -chx;
			final boolean b2 = random.nextBoolean(.5);
			if(b2)
				chy = -chy;
			x = loc.x + chx;
			y = loc.y + chy;
			final boolean nottoroidal = !as.toroidal;
			final int gridWidth = as.gridWidth;
			final int gridHeight = as.gridHeight;
			if(nottoroidal){
				if(x > gridWidth-1 || x < 0)
					x = loc.x - chx;
				if(y > gridHeight-1 || y < 0)
					y = loc.y - chy;
			}
			else{
				x = as.agentsSpace.stx(x);
				y = as.agentsSpace.sty(y);
			}
		}
		else{
			final int gridWidth = as.gridWidth;
			final int gridHeight = as.gridHeight;
			x = random.nextInt(gridWidth - 1) + 1; 
			y = random.nextInt(gridHeight - 1) + 1; 
		}
		return new Int2D(x,y);
	}



	public void setPortrayal(AgentsSimulation as, Agent a){
		if(a.type.y == AgentsSimulation.NOT_WALK_AWAY)        
			as.agentsPortrayal.setPortrayalForObject(a,new sim.portrayal.simple.RectanglePortrayal2D(new Color((float)(1.0-this.mixed_p),(float)0.0,(float)this.mixed_p)));              
		else if(a.type.x == AgentsSimulation.DEFECTOR)
			as.agentsPortrayal.setPortrayalForObject(a,new sim.portrayal.simple.RectanglePortrayal2D(Color.magenta));
		else if(a.type.x == AgentsSimulation.COOPERATOR)
			as.agentsPortrayal.setPortrayalForObject(a,new sim.portrayal.simple.RectanglePortrayal2D(Color.cyan));
		else               
			as.agentsPortrayal.setPortrayalForObject(a,new sim.portrayal.simple.OvalPortrayal2D(new Color((float)(1.0-this.mixed_p),(float)0.0,(float)this.mixed_p)));
	}

	public void moveProbabilistically(AgentsSimulation as){
		Int2D dir =newDirection();
		xdir = dir.x;
		ydir = dir.y;
		Int2D newLoc = new Int2D(loc.x + xdir,loc.y + ydir);
		final boolean toroidal = as.toroidal;
		if(toroidal){
			int x = as.agentsSpace.stx(newLoc.x);
			int y = as.agentsSpace.sty(newLoc.y);
			newLoc = new Int2D(x,y);
		}
		else
			newLoc = bounceOffWalls(as, newLoc);	
		//eventually, account for obstacles and collisions
		//Bag bag = as.agentsSpace.getObjectsAtLocation(newLoc);
		Object obj = as.agentsSpace.get(newLoc.x, newLoc.y);
		final int wallType =as.wallType;
		final boolean occupySameSpace = as.occupySameSpace;
		
		if((wallType == as.NO_WALL || as.wallGrid.field[newLoc.x] [newLoc.y] == 0) && (occupySameSpace || 
				obj == null)){
			as.agentsSpace.set(newLoc.x, newLoc.y, this);
			as.agentsSpace.set(loc.x, loc.y, null);
			loc = newLoc;
		}
	}

	public final Int2D bounceOffWalls(final AgentsSimulation as, final Int2D newLoc){
		int x = newLoc.x;
		int y = newLoc.y;
		if(x < 0 || x >= as.gridWidth){
			xdir = -xdir;
			x = loc.x + xdir;
		}
		if(y < 0 || y >= as.gridHeight){
			ydir = -ydir;
			y = loc.y + ydir;
		}
		orientation = lookUpOrientation (xdir,ydir);
		return new Int2D(x, y);
	}


	public void die(AgentsSimulation as){
		//as.agentsSpace.remove(this);
		as.agentsSpace.set(loc.x, loc.y, null);
		as.allAgents.remove(this);
		if(this.event != null)
			this.event.stop();
		recycledAgents.add(this);
	}




	/*********************************************************************************************************
	 * 					Step Method
	 ***********************************************************************************************************/
	public void step(SimState state){
		final AgentsSimulation as = (AgentsSimulation)state;

		//if death conditions, kill agent. Else,
		final double randomDeath = as.randomDeath;
		final boolean death = as.random.nextBoolean(randomDeath);
		final double energy = this.energy;
		if(death|| energy <= 0) //random death or loss of energy
			die(as);
		else{
			//loc = as.agentsSpace.getObjectLocation(this);
			if(as.playWithAll)
				playPDWithAll(as);
			else if(as.multipleInteractions || !this.played)
				playPD(as);
                        
			//reproduce
			final double reproduceEnergy = as.reproduceEnergy;
			if(energy >= reproduceEnergy)
				reproduce(as);
                        
			//energy loss
			final double energyLoss = as.energyLoss;
			this.energy -= energyLoss;
			final double maxEnergy = as.maxEnergy;
			if(energy > maxEnergy)
				this.energy = maxEnergy;
                        
			//move probabilistically
			//NOTE: ObjectGrid2D can only hold one object per location, so occupySameSpace doesn't work. 
			if(!this.played || this.walk)
				moveProbabilistically(as);
		}
                
		final boolean aktipisDeath = as.aktipisDeath;
		if(aktipisDeath){
			final Bag agents = as.allAgents;
			final int pop = agents.numObjs;
			final long maxAgents = as.maxAgents;
			if(pop >= maxAgents){
				final int r = random.nextInt(pop);
				Agent a = (Agent)agents.objs[r];
				//a.die(as);
				a.energy -= 10;
			}
		}

	}//end step method


}//end class
