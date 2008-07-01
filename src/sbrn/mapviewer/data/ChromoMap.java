package sbrn.mapviewer.data;

import java.util.*;

/**
 * A ChromoMap is an object designed to hold LinkageMap/Chromosome data, which
 * in this case is a list of features (markers, qtl positions, etc). Each
 * feature holds an internal start and stop position - there is no guarantee
 * that the order the features are held in by a ChromoMap will correspond to
 * their start/stop positions.
 */
public class ChromoMap implements Iterable<Feature>
{
	private String name;
	private String taxon;
	
	// A reference to the MapSet that owns this object
	private MapSet owner;
	
	private LinkedList<Feature> features = new LinkedList<Feature>();
	private Hashtable<String,Feature> nameLookup = new Hashtable<String,Feature>();
	
	private float start = Integer.MAX_VALUE, stop = Integer.MIN_VALUE;
	
	/**
	 * Constructs a new chromosome map with the given name.
	 * @param name the name of this chromosome map
	 */
	public ChromoMap(String name)
	{
		this.name = name;
	}
	
	/**
	 * Allows you to use ChromoMap in a 1.5 for loop. Will be slow for large
	 * maps - if you know the feature you're after - do a search by name instead
	 * with the getFeature(String) method.
	 * @see sbrn.mapviewer.data.ChromoMap#getFeature(String) getFeature(String) 
	 */
	public Iterator<Feature> iterator()
		{ return features.iterator(); }
	
	/**
	 * Returns a string representation of this chromosome map. Currently its
	 * name.
	 * @return a string representation of this chromosome map.
	 */
	public String toString()
		{ return name; }
	
	/**
	 * Returns the name of this map.
	 * @return the name of this map
	 */
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public String getTaxon()
		{ return taxon; }
	
	public void setTaxon(String taxon)
		{ this.taxon = taxon; }
	
	/**
	 * Returns a reference to the MapSet object that contains this map.
	 * @return a reference to the MapSet object that contains this map, or null
	 * if this map is not part of a set
	 */
	public MapSet getOwningMapSet()
		{ return owner; }

	/**
	 * Sets the owning MapSet object for this map.
	 * @param owner the new owning MapSet object for this map
	 */
	void setOwningMapSet(MapSet owner)
		{ this.owner = owner; }
	
//	public LinkedList<Feature> getFeatures()
//		{ return features; }

	/**
	 * Adds another feature to this chromosome map.
	 * @param feature the feature to add
	 */
	public void addFeature(Feature feature)
	{ 
		features.add(feature);
		nameLookup.put(feature.getName(), feature);
		
		// Find out if this feature has a greater start/stop position
		if (feature.getStart() <= start)
			start = feature.getStart();
		if (feature.getStop() >= stop)
			stop = feature.getStop();
		
//		Collections.sort(features);

		// Also let the feature track the map
		feature.setOwningMap(this);
	}
	
	/**
	 * Returns the feature at the given index position.
	 * @return the feature at the given index position
	 */
	public Feature getFeature(int index)
		{ return features.get(index); }
	
	/**
	 * Returns the feature with the given name.
	 * @return the feature with the given name, or null if it is not found
	 */
	public Feature getFeature(String name)
		{ return nameLookup.get(name); }
	
	/**
	 * Returns the number of features held by this chromosome map.
	 * @return the number of features held by this chromosome map
	 */
	public int countFeatures()
		{ return features.size(); }
	
	public void setStart(float start)
		{ this.start = start; }
	
	/**
	 * Returns the position on the map of the lowest numerical starting
	 * position for a feature.
	 * @return the position on the map of the lowest numerical starting
	 * position for a feature
	 */
	public float getStart()
		{ return start; }
	
	public void setStop(float stop)
		{ this.stop = stop; }
	
	/**
	 * Returns the position on the map of the highest numerical stop position
	 * for a feature.
	 * @return the position on the map of the highest numerical stop position
	 * for a feature
	 */
	public float getStop()
		{ return stop; }
	
	/**
	 * Returns true if this chromosome map holds a feature with the given name.
	 * @param name the name of a feature to search for
	 * @return true if this chromosome map holds a feature with the given name
	 */
	public boolean containsFeature(String name)
	{
		return nameLookup.containsKey(name);
	}
	
	public LinkedList<Feature> getFeatureList()
	{
		return this.features;
	}
	
}