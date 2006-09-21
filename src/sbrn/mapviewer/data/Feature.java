package sbrn.mapviewer.data;

import java.util.*;

/**
 * Class that represents a "feature" on a map. This is rather abstract for now,
 * so a Feature object can literally correspond to whatever "feature" is
 * required for a given task - marker, qtl, annotation, etc.
 */
public class Feature //implements Comparable
{
	// Feature types
	public final static int GENERIC = 0;
	
	public final static int MARKER = 1;
	public final static int SSR = 2;
	public final static int SNP = 3;
	//etc
	
	// Owning ChromoMap for this Feature
	private ChromoMap cMap;
	
	// The name of this Feature
	private String name;
	// And any aliases
	private LinkedList<String> aliases = new LinkedList<String>();
	// And any links its involved in
	private LinkedList<Link> links = new LinkedList<Link>();
	
	// Its start and stop positions (in whatever distance format) on the map
	private float start, stop;
		
	// Feature type
	private int type = GENERIC;
	
	/**
	 * Constructs a new feature with the given name.
	 * @param name the name of this feature
	 */
	public Feature(String name)
	{
		this.name = name;
	}
	
	/**
	 * Returns a string representation of this feature. Currently its name.
	 * @return a string representation of this feature.
	 */
	public String toString()
		{ return name; }
	
	/**
	 * Returns the name of this feature.
	 * @return the name of this feature
	 */
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
	
	/**
	 * Returns a list of every alias that this feature is known by.
	 * @return a list of every alias that this feature is known by
	 */
	public LinkedList<String> getAliases()
		{ return aliases; }
	
	/**
	 * Adds another name alias for this feature.
	 * @param alias the name alias to add
	 */
	public void addAlias(String alias)
		{ aliases.add(alias); }
	
	/**
	 * Returns a reference to the ChromoMap object that contains this feature.
	 * @return a reference to the ChromoMap object that contains this feature.
	 */
	public ChromoMap getOwningMap()
		{ return cMap; }
	
	/**
	 * Sets the owning ChromoMap object for this feature.
	 * @param cMap the new owning ChromoMap object for this feature
	 */
	public void setOwningMap(ChromoMap cMap)
		{ this.cMap = cMap; }
	
	/**
	 * Returns a list of links that correspond to this feature. Each Link will
	 * hold a reference to this feature in either its feature1 or feature2
	 * reference.
	 * @return a list of links that correspond to this feature.
	 */
	public LinkedList<Link> getLinks()
		{ return links; }
	
/*	public int compareTo(Object other)
	{
		Feature f2 = (Feature) other;
		
		if (this.start < f2.start)
			return -1;
		if (this.start > f2.start)
			return 1;
		
		return 0;
	}
*/
}