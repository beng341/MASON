package agents.initialize;

import java.lang.reflect.*;
import java.lang.NoSuchFieldException;
import sim.util.Bag;

/*
 * This class is used to set the parameters of an object.  As I will build agent-based
 * models, mutable parameters we be loaded in a chromosome with a variable name
 * identifying the parameter to be looked up in the appropriate agent and set at
 * runtime.  Non mutable parameters can be done the same way.  My plan is to have 
 * the parameters in agents be integers that look up values in the chromosomes or
 * parameter arrays attached to them.  This will make substituting chromosomes easy
 * once the chromosome is indexed in each agent.
 */

public final class SetParameters {

	public final static void setDouble(Object o, String prameter, double value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setDouble(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}
	
	public final static Object getObject(Object o, String prameter){
		Object obj = null;
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			obj = f.get(o);
		}	catch (Throwable e) {
			System.err.println("Error in get object: " + " String parameter: " + prameter + ", obj: " + obj + ", e: " +e);
		}
		return obj;
	}

	public final static void setShort(Object o, String prameter, short value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setShort(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}

	public final static void setInt(Object o, String prameter, int value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setInt(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}

	public final static void setLong(Object o, String prameter, long value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setLong(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}

	public final static void setChar(Object o, String prameter, char value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setChar(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}

	public final static void setByte(Object o, String prameter, byte value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setByte(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}


	public final static void setBoolean(Object o, String prameter, boolean value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.setBoolean(o, value);
		}	catch (Throwable e) {
			System.err.println("SetBoolean: " + e);
		}
	}

	public final static void setString(Object o, String prameter, String value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.set(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}

	public final static void setBag(Object o, String prameter, Bag value){
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			f.set(o, value);
		}	catch (Throwable e) {
			System.err.println(e);
		}
	}
	

	/**
	 * This method takes an object, finds the corresponding public parameter (if it exists),
	 * then sets the value to that parameter.  As this method will be used, value will be
	 * either a double or String by assumption.  If this assumption cannot be made, then
	 * some additions should be made.
	 * @param o
	 * @param prameter
	 * @param value
	 */

	public final static void set(Object o, String prameter, Object value){
		Class cp = null;
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			cp = f.getType();
			if(cp.equals(int.class)){
				double d = ((Double)value).doubleValue();
				f.setInt(o, (int)d);
			}
			else if(cp.equals(short.class))
				f.setShort(o, (short)((Double)value).doubleValue());
			else if(cp.equals(long.class)){
				double d = ((Double)value).doubleValue();
				f.setLong(o, (long)d);	
			}
			else if(cp.equals(double.class))
				f.setDouble(o, ((Double)value).doubleValue());
			else if(cp.equals(float.class))
				f.setFloat(o, (float)((Double)value).doubleValue());
			else  if(cp.equals(boolean.class))	{	
				String s = value.toString();
				boolean b;
				if(s.equalsIgnoreCase("true"))
					b = true;
				else
					b = false;
				f.setBoolean(o, b);	
			}
			else  if(cp.equals(char.class))		 
				f.setChar(o, (Character)value);
			else  if(cp.equals(byte.class))		 
				f.setByte(o, (Byte)value);
			else  if(cp.equals(String.class))		 
				f.set(o, (String)value);
			else  if(cp.equals(Bag.class))		 
				f.set(o, (Bag)value);
		} catch (NoSuchFieldException e){
			//"do nothing"
		
		}	catch (Throwable e) {
			if(true);
			System.out.println("Error in set "+ prameter+" " + cp.toString()+ " " + value +" " + e);
		}
	}
	
	public final static void set2(Object o, String prameter, Object value){
		Class cp = null;
		try {
			Class c = o.getClass();
			Field f = c.getField(prameter);
			cp = f.getType();
			if(cp.equals(int.class))
				f.setInt(o, (Integer)value);
			else if(cp.equals(short.class))
				f.setShort(o, (Short)value);
			else if(cp.equals(long.class))
				f.setLong(o, (Long)value);
			else if(cp.equals(double.class))
				f.setDouble(o, (Double)value);
			else if(cp.equals(float.class))
				f.setFloat(o, (Float)value);
			else  if(cp.equals(boolean.class))		 
				f.setBoolean(o, (Boolean)value);
			else  if(cp.equals(char.class))		 
				f.setChar(o, (Character)value);
			else  if(cp.equals(byte.class))		 
				f.setByte(o, (Byte)value);
			else  if(cp.equals(String.class))		 
				f.set(o, (String)value);
			else  if(cp.equals(Bag.class))		 
				f.set(o, (Bag)value);
			
		}	catch (Throwable e) {
			System.err.println("Error in set "+ prameter+" " + cp.toString()+ " " + value +" " + e);
		}
	}

	public final static void setStringsWithBags(Object o, Bag vars, Bag values){
		int m = vars.numObjs;
		for(int i=0;i<m;i++)
			if(vars.objs[i] != null && values.objs[i] != null)
				SetParameters.setString(o,(String)vars.objs[i], (String)values.objs[i]);
	}

	public final static void setIntWithLength(Object o,String[] s){
		for(int i=0;i<s.length;i++)
			SetParameters.setInt(o,s[i], i);	
	}

	public final static void setIntWithArray(Object o,String[] s, int[] a){
		for(int i=0;i<s.length;i++)
			SetParameters.setInt(o,s[i], a[i]);	
	}

	public final static void setWithBag(Object o, Bag s, Bag a){
		for(int i=0;i<s.numObjs;i++)
			set(o,(String)s.objs[i], a.objs[i]);
	}
}
