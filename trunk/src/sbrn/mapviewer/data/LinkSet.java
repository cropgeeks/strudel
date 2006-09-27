package sbrn.mapviewer.data;

import java.util.*;

/**
 * Holds a list of links between features. The link set does not concern itself
 * with where the features are actually located, but it can contain a list of
 * one or more MapSet objects that relate to where its feature links are valid.
 */
public class LinkSet implements Iterable<Link>
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
	 * Allows you to use LinkSet in a 1.5 for loop.
	 */
	public Iterator<Link> iterator()
		{ return links.iterator(); }
	
	/**
	 * Returns the list of map sets held by this object.
	 * @return the list of map sets held by this object
	 */
	public LinkedList<MapSet> getMapSets()
		{ return mapSets; }
	
	/**
	 * Returns true if the given link represents a link between two features
	 * held by maps that are contained within the same map set.
	 * @param link the link to query
	 * @return true if the given link represents a link between two features
	 * held by maps that are contained within the same map set
	 */
	public boolean isLinkUniqueToMapSet(Link link)
	{
		// Need to find the owning ChromoMap for the two features in this link
		// and then search the known MapSets to see if they exist in the same
		// one or not
		ChromoMap map1 = link.getFeature1().getOwningMap();
		ChromoMap map2 = link.getFeature2().getOwningMap();
		
		// Search for a case where a MapSet holds both the ChromoMaps
		for (MapSet mapset: mapSets)
			if (mapset.contains(map1) && mapset.contains(map2))
				return true;
		
		// If it wasn't found, then we can assume the Link is between MapSets
		return false;
	}
	
	/**
	 * Returns a new link set that only contains links that join objects located
	 * in different map sets. Links between objects in the same map set will be
	 * ignored and not added to the new set. If the original link set only
	 * contains a single map set, the new link set object will be empty.
	 */
	public LinkSet getBetweenMapSetLinks()
	{
		LinkSet newSet = new LinkSet();
		
		// If there is only 0 or 1 MapSet, then we can't have any between map
		// links
		if (mapSets.size() < 2)
			return newSet;
		
		for (Link link: links)
		{
			// If the link spans map sets, then we want to add it
			if (isLinkUniqueToMapSet(link) == false)
			{
				newSet.addLink(link);
				
				// But we also need to find the map sets involved
				ChromoMap map1 = link.getFeature1().getOwningMap();
				ChromoMap map2 = link.getFeature2().getOwningMap();
				
				// And add them to the link set too
				newSet.addMapSet(getMapSet(map1));
				newSet.addMapSet(getMapSet(map2));
			}
		}
		
		return newSet;
	}
	
	// Returns the map set that holds the given map
	private MapSet getMapSet(ChromoMap map)
	{
		for (MapSet mapset: mapSets)
			if (mapset.contains(map))
				return mapset;
		
		return null;
	}
	
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