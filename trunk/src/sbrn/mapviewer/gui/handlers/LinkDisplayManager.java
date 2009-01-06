package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;

public class LinkDisplayManager
{
	
//	====================================vars===============================================
	
	MainCanvas mainCanvas;
	
	// a hashtable that contains chromomaps from both genomes as keys and Vector objects as values, which in
	// turn hold a list of LinkSet objects each, where each Linkset represents the links between the chromomap and a
	// chromomap in the respectively other genome
	Hashtable<ChromoMap, Vector<LinkSet>> linkSetLookup;
	
	Hashtable<ChromoMap, LinkSet> allLinksLookup;
	
	public static double blastThreshold = 1;
	
//	=====================================c'tor==============================================
	
	public LinkDisplayManager(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
		makeTargetLinkSubSets();
		precomputeAllLinks();
	}
	
//	=====================================methods===========================================
	
	// display the homologies between chromosomes as lines
	public void processLinkDisplayRequest(int x, int y, boolean isCtrlClickSelection)
	{
		// first figure out which chromosome we are in
		GChromoMap selectedMap = Utils.getSelectedMap(mainCanvas.gMapSetList, x, y);
		
		// the click has hit a chromosome
		if (selectedMap != null)
		{
			// single click with Ctrl down -- user wants to select individual maps
			// in that case we just add or remove maps to the vector of selected maps as requested
			if (isCtrlClickSelection)
			{
				// if the map is already added we need to remove it (this is toggle-style functionality)
				if (selectedMap.owningSet.selectedMaps.contains(selectedMap))
				{
					selectedMap.owningSet.removeSelectedMap(selectedMap);
				}
				// otherwise we add it
				else
				{
					selectedMap.owningSet.addSelectedMap(selectedMap);
				}
			}
			// this is just a normal single click -- user wants to do overviews of individual target chromosomes, one at a time
			else
			{
				// only do this if the selected map belongs to the target genome
				// if the single click was on a reference chromo we don't want any action taken
				if (selectedMap.owningSet.isTargetGenome)
				{
					// in that case we first clear out the existing vector of selected maps in the target genome
					selectedMap.owningSet.deselectAllMaps();
					
					// then we add the selected map only
					selectedMap.owningSet.addSelectedMap(selectedMap);
					
					// now add ALL maps into the vector of selected elements for the reference genome so the links can be drawn
					for (GMapSet referenceGMapSet : mainCanvas.referenceGMapSets)
					{
						referenceGMapSet.selectAllMaps();
					}
					mainCanvas.drawLinks = true;
				}
			}
			
			// now check whether we have selected chromosomes in the target genome
			if (mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.size() > 0)
			{
				mainCanvas.drawLinks = true;
			}
			// if not, we don't want to draw links, just display the selected outlines of the reference genome chromsomes
			else
			{
				mainCanvas.drawLinks = false;
			}
			
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	public void drawAllLinks(Graphics2D g2)
	{
		try
		{
			
			// for each map in the selectedMaps vector of the target genome
			for (int i = 0; i < mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.size(); i++)
			{
				// get the currently selected map
				GChromoMap selectedMap = mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.get(i);
				
				// get the ChromoMap for the currently selected chromosome
				ChromoMap selectedChromoMap = selectedMap.chromoMap;
				
				// get all the links between the selected chromosome and the reference mapset
				Vector<LinkSet> linkSets = linkSetLookup.get(selectedChromoMap);
				
				float targetMapStop = selectedChromoMap.getStop();
				
				// for all selected links
				for (LinkSet selectedLinks : linkSets)
				{
					
					//find out which reference mapset we are dealing with here
					GMapSet referenceGMapSet = null;
					for (GMapSet gMapSet : mainCanvas.referenceGMapSets)
					{
						if(gMapSet.mapSet == selectedLinks.getMapSets().get(1))
							referenceGMapSet = gMapSet;
					}
					
					// get the real coordinates for the selected chromo and the reference chromo
					int selectedChromoY = selectedMap.y + selectedMap.currentY;
					// the x coordinates have to be worked out
					int targetChromoX = -1;
					int referenceChromoX = -1;
					
					// if we have only one reference genome we want the targetChromoX to be that chromo'sx plus its width and
					// the referenceChromoX to be the reference chromo's x
					if (mainCanvas.referenceGMapSets.size() == 1 || (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 1))
					{
						targetChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition + mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).gMaps.get(0).width);
						referenceChromoX = Math.round(referenceGMapSet.xPosition);
					}
					// if we have two reference genomes and this is the first reference genome
					else if (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 0)
					{
						// we want the referenceChromoX to be the mapsets x plus its width
						referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width);
						// and we want the target genome's x to be the targetChromoX
						targetChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition);
					}
					
					// check whether this is a linkset we want to draw
					// this depends on which chromosome in the reference genome it points to
					// the linksets are ordered by chromosome index
					// check whether this index matches one of the ones in the vector of selected maps in the reference genome
					boolean draw = false;
					for (GChromoMap gMap : referenceGMapSet.selectedMaps)
					{
						if (gMap.isShowingOnCanvas)
							draw = true;
					}
					
					if (draw)
					{
						// set the colour
						g2.setColor(Colors.linkColour);
						
						// for each link in the linkset
						for (Link link : selectedLinks)
						{
							// we only want to draw this link if it has a BLAST e-value smaller than the cut-off currently selected by the user
							if (link.getBlastScore() <= blastThreshold)
							{
								// get the positional data of feature1 (which is on the selected chromo) and the end point of the map
								float feat1Start = link.getFeature1().getStart();
								
								// get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
								float feat2Start = link.getFeature2().getStart();
								ChromoMap referenceCMap = link.getFeature2().getOwningMap();
								float referenceMapStop = referenceCMap.getStop();
								int refChromoIndex = referenceCMap.getOwningMapSet().getMaps().indexOf(
												referenceCMap);
								GChromoMap referenceGMap = referenceGMapSet.gMaps.get(refChromoIndex);
								int referenceChromoY = referenceGMap.y + referenceGMap.currentY;
								
								GChromoMap targetGMap = link.getFeature1().getOwningMap().getGChromoMap();
								
								// convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
								int targetY = (int) (feat1Start / (targetMapStop / selectedMap.height)) + selectedChromoY;
								int referenceY = (int) (feat2Start / (referenceMapStop / referenceGMap.height)) + referenceChromoY;
								
								//check for chromosome inversion and invert values if necessary
								if(targetGMap.isPartlyInverted)
								{
									targetY = (int) ((targetMapStop - feat1Start) / (targetMapStop / selectedMap.height)) + selectedChromoY;
								}
								if(referenceGMap.isPartlyInverted)
								{
									referenceY = (int) ((referenceMapStop - feat2Start) / (referenceMapStop / referenceGMap.height)) + referenceChromoY;
								}
								
								//this next condition ensures we only draw links to reference features that are showing on the canvas
								if ((referenceY > 0  && referenceY < mainCanvas.getHeight()) &&
												(targetY > 0  && targetY < mainCanvas.getHeight()) &&
												referenceGMapSet.selectedMaps.contains(referenceGMap))
								{
									// draw the line
									g2.drawLine(targetChromoX + 1, targetY, referenceChromoX - 1, referenceY);
								}
							}
						}
					}
				}
			}
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawHighlightedLink(Graphics2D g2, Link link, Color linkColour, boolean thickerLine)
	{
		// we only want to draw this link if it has a BLAST e-value smaller than the cut-off currently selected by the user
		if (link.getBlastScore() <= blastThreshold)
		{
			//get the features of this link
			Feature f1 = link.getFeature1();
			Feature f2 = link.getFeature2();
			
			//get the owning GChromoMap objects associated with this link
			GChromoMap gMap1 = f1.getOwningMap().getGChromoMap();
			GChromoMap gMap2 = f2.getOwningMap().getGChromoMap();
			
			//get the respective GMapSets they belong to and figure out whether they are target or reference gmapsets
			GMapSet referenceGMapSet, targetGMapSet;
			if(gMap1.owningSet.isTargetGenome)
			{
				targetGMapSet = gMap1.owningSet;
				referenceGMapSet = gMap2.owningSet;
			}
			else
			{
				targetGMapSet = gMap2.owningSet;
				referenceGMapSet = gMap1.owningSet;
			}
			
			//work out the x and y coords for drawing the link
			int targetChromoX = -1;
			int referenceChromoX = -1;
			
			// if we have only one reference genome we want the targetChromoX to be that chromo'sx plus its width and
			// the referenceChromoX to be the reference chromo's x
			if (mainCanvas.referenceGMapSets.size() == 1 || (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 1))
			{
				targetChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition + mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).gMaps.get(0).width);
				referenceChromoX = Math.round(referenceGMapSet.xPosition);
			}
			
			// if we have two reference genomes and this is the first reference genome
			else if (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 0)
			{
				// we want the referenceChromoX to be the mapsets x plus its width
				referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width);
				// and we want the target genome's x to be the targetChromoX
				targetChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition);
			}
			
			//y coords
			int y1 = Math.round(gMap1.y + gMap1.currentY + (f1.getStart() * (gMap1.height / gMap1.chromoMap.getStop())));
			int y2 = Math.round(gMap2.y + gMap2.currentY + (f2.getStart() * (gMap2.height / gMap2.chromoMap.getStop())));
			
			//check for chromosome inversion and invert values if necessary
			if(gMap1.isPartlyInverted)
			{
				y1 = (int) ((gMap1.chromoMap.getStop() - f1.getStart()) / (gMap1.chromoMap.getStop() / gMap1.height)) + (gMap1.y + gMap1.currentY);
			}
			if(gMap2.isPartlyInverted)
			{
				y2 = (int) ((gMap2.chromoMap.getStop() - f2.getStart()) / (gMap2.chromoMap.getStop() / gMap2.height)) + (gMap2.y + gMap2.currentY);
			}
			
			// draw the link
			g2.setColor(linkColour);
//			if(thickerLine)
//				g2.setStroke(new BasicStroke(2.0f));
//			else
//				g2.setStroke(new BasicStroke());
			g2.drawLine(targetChromoX, y1, referenceChromoX, y2);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	
	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	public void drawSingleHighlightedLink(Graphics2D g2)
	{
		
		// for all  links between the target genome and all reference genomes
		for (LinkSet selectedLinks : mainCanvas.linkSets)
		{
			// for each link in the linkset
			for (Link link : selectedLinks)
			{
				//get the features of this link
				Feature f1 = link.getFeature1();
				Feature f2 = link.getFeature2();
				
				Vector<Feature> foundFeatures = MapViewer.winMain.fatController.foundFeatures;
				Vector<Feature> foundFeatureHomologs = MapViewer.winMain.fatController.foundFeatureHomologs;
				Color linkColour = Colors.strongEmphasisLinkColour;
				
				//check whether either of the features for this link are included in the found features list of their maps
				if((foundFeatures.contains(f1) && foundFeatureHomologs.contains(f2)) ||
								(foundFeatures.contains(f2) && foundFeatureHomologs.contains(f1)))
				{
					//draw the link
					drawHighlightedLink(g2, link,linkColour, true);				
				}
			}
		}
		
		//draw labels for the feature and its homologs
		LabelDisplayManager.drawHighlightedFeatureLabels(g2);
	}
	
	
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	
	// Draws the lines between features in a certain range on a chromosome of the target genome and all potential homologues in the compared genome
	public void drawHighlightedLinksInRange(Graphics2D g2)
	{		
		// for all  links between the target genome and all reference genomes
		for (LinkSet selectedLinks : mainCanvas.linkSets)
		{
			// for each link in the linkset
			for (Link link : selectedLinks)
			{
				//get the features of this link
				Feature f1 = link.getFeature1();
				Feature f2 = link.getFeature2();
				
				Vector<Feature> featuresInRange = MapViewer.winMain.fatController.featuresInRange;
				
				Color linkColour = Colors.mildEmphasisLinkColour;
				
				//check whether either of the features for this link are included in the found features list of their maps
				if(featuresInRange.contains(f1) ||	featuresInRange.contains(f2))
				{
					//draw the link
					drawHighlightedLink(g2, link,linkColour, false);	
				}
			}
		}
	}
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method precomputes subsets of links between each target chromosome and each reference genome so that drawing them is quicker.
	 */
	private void makeTargetLinkSubSets()
	{
		try
		{
			linkSetLookup = new Hashtable<ChromoMap, Vector<LinkSet>>();
			
			//we need to do this for all linksets between the target and each of the reference genomes
			for (LinkSet links : mainCanvas.linkSets)
			{
				MapSet targetMapSet = links.getMapSets().get(0);
				MapSet referenceMapSet = links.getMapSets().get(1);
				
				// for each chromosome in the target mapset
				for (ChromoMap targetMap : targetMapSet)
				{
					Vector<LinkSet> linkSets = null;
					
					//check whether this target map already has a vector of linksets in the lookup table
					if(linkSetLookup.get(targetMap) == null)
					{
						//if it doesn't exist , create it here
						// create a new Vector which holds all the linksets of links between this chromosome and the reference chromosomes
						linkSets = new Vector<LinkSet>();
						//add the vector as the value for this targetMap in the lookup table
						// then add the list to the hashtable
						linkSetLookup.put(targetMap, linkSets);
					}
					else // it already exists, just retrieve it
					{
						linkSets = linkSetLookup.get(targetMap);
					}
					
					// for each reference chromosome
					for (ChromoMap refMap : referenceMapSet)
					{
						
						// make a linkset that contains only the links between this chromo and the target chromo
						LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
						
						// add the linkset to the list but only if it has links in it
						if(linkSubset.getLinks().size() > 0)
							linkSets.add(linkSubset);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method precomputes subsets of links between each target chromosome and each reference genome so that drawing them is quicker.
	 */
	private void precomputeAllLinks()
	{
		try
		{
			allLinksLookup = new Hashtable<ChromoMap, LinkSet>();
			
			//we need to do this for all linksets between the target and each of the reference genomes
			for (LinkSet links : mainCanvas.linkSets)
			{
				MapSet targetMapSet = links.getMapSets().get(0);
				MapSet referenceMapSet = links.getMapSets().get(1);
				
				// for each chromosome in the target mapset
				for (ChromoMap targetMap : targetMapSet)
				{
					LinkSet linkSet = null;
					
					//check whether this target map already has a linkset in the lookup table
					if(allLinksLookup.get(targetMap) == null)
					{
						//if it doesn't exist , create it here
						// create a  single linkset of links between this chromosome and the reference chromosomes
						linkSet = new LinkSet();
						//add the linkset as the value for this targetMap in the lookup table
						allLinksLookup.put(targetMap, linkSet);
					}
					else // it already exists, just retrieve it
					{
						linkSet = allLinksLookup.get(targetMap);
					}
					
					// for each reference chromosome
					for (ChromoMap refMap : referenceMapSet)
					{						
						// make a linkset that contains only the links between this chromo and the target chromo
						LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
						
						// add the linkset to the list but only if it has links in it
						if(linkSubset.getLinks().size() > 0)
							linkSet.combineWithLinkSet(linkSubset);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	
}// end class
