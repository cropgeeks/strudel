package sbrn.mapviewer.data;

import java.util.*;

public class Feature
{
	// Feature types
	public final static int GENERIC = 0;
	
	public final static int MARKER = 1;
	public final static int SSR = 2;
	public final static int SNP = 3;
	//etc
	
	
	// The name of this feature
	private String name;
	// And any aliases
	private LinkedList<String> aliases = new LinkedList<String>();
	
	// Its start and stop positions (in whatever distance format) on the map
	private float start, stop;
	
	public double rnd = Math.random();
	
	// Feature type
	private int type = GENERIC;
	
	public Feature(String name)
	{
		this.name = name;
	}
	
	public String toString()
		{ return name; }
	
	public String getName()
		{ return name; }
	
	public void setStart(float start)
		{ this.start = start; }
	
	public float getStart()
		{ return start; }
	
	public void setStop(float stop)
		{ this.stop = stop; }
	
	public float getStop()
		{ return stop; }
	
	public int getType()
		{ return type; }
	
	public void setType(int type)
		{ this.type = type; }
	
	public LinkedList<String> getAliases()
		{ return aliases; }
	
	public void addAlias(String alias)
		{ aliases.add(alias); }
}