package sbrn.mapviewer.data;

import java.util.*;

/**
 * So far this class is just a wrapper around a list (with a name). Overkill for
 * now but it may require more in the future.
 */
public class MapSet implements Iterable<ChromoMap>
{
	// The name of this MapSet
	private String name;
	
	// Holds a list of all the maps in this set
	private LinkedList<ChromoMap> maps = new LinkedList<ChromoMap>();
	
	public MapSet()
	{
		name = "";
	}
		
	public MapSet(String name)
	{
		this.name = name;
	}
	
	/** Allows you to use MapSet in a 1.5 for loop. */
	public Iterator<ChromoMap> iterator()
		{ return maps.iterator(); }
	
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public void addMap(ChromoMap map)
		{ maps.add(map); }
	
	public ChromoMap getMap(int index)
		{ return maps.get(index); }
	
	/**
	 * Searches this MapSet to see if a map exists with the given name. Returns
	 * the ChromoMap if it does, or null if a matching map cannot be found.
	 */
	public ChromoMap getMapByName(String name)
	{
		for (ChromoMap map: maps)
			if (map.getName().equals(name))
				return map;
		
		return null;
	}
	
	/** Returns the number of ChromoMap objects held by this MapSet. */
	public int size()
		{ return maps.size(); }
	
	public void printSummary()
	{
		System.out.println("MapSet Summary:");
		System.out.println("  Number of maps: " + maps.size());
		for (ChromoMap map: maps)
			System.out.println("    Map: " + map.getName() + " with " + map.countFeatures() + " feature(s)");
	}
}