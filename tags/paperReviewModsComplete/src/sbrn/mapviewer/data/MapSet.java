package sbrn.mapviewer.data;

import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.entities.*;

/**
 * A MapSet holds a collection of chromosome map (ChromoMap) objects.
 */
public class MapSet implements Iterable<ChromoMap>
{
	// The name of this MapSet
	private String name;

	//the annotation URL for this genome
	//must allow appending of feature names to be searched for
	private String URL = null;

	// Holds a list of all the maps in this set
	private final Vector<ChromoMap> maps = new Vector<ChromoMap>();
	
	//the mapset's graphical representation
	public GMapSet gMapSet;

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
	 * Adds another chromosome map to this map set. If the map already exists in
	 * this set, it will not be added again.
	 * @param map the chromosome map to add
	 */
	public void addMap(ChromoMap map)
	{
		if (maps.contains(map) == false)
		{
			maps.add(map);
			map.setOwningMapSet(this);
		}
	}

	/**
	 * Returns true if this map set holds a copy of the given chromosome map.
	 * @param map the map to search for
	 * @return true if this map set holds a copy of the given chromosome map
	 */
	public boolean contains(ChromoMap map)
		{ return maps.contains(map); }

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
		Strudel.logger.fine("MapSet Summary:");
		Strudel.logger.fine("  Number of maps: " + maps.size());
		for (ChromoMap map: maps)
			Strudel.logger.fine("    Map: " + map.getName() + " with " + map.countFeatures() + " feature(s) " + map.getStop());
	}

	/**
	 * Returns a LinkedList which contains the ChromoMap objects held in this mapset
	 */
	public Vector<ChromoMap> getMaps()
	{
		return maps;
	}

	public String getURL()
	{
		return URL;
	}

	public void setURL(String url)
	{
		URL = url;
	}
}