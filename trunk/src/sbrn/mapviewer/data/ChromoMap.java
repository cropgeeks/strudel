package sbrn.mapviewer.data;

import java.util.*;

public class ChromoMap implements Iterable<Feature>
{
	private String name;
	private String taxon;
	
	private LinkedList<Feature> features = new LinkedList<Feature>();
	private Hashtable<String,Feature> lookup = new Hashtable<String,Feature>();
	
	private float start, stop;
	
	
	public ChromoMap(String name)
	{
		this.name = name;
	}
	
	// Allows you to use MapSet in a J2SE1.5 for loop. Will be slow for large
	// maps - if you know the feature you're after - do a search by name instead
	// with the getFeature(String) method.
	// for (Feature feature: myCMap) {}
	public Iterator<Feature> iterator()
		{ return features.iterator(); }
	
	public String toString()
		{ return name; }
	
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

	public void addFeature(Feature feature)
	{ 
		features.add(feature);
		lookup.put(feature.getName(), feature);
	}
	
	/** Returns the Feature at the given index position. */
	public Feature getFeature(int index)
		{ return features.get(index); }
	
	/** Returns the Feature with the given name or null if it is not found. */
	public Feature getFeature(String name)
		{ return lookup.get(name); }
	
	public int countFeatures()
		{ return features.size(); }
	
	public void setStart(float start)
		{ this.start = start; }
	
	public float getStart()
		{ return start; }
	
	public void setStop(float stop)
		{ this.stop = stop; }
	
	public float getStop()
		{ return stop; }
	
	public boolean containsFeature(String name)
	{
		return lookup.containsKey(name);
	}
	
	
}