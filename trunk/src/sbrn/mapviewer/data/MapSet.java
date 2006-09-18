package sbrn.mapviewer.data;

import java.util.*;

/**
 * A MapSet holds a collection of chromosome map (ChromoMap) objects.
 */
public class MapSet implements Iterable<ChromoMap>
{
	// The name of this MapSet
	private String name;
	
	// Holds a list of all the maps in this set
	private LinkedList<ChromoMap> maps = new LinkedList<ChromoMap>();
	
	/**
	 * Constructs a new map set.
	 */
	public MapSet()
	{
		name = "";
	}
	
	/**
	 * Constructs a new map set with the given name.
	 * @param name the name of this map set.
	 */	
	public MapSet(String name)
	{
		this.name = name;
	}
	
	/** Allows you to use MapSet in a 1.5 for loop. */
	public Iterator<ChromoMap> iterator()
		{ return maps.iterator(); }
	
	/**
	 * Returns the name of this feature.
	 * @return the name of this feature
	 */
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	/**
	 * Adds another chromosome map to this map set.
	 * @param map the chromosome map to add
	 */
	public void addMap(ChromoMap map)
		{ maps.add(map); }
	
	/**
	 * Returns the chromosome map held at the given index position.
	 * @return the chromosome map held at the given index position
	 */
	public ChromoMap getMap(int index)
		{ return maps.get(index); }
	
	/**
	 * Returns the first instance of a chromosome map whose name matches that
	 * given.
	 * @param name the name of the chromosome map to search for
	 * @return the first instance of a chromosome map whose name matches that
	 * given, or null if one cannot be found 
	 */
	public ChromoMap getMapByName(String name)
	{
		for (ChromoMap map: maps)
			if (map.getName().equals(name))
				return map;
		
		return null;
	}
	
	/**
	 * Returns the number of chromosome maps held in this map set.
	 * @return the number of chromosome maps held in this map set
	 */
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