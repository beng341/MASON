package agents.timing;



public class Clock {
	public final static double START_TIME = 0;
	public double currentTime = START_TIME;
	public int countDown = 0;
	public boolean alarm = false;
	
	public void setCountDown(final int countDown){
		this.countDown = countDown;
		alarm = false;		
	}
	
	public void reset (final int countDown, final double currentTime){
		this.currentTime = currentTime;
		this.countDown = countDown;
		alarm = false;
	}
	
	public void step(){
		if(countDown == 0){
			alarm = true;
			currentTime ++;
		}
		else 
			countDown --;
	}
	

}
