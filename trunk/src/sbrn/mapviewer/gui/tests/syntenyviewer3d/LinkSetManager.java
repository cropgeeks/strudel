package sbrn.mapviewer.gui.tests.syntenyviewer3d;

import java.util.LinkedList;

import sbrn.mapviewer.data.ChromoMap;
import sbrn.mapviewer.data.Feature;
import sbrn.mapviewer.data.Link;
import sbrn.mapviewer.data.LinkSet;
import sbrn.mapviewer.data.MapSet;

/**
 * Class for creating and managing LinkSets
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class LinkSetManager
{
	
//===================================methods==============================	
	
	/**
	 * Makes a linkset that contains only the links between the central chromosome and a given peripheral one
	 */
	public static LinkSet makeSpecificLinkSet(int iteration, LinkSet centralChromoLinkSet, MapSet referenceMapset, LinkSet [] linkSubsets)
	{
		//first take the reduced linkset (with all the links from the central chromo) and reduce it 
		//further to the links only that are specific to this particular combination of central chromo and peripheral chromo
		LinkSet specSubSet = new LinkSet();
		//iterate over the overall linkset
		//for each link in the set			
		LinkedList <Link> allLinks = centralChromoLinkSet.getLinks();
		for(Link link : allLinks)
		{
			//check wether either of the features is owned by the central chromo
			Feature feature = link.getFeature2();
			//if yes, add it to the subset
			if(feature.getOwningMap().equals(referenceMapset.getMap(iteration)))
			{
				specSubSet.addLink(link);
			}
		}
		
		linkSubsets[iteration] = specSubSet;
		
		return specSubSet;
	}
	
//	 ---------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Returns a linkset that contains all links that the central chromosome is involved in
	 */
	public static LinkSet makeCentralChromoLinkSet(LinkSet globalLinkSet, ChromoMap centralChromo)
	{
		//first make a subset of the overall linkset
		LinkSet linksSubSet = new LinkSet();
		//iterate over the overall linkset
		//for each link in the set			
		LinkedList <Link> allLinks = globalLinkSet.getLinks();
		for(Link link : allLinks)
		{
			//check wether either of the features is owned by the central chromo
			Feature feature = link.getFeature1();
			//if yes, add it to the subset
			if(feature.getOwningMap().equals(centralChromo))
			{
				linksSubSet.addLink(link);
			}
		}	
		return linksSubSet;
	}
	
//	 ---------------------------------------------------------------------------------------------------------------------
	/**
	 *Returns a 2d array that holds two chromosomes in the outer array (central chromo at index0, 
	 *peripheral one at index1)and a list of y positions for each link end point in the inner array
	 */
	public static float [][] calcLinkPositions(boolean invert, LinkSet specSubSet, ChromoMap centralChromo, float cylinderHeight)
	{
		float [][] linkPositions = null;
		
		try
		{
			//this 2d array holds two chromosomes in the outer array (central chromo at index0, peripheral one at index1)
			//and a list of y positions for each link end point in the inner array
			linkPositions= new float[2][specSubSet.getLinks().size()];
			
			// for every link in the subset
			LinkedList <Link> subsetLinks = specSubSet.getLinks();
			int i = 0;
			for(Link link : subsetLinks)
			{
				//the y position of each vertex is a float that is relative to the height of the cylinder, e.g. 
				//{cylinderHeight / 2 - 0.7167057515739517f
				//the position needs to be normalised with respect to the cylinder height as the values passed in are absolutes
				double markerPos1 = 0;
				float yPos1 = 0;
				double markerPos2 = 0;
				float yPos2 = 0;
				
				Feature feat1 = link.getFeature1();
				Feature feat2 = link.getFeature2();
				
				ChromoMap cmap1 = centralChromo;
				ChromoMap cmap2 = feat2.getOwningMap();
				
				float mapEndPoint1 = cmap1.getStop();
				float mapEndPoint2 = cmap2.getStop();

				//work out the y position for this marker on the first chromosome
				markerPos1 = feat1.getStart();
				//now calulate the actual y position 
				//this needs to take into account the map end point values
				yPos1 = cylinderHeight / 2 - (float)((markerPos1/mapEndPoint1));
				linkPositions[0][i] = yPos1;

				//work out the y position for this marker on the second chromosome
				markerPos2 =feat2.getStart();
				//the actual y position 
				//this is the value we need to invert if required
				yPos2 = cylinderHeight / 2 - (float)((markerPos2/mapEndPoint2));
				linkPositions[1][i] = yPos2;
				
				//System.out.println("yPos2 = " + yPos2);
				if(invert)
				{
					//System.out.println("inverting y value");
					//the normalised values range from -0.5 to 0.5
					// to invert the value just change the sign
					yPos2 = (-1)* yPos2;
					linkPositions[1][i] = yPos2;
					//System.out.println("yPos2 after= " + yPos2);
				}
				
				i++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return linkPositions;		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
}//end class
