package sbrn.mapviewer.data;

import java.util.*;

/**
 * Holds a list of Links between Features. The LinkSet does not concern itself
 * with where the features are actually located, but it can contain a list of
 * one or more MapSet objects that relate to where its feature links are valid.
 */
public class LinkSet
{
	private LinkedList<Link> links = new LinkedList<Link>();
	
	private LinkedList<MapSet> mapSets = new LinkedList<MapSet>();
	
	public LinkSet()
	{
	}
	
	public LinkedList<Link> getLinks()
		{ return links; }
	
	public LinkedList<MapSet> getMapSets()
		{ return mapSets; }
	
	/** Adds another MapSet to this object's collection of sets. */
	public void addMapSet(MapSet mapset)
	{
		if (mapSets.contains(mapset) == false)
			mapSets.add(mapset);
	}
	
	public void addLink(Link link)
	{
		links.add(link);
	}
	
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
		for (Link link: links)
		{
			str.append("Link between " + link.getFeature1() + " and "
				+ link.getFeature2() + "\n");
		}
		
		return str.toString();
	}
}