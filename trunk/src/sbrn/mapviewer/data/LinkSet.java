package sbrn.mapviewer.data;

import java.util.*;

/**
 * Holds a list of links between features. The link set does not concern itself
 * with where the features are actually located, but it can (and should) contain
 * a list of one or more MapSet objects that relate to where its feature links
 * are valid.
 */
public class LinkSet implements Iterable<Link>
{
	private Vector<Link> links = new Vector<Link>();
	
	private Vector<MapSet> mapSets = new Vector<MapSet>();
	
	/**
	 * Constructs a new LinkSet.
	 */
	public LinkSet()
	{
	}
	
	/**
	 * Returns a list of all links held by this object.
	 * @return a list of all links held by this object
	 */
	public Vector<Link> getLinks()
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
	public Vector<MapSet> getMapSets()
		{ return mapSets; }
	
	/**
	 * Returns the number of links held by this link set.
	 * @return the number of links held by this link set
	 */
	public int size()
		{ return links.size(); }
	
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
	public LinkSet getLinksBetweenAllMapSets()
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
				// And add them to the link set too
				newSet.addMapSet(link.getFeature1().getOwningMapSet());
				newSet.addMapSet(link.getFeature1().getOwningMapSet());
			}
		}
		
		return newSet;
	}
	
	/**
	 * Returns a new link set that only contains links that originate/terminate
	 * from the given chromosome map. If the map is not held within any of the
	 * map sets referenced by this link set, or if the map does not have any
	 * links, then the returned link set will be empty.
	 * @param map the chromosome map to use
	 * @return a new link set that only contains links that originate/terminate
	 * from the given chromosome map
	 */
	public LinkSet getLinksByMap(ChromoMap map)
	{
		LinkSet newSet = new LinkSet();
		
		for (Link link: links)
		{
			ChromoMap map1 = link.getFeature1().getOwningMap();
			ChromoMap map2 = link.getFeature2().getOwningMap();
			
			if (map1.equals(map) || map2.equals(map))
			{
				newSet.addLink(link);
				newSet.addMapSet(map.getOwningMapSet());
			}
		}
		
		return newSet;
	}
	
	/**
	 * Returns a new link set that only contains links between the two given
	 * chromosome maps. If either map is not held within any of the map sets
	 * referenced by this link set, or if a map does not have any links, then
	 * the returned link set will be empty.
	 * @param map1 the first chromosome map to use
	 * @param map2 the second chromosome map to use
	 * @return a new link set that only contains links between the two given
	 * chromosome maps
	 */
	public LinkSet getLinksBetweenMaps(ChromoMap map1, ChromoMap map2)
	{
		LinkSet newSet = new LinkSet();
		
		for (Link link: links)
		{
			ChromoMap owner1 = link.getFeature1().getOwningMap();
			ChromoMap owner2 = link.getFeature2().getOwningMap();
			
			if ((map1.equals(owner1) || map1.equals(owner2)) &&
				(map2.equals(owner1) || map2.equals(owner2)))
			{
				newSet.addLink(link);
				newSet.addMapSet(owner1.getOwningMapSet());
				newSet.addMapSet(owner2.getOwningMapSet());
			}
		}
		
		return newSet;
	}
	
	/**
	 * Returns a new link set that only contains links between the two given
	 * map sets.
	 * @param mapset1 the first map set to use
	 * @param mapset2 the second map set to use
	 * @return a new link set that only contains links between the two given
	 * map sets.
	 */
	public LinkSet getLinksBetweenMapSets(MapSet mapset1, MapSet mapset2)
	{
		LinkSet newSet = new LinkSet();
		
		for (Link link: links)
		{
			ChromoMap map1 = link.getFeature1().getOwningMap();
			MapSet owner1 = map1.getOwningMapSet();
			ChromoMap map2 = link.getFeature2().getOwningMap();
			MapSet owner2 = map2.getOwningMapSet();			
			
			if ((mapset1.equals(owner1) || mapset1.equals(owner2)) &&
				(mapset2.equals(owner1) || mapset2.equals(owner2)))
			{
				newSet.addLink(link);
				newSet.addMapSet(owner1);
				newSet.addMapSet(owner2);
			}
		}
		
		return newSet;
	}
	
	/**
	 * Returns a new link set that only contains links between the given map and
	 * map set.
	 * @param map the map to use
	 * @param mapset the mapset to use
	 * @return a new link set that only contains links between the given map and
	 * map set
	 */ 
	public LinkSet getLinksBetweenMapandMapSet(ChromoMap map, MapSet mapset)
	{
		LinkSet newSet = new LinkSet();
		
		for (Link link: links)
		{
			ChromoMap map1 = link.getFeature1().getOwningMap();
			MapSet owner1 = map1.getOwningMapSet();
			ChromoMap map2 = link.getFeature2().getOwningMap();
			MapSet owner2 = map2.getOwningMapSet();			
			
			if ((mapset.equals(owner1) || mapset.equals(owner2)) &&
				(map.equals(map1) || map.equals(map2)))
			{
				newSet.addLink(link);
				newSet.addMapSet(owner1);
				newSet.addMapSet(owner2);
			}
		}
		
		return newSet;
	}
	
	/**
	 * Adds another map set to this link set.
	 * @param mapset the map set to add
	 */
	private void addMapSet(MapSet mapset)
	{
		if (mapSets.contains(mapset) == false)
		{
			mapSets.add(mapset);
		}
	}
	
	/**
	 * Adds another link to this link set.
	 * @param link the link to add
	 */
	public void addLink(Link link)
	{
		links.add(link);
		
		// When we add a link, make sure we track the MapSets too
		addMapSet(link.getFeature1().getOwningMap().getOwningMapSet());
		addMapSet(link.getFeature2().getOwningMap().getOwningMapSet());
	}
	
	/**
	 * Returns a string representation of this link set. The string is formatted
	 * to return details on each link; one per line.
	 * @return a string representation of this link set
	 */
//	public String toString()
//	{
//		StringBuffer str = new StringBuffer();
//		
//		for (Link link: links)
//		{
//			str.append("Link between " + link.getFeature1() + " and "
//				+ link.getFeature2() + "\n");
//		}
//		
//		return str.toString();
//	}
	
	/**
	 * Adds all the links from the Linkset passed in to the current Linkset.
	 * @param linkset -- the linkset to combine with is one
	 */
	public void combineWithLinkSet(LinkSet linkSet)
	{
		links.addAll(linkSet.getLinks());
	}
	
}