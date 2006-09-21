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
	
	private LinkedList<Feature> features = new LinkedList<Feature>();
	private Hashtable<String,Feature> lookup = new Hashtable<String,Feature>();
	
	private float start, stop;
	private float maxStart, maxStop;
	
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
	 * Returns the name of this feature.
	 * @return the name of this feature
	 */
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public String getTaxon()
		{ return taxon; }
	
	public void setTaxon(String taxon)
		{ this.taxon = taxon; }
	
//	public LinkedList<Feature> getFeatures()
//		{ return features; }

	/**
	 * Adds another feature to this chromosome map.
	 * @param feature the feature to add
	 */
	public void addFeature(Feature feature)
	{ 
		features.add(feature);
		lookup.put(feature.getName(), feature);
		
		// Find out if this feature has a greater start/stop position
		if (feature.getStart() >= maxStart)
			maxStart = feature.getStart();
		if (feature.getStop() >= maxStop)
			maxStop = feature.getStop();
		
//		Collections.sort(features);
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
		{ return lookup.get(name); }
	
	/**
	 * Returns the number of features held by this chromosome map.
	 * @return the number of features held by this chromosome map
	 */
	public int countFeatures()
		{ return features.size(); }
	
	public void setStart(float start)
		{ this.start = start; }
	
	public float getStart()
		{ return start; }
	
	/**
	 * Returns the maximum start position (on the chromosome map) where a
	 * feature begins.
	 * @return the maximum start position (on the chromosome map) where a
	 * feature begins
	 */
	public float getMaxStart()
		{ return maxStart; }
	
	public void setStop(float stop)
		{ this.stop = stop; }
	
	public float getStop()
		{ return stop; }
	
	/**
	 * Returns the maximum stop position (on the chromosome map) where a
	 * feature ends.
	 * @return the maximum stop position (on the chromosome map) where a
	 * feature ends
	 */
	public float getMaxStop()
		{ return maxStop; }
	
	/**
	 * Returns true if this chromosome map holds a feature with the given name.
	 * @param name the name of a feature to search for
	 * @return true if this chromosome map holds a feature with the given name
	 */
	public boolean containsFeature(String name)
	{
		return lookup.containsKey(name);
	}
	
	
}