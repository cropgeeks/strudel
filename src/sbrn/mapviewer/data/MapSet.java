package sbrn.mapviewer.data;

import java.util.*;

// So far this class is just a wrapper around a list (with a name). Overkill for
// now but it may require more in the future.
public class MapSet implements Iterable
{
	// The name of this MapSet
	private String name;
	
	// Holds a list of all the maps in this set
	private LinkedList<Map> maps = new LinkedList<Map>();
		
	public MapSet(String name)
	{
		this.name = name;
	}
	
	// Allows you to use MapSet in a J2SE1.5 for loop:
	// for (Map map: myMapSet) {}
	public Iterator<Map> iterator()
		{ return maps.iterator(); }
	
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public LinkedList<Map> getMaps()
		{ return maps; }
	
	public Map getMap(int index)
		{ return maps.get(index); }
	
	public int size()
		{ return maps.size(); }
}