package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class LinkDisplayManager
{
	
//====================================vars===============================================	
	
	MainCanvas mainCanvas;
	
//=====================================c'tor==============================================	
	
	public LinkDisplayManager(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
		makeTargetLinkSubSets();
	}
	
//=====================================methods===========================================
	
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
					mainCanvas.linksToDraw = true;
				}
			}
			
			// now check whether we have selected chromosomes in the target genome
			if (mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.size() > 0)
			{
				mainCanvas.linksToDraw = true;
			}
			// if not, we don't want to draw links, just display the selected outlines of the reference genome chromsomes
			else
			{
				mainCanvas.linksToDraw = false;
			}
			
		}
		// no hit detected
		else
		{
			// don't draw links
			mainCanvas.linksToDraw = false;
			
			// reset the selectedMaps vectors in all genomes -- this removes the highlight frames from the chromosomes
			for (GMapSet mapSet : mainCanvas.gMapSetList)
			{
				mapSet.deselectAllMaps();
			}
		}
		
		mainCanvas.repaint();		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	public void drawLinks(Graphics2D g2)
	{
		// check whether we have selected chromosomes in the target genome
		// if not, we do not want to draw any links at all
		if (mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.size() > 0)
		{
			// for each map in the selectedMaps vector of the target genome
			for (int i = 0; i < mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.size(); i++)
			{			
				// get the currently selected map
				GChromoMap selectedMap = mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).selectedMaps.get(i);
				
				// get the ChromoMap for the currently selected chromosome
				ChromoMap selectedChromoMap = selectedMap.chromoMap;
				
				// for each of the reference genomes
				for (GMapSet referenceGMapSet : mainCanvas.referenceGMapSets)
				{
					System.out.println("drawing links for reference map set " + referenceGMapSet.name);
					
//					int referenceMapSetIndex = mainCanvas.gMapSetList.indexOf(referenceGMapSet);

					// get all the links between the selected chromosome and the reference mapset
					Vector<LinkSet> linkSets = mainCanvas.linkSetLookup.get(selectedChromoMap);
					
					System.out.println("linkSets.size() in LDM  = " + linkSets.size());
					
					float targetMapStop = selectedChromoMap.getStop();
					
					// get the real coordinates for the selected chromo and the reference chromo
					int selectedChromoY = selectedMap.y;
					// the x coordinates have to be worked out
					int selectedChromoX = -1;
					int referenceChromoX = -1;
					// if we have only one reference genome we want the selectedChromoX to be that chromo'sx plus its width and
					// the referenceChromoX to be the reference chromo's x
					if (mainCanvas.referenceGMapSets.size() == 1 || (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 1))
					{
						selectedChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition + mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).gMaps.get(0).width);
						referenceChromoX = Math.round(referenceGMapSet.xPosition);
					}
					// if we have two reference genomes and this is the first reference genome
					else if (mainCanvas.referenceGMapSets.size() == 2 && mainCanvas.referenceGMapSets.indexOf(referenceGMapSet) == 0)
					{
						// we want the referenceChromoX to be the mapsets x plus its width
						referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width);
						// and we want the target genome's x to be the selectedChromoX
						selectedChromoX = Math.round(mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).xPosition);
					}
					
					// for all selected links
					for (LinkSet selectedLinks : linkSets)
					{
//						System.out.println("linkset is between " + selectedLinks.getMapSets().get(0).getName() + 
//										" and " + selectedLinks.getMapSets().get(1).getName());
						
						// check whether this is a linkset we want to draw
						// this depends on which chromosome in the reference genome it points to
						// the linksets are ordered by chromosome index
						int linkSetIndex = linkSets.indexOf(selectedLinks);
						
						// check whether this index matches one of the ones in the vector of selected maps in the reference genome
						boolean draw = false;
						for (GChromoMap gMap : referenceGMapSet.selectedMaps)
						{
							if (gMap.index == linkSetIndex && gMap.isShowingOnCanvas)
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
								if (link.getBlastScore() <= mainCanvas.winMain.controlPanel.blastThreshold)
								{								
									// get the positional data of feature1 (which is on the selected chromo) and the end point of the map
									float feat1Start = link.getFeature1().getStart();
									
									// get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
									float feat2Start = link.getFeature2().getStart();
									ChromoMap owningMap = link.getFeature2().getOwningMap();
									float referenceMapStop = owningMap.getStop();
									int refChromoIndex = owningMap.getOwningMapSet().getMaps().indexOf(
													owningMap);
									GChromoMap referenceGMap = referenceGMapSet.gMaps.get(refChromoIndex);
									int referenceChromoY = referenceGMap.y;
									
									// convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
									int targetY = (int) (feat1Start / (targetMapStop / mainCanvas.gMapSetList.get(mainCanvas.targetGMapSetIndex).gMaps.get(0).height)) + selectedChromoY;
									int referenceY = (int) (feat2Start / (referenceMapStop / referenceGMap.height)) + referenceChromoY;
	
									//this next condition ensures we only draw links to reference features that are showing on the canvas
									if ((referenceY > 0  && referenceY < mainCanvas.getHeight()) && 
													(targetY > 0  && targetY < mainCanvas.getHeight()))
									{
										System.out.println("drawing link between maps " + link.getFeature1().getOwningMap().getName() + " and " + link.getFeature2().getOwningMap().getName());
										
										// draw the line
										g2.drawLine(selectedChromoX + 1, targetY, referenceChromoX - 1, referenceY);
									}
								}
							}
						}
					}
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
			mainCanvas.linkSetLookup = new Hashtable<ChromoMap, Vector<LinkSet>>();
			
			for (LinkSet links : mainCanvas.linkSets)
			{
				MapSet targetMapSet = links.getMapSets().get(0);
				MapSet referenceMapSet = links.getMapSets().get(1);
				
				System.out.println("making link subset between " + links.getMapSets().get(0).getName() + 
								" and " +  links.getMapSets().get(1).getName());
				
				// for each chromosome in the target mapset
				for (ChromoMap targetMap : targetMapSet)
				{
					Vector<LinkSet> linkSets = null;
					
					//check whether this target map already has a vector of linksets in the lookup table#
					if(mainCanvas.linkSetLookup.get(targetMap) == null)
					{
						System.out.println("making new vector");
						//if it doesn't exist , create it here
						// create a new Vector which holds all the linksets of links between this chromosome and the reference chromosomes
						linkSets = new Vector<LinkSet>();
						//add the vector as the value for this targetMap in the lookup table
						// then add the list to the hashtable
						mainCanvas.linkSetLookup.put(targetMap, linkSets);
					}
					else // it already exists, just retrieve it
					{
						System.out.println("retrieving existing vector");
						linkSets = mainCanvas.linkSetLookup.get(targetMap);
					}
					
					// for each reference chromosome
					for (ChromoMap refMap : referenceMapSet)
					{
						System.out.println("making subset between maps " + targetMap.getName() + " and " 
						+ refMap.getName());
						// make a linkset that contains only the links between this chromo and the target chromo
						LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
//						System.out.println("linkSubset = " + linkSubset);
//						System.out.println("linkSets = " + linkSets);
						// add the linkset to the list
						linkSets.add(linkSubset);
					}
				}
				
				//check
				System.out.println("cmaps in linkset lookup:");
				for( ChromoMap cMap : mainCanvas.linkSetLookup.keySet())
				{
					System.out.println(cMap.getName() + " , size = " + mainCanvas.linkSetLookup.get(cMap).size());
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
