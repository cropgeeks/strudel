package sbrn.mapviewer.data;

import java.util.*;

/**
 * Holds a list of links between features. The link set does not concern itself
 * with where the features are actually located, but it can contain a list of
 * one or more MapSet objects that relate to where its feature links are valid.
 */
public class LinkSet
{
	private LinkedList<Link> links = new LinkedList<Link>();
	
	private LinkedList<MapSet> mapSets = new LinkedList<MapSet>();
	
	/**
	 * Constructs a new LinkSet.
	 */
	public LinkSet()
	{
	}
	
	/**
	 * Returns the list of links held by this object.
	 * @return the list of links held by this object
	 */
	public LinkedList<Link> getLinks()
		{ return links; }
	
	/**
	 * Returns the list of map sets held by this object.
	 * @return the list of map sets held by this object
	 */
	public LinkedList<MapSet> getMapSets()
		{ return mapSets; }
	
	/**
	 * Adds another map set to this link set.
	 * @param mapset the map set to add
	 */
	public void addMapSet(MapSet mapset)
	{
		if (mapSets.contains(mapset) == false)
			mapSets.add(mapset);
	}
	
	/**
	 * Adds another link to this link set.
	 * @param link the link to add
	 */
	public void addLink(Link link)
	{
		links.add(link);
	}
	
	/**
	 * Returns a string representation of this link set. The string is formatted
	 * to return details on each link; one per line.
	 * @return a string representation of this link set
	 */
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