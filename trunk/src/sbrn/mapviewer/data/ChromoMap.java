package sbrn.mapviewer.data;

import java.util.*;

public class ChromoMap implements Iterable<Feature>
{
	private String name;
	private String taxon;
	
	private LinkedList<Feature> features = new LinkedList<Feature>();
	
	private float start, stop;
	
	
	public ChromoMap(String name)
	{
		this.name = name;
	}
	
	// Allows you to use MapSet in a J2SE1.5 for loop:
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
		{ features.add(feature); }
	
	public Feature getFeature(int index)
		{ return features.get(index); }
	
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
}