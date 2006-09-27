package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.data.*;

public class CMapLinkImporter
{
	private File filename;
	private LinkSet linkSet = new LinkSet();
	
	private LinkedList<MapSet> mapSets = new LinkedList<MapSet>();
	
	public CMapLinkImporter(File filename)
	{
		this.filename = filename;
	}
	
	/** Adds a MapSet that (may) contain Features referred to by the data. */
	public void addMapSet(MapSet mapset)
	{
		mapSets.add(mapset);
	}
	
	public LinkSet loadLinkSet()
		throws IOException, ArrayIndexOutOfBoundsException
	{ 
		// We load feature links by scanning the data file. Other than the first
		// line, expect to see one link (pair of features) per line
		
		BufferedReader in = new BufferedReader(new FileReader(filename));		
		in.readLine();
		
		String str = in.readLine();		
		while (str != null && str.length() > 0)
		{
			String[] t = str.split("\\t");
			
			// Find all features with the first name and all with the second
			LinkedList<Feature> f1List = getFeaturesByName(t[0]);
			LinkedList<Feature> f2List = getFeaturesByName(t[1]);
			
			// Pair up every instance of f1 with f2
			for (Feature f1: f1List)
				for (Feature f2: f2List)
				{
					Link link = new Link(f1, f2);
					linkSet.addLink(link);
					
					// We also add the Link to each Feature so the Feature
					// itself knows about the links it has with others
					f1.getLinks().add(link);
					f2.getLinks().add(link);
										
					// TODO: Do we want to add a list of references Features to
					// the Feature object itself, so it knows who it links to?
					// If so, how do we deal with, eg removing MapSets and
					// keeping these lists (and the LinkSet!) up to date.
				}
			
			str = in.readLine();
		}
		
		in.close();
		
		return linkSet;
	}
	
	// Searches over all MapSets to find every feature whose name matches the
	// one given.
	private LinkedList<Feature> getFeaturesByName(String name)
	{
		LinkedList<Feature> list = new LinkedList<Feature>();
		Feature feature = null;
				
		for (MapSet mapset: mapSets)
		{
			for (ChromoMap map: mapset)
			{ 
				// TODO: Should this be a case-insensitive search?
				feature = map.getFeature(name);
				if (feature != null)
				{
					list.add(feature);
					
					// We also need to remember to track the MapSet where
					// this Feature was found
					linkSet.addMapSet(mapset);
				}
			}
		}
		
		return list;
	}
}