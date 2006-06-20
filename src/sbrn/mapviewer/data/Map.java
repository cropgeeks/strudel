package sbrn.mapviewer.data;

import java.util.*;

public class Map implements Iterable
{
	private String name;
	private String taxon;
	
	private LinkedList<Feature> features = new LinkedList<Feature>();
	
	private int start, stop;
	
	
	public Map(String name)
	{
		this.name = name;
	}
	
	// Allows you to use MapSet in a J2SE1.5 for loop:
	// for (Map map: myMapSet) {}
	public Iterator<Feature> iterator()
		{ return features.iterator(); }
	
	public String getName()
		{ return name; }
	
	public void setName(String name)
		{ this.name = name; }
	
	public LinkedList<Feature> getFeatures()
		{ return features; }
	
	public Feature getFeature(int index)
		{ return features.get(index); }
	
	public int countFeatures()
		{ return features.size(); }
}