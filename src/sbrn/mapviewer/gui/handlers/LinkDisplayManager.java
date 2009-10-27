package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import sbrn.mapviewer.*;
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
	
	private static double blastThreshold = 1;

	//degree of link curvature
	public float linkShapeCoeff = Constants.MAX_CURVEDLINK_COEFF;
	
	// CubicCurve2D.Double -- this object can be reused for drawing all curved lines
	CubicCurve2D curve = new CubicCurve2D.Double();
	
	//the currently selected map and mapset
	GChromoMap selectedMap = null;
	GMapSet selectedSet = null;
	
	
	//	=====================================curve'tor==============================================
	
	public LinkDisplayManager(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
		makeTargetLinkSubSets();
//		precomputeAllLinks();
	}
	
	//	=====================================methods===========================================
	
	// display the homologies between chromosomes as lines
	public void processLinkDisplayRequest(int x, int y, boolean isCtrlClickSelection)
	{
		//only do this if we have reference genomes -- otherwise there are no links to deal with
		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
		{			
			// first figure out which chromosome we are in
			selectedMap = Utils.getSelectedMap(MapViewer.winMain.dataContainer.gMapSets, x, y);
			selectedSet = selectedMap.owningSet;
			
			MapViewer.logger.info("selectedMap = " + selectedMap.name);
			MapViewer.logger.info("selectedSet = " + selectedSet.name);
			
			// the click has hit a chromosome
			if (selectedMap != null)
			{
				// single click with Ctrl down -- user wants to select individual maps
				// in that case we just add or remove maps to the vector of selected maps as requested
				if (isCtrlClickSelection)
				{
					// if the map is already added we need to remove it (this is toggle-style functionality)
					if (selectedSet.selectedMaps.contains(selectedMap))
					{
						selectedSet.removeSelectedMap(selectedMap);
					}
					// otherwise we add it
					else
					{
						selectedSet.addSelectedMap(selectedMap);
					}
				}
				// this is just a normal single click -- user wants to do overviews of links from individual target chromosomes, one at a time
				else
				{

						// in that case we first clear out the existing vector of selected maps in the target genome
						selectedSet.deselectAllMaps();
						
						// then we add the selected map only
						selectedSet.addSelectedMap(selectedMap);
						
						// now add all maps from all the other genomes into the vector of selected elements for the reference genome so the links can be drawn
						for (GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSets)
						{
							//check that we are not adding maps from mapsets that would require us to draw across another mapset
							int indexDifference = Math.abs(MapViewer.winMain.dataContainer.gMapSets.indexOf(selectedSet) - 
							MapViewer.winMain.dataContainer.gMapSets.indexOf(gMapSet));
							if (!gMapSet.equals(selectedSet) && indexDifference < 2)
							{
								gMapSet.selectAllMaps();
							}
						}
						mainCanvas.drawLinks = true;

				}
				
				// now check whether we have selected chromosomes in the target genome
				if (selectedSet.selectedMaps.size() > 0)
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
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	public void drawAllLinks(Graphics2D g2)
	{

		//only do this if we have at least genomes -- otherwise there are no links to deal with
		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
		{			
			try
			{				
				// for each map in the selectedMaps vector of the target genome
				for (int i = 0; i < selectedSet.selectedMaps.size(); i++)
				{
					// get the currently selected map
					GChromoMap selectedMap = selectedSet.selectedMaps.get(i);

					// get the ChromoMap for the currently selected chromosome
					ChromoMap selectedChromoMap = selectedMap.chromoMap;
					
					// get all the links between the selected chromosome and the reference mapset
					Vector<LinkSet> linkSets = linkSetLookup.get(selectedChromoMap);
					
					float targetMapStop = selectedChromoMap.getStop();
					
					// for all selected links
					for (LinkSet selectedLinks : linkSets)
					{
						int numLinksdrawn = 0;
						
						//find out which mapsets we are dealing with here
						//we know that what was clicked on is our target map and its owning mapset is therefore the target mapset
						GMapSet targetGMapSet = selectedMap.owningSet;
						//now find out which mapset the other one is int he current set of links	
						GMapSet mapset1 = Utils.getGMapSetByName(selectedLinks.getMapSets().get(0).getName());
						GMapSet mapset2 = Utils.getGMapSetByName(selectedLinks.getMapSets().get(1).getName());						
						GMapSet referenceGMapSet = null;
						if(mapset1 == targetGMapSet)
							referenceGMapSet = mapset2;
						else
							referenceGMapSet = mapset1;

						// get the real coordinates for the selected chromo and the reference chromo
						int selectedChromoY = selectedMap.y + selectedMap.currentY;
						// the x coordinates have to be worked out
						int targetChromoX = 0;
						int referenceChromoX = 0;
						
						//need to adjust the x positions of the links here
						//if the reference genome is to the right of the target genome (indicated by their positions in the list of mapsets held inthe datacontainer)
						if (MapViewer.winMain.dataContainer.gMapSets.indexOf(targetGMapSet) < MapViewer.winMain.dataContainer.gMapSets.indexOf(referenceGMapSet) )
						{
							targetChromoX = Math.round(targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width);
							referenceChromoX = Math.round(referenceGMapSet.xPosition);
						}
						//the ref genome is to the left of the target genome
						else 
						{
							// we want the referenceChromoX to be the mapsets x plus its width
							referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width);
							// and we want the target genome's x to be the targetChromoX
							targetChromoX = Math.round(targetGMapSet.xPosition);
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
									
									//we can't make any assumptions about the ordering of the links because we use the same link to 
									//display homologies going either way
									//so we need to figure out here which is the target feature and which the reference	
									GChromoMap targetGMap = selectedMap;
									GChromoMap referenceGMap = null;
									Feature targetfeature, referenceFeature;
									//if feature 1 is on the target map
									if(link.getFeature1().getOwningMap() == targetGMap.chromoMap)
									{
										referenceGMap = link.getFeature2().getOwningMap().getGChromoMap();
										targetfeature = link.getFeature1();
										referenceFeature = link.getFeature2();
									}
									else//if feature 2 is on the target map, feat 1 on the reference
									{
										referenceGMap = link.getFeature1().getOwningMap().getGChromoMap();
										targetfeature = link.getFeature2();
										referenceFeature = link.getFeature1();
									}
									
									//now we can get on with working out coordinates
									// get the positional data of the features and the end point of the map
									float targetFeatureStart = targetfeature.getStart();
									float referenceFeatureStart = referenceFeature.getStart();
									float referenceMapStop = referenceGMap.chromoMap.getStop();							
									int referenceChromoY = referenceGMap.y + referenceGMap.currentY;
																	
									// convert the y value to scaled coordinates on the canvas by obtaining the coords of the appropriate chromosome object and scaling them appropriately
									int targetY = (int) (targetFeatureStart / (targetMapStop / selectedMap.height)) + selectedChromoY;
									int referenceY = (int) (referenceFeatureStart / (referenceMapStop / referenceGMap.height)) + referenceChromoY;

									//check for chromosome inversion and invert values if necessary
									if(targetGMap.isPartlyInverted)
									{
										targetY = (int) ((targetMapStop - targetFeatureStart) / (targetMapStop / targetGMap.height)) + selectedChromoY;
									}
									if(referenceGMap.isPartlyInverted)
									{
										referenceY = (int) ((referenceMapStop - referenceFeatureStart) / (referenceMapStop / referenceGMap.height)) + referenceChromoY;
									}
									
									//this is a user preference for filtering the number of links by whether their originating
									//feature is visible on canvas or not
									if (Prefs.drawOnlyLinksToVisibleFeatures)
									{
										//this next condition ensures we only draw links to reference features that are showing on the canvas
										if ((referenceY > 0 && referenceY < mainCanvas.getHeight()) && (targetY > 0 && targetY < mainCanvas.getHeight()) && referenceGMapSet.selectedMaps.contains(referenceGMap))
										{
											// draw the link either as a straight line or a curve
											drawStraightOrCurvedLink(
															g2,
															targetChromoX + 1,
															targetY,
															referenceChromoX - 1,
															referenceY);
											numLinksdrawn++;
										}
									}
									//otherwise we just draw every link
									else
									{
										if (referenceGMapSet.selectedMaps.contains(referenceGMap))
										{
											// draw the link either as a straight line or a curve
											drawStraightOrCurvedLink(
															g2,
															targetChromoX + 1,
															targetY,
															referenceChromoX - 1,
															referenceY);
											numLinksdrawn++;
										}
									}
								}
								else
								{
									MapViewer.logger.fine("link e-value is above BLAST threshold");
								}
							}
						}
						MapViewer.logger.fine("numLinksdrawn = " + numLinksdrawn);
					}
				}
			}			
			catch (RuntimeException e)
			{
				e.printStackTrace();
			}		
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	public void drawHighlightedLink(Graphics2D g2, Feature f1, Feature f2, boolean strongEmphasis)
	{
		//only do this if we have at least 2 genomes -- otherwise there are no links to deal with
		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
		{					
			//get the owning GChromoMap objects associated with this link
			GChromoMap gMap1 = f1.getOwningMap().getGChromoMap();
			GChromoMap gMap2 = f2.getOwningMap().getGChromoMap();
			
			GMapSet targetGMapSet = gMap1.owningSet;
			GMapSet referenceGMapSet = gMap2.owningSet;

			//work out the x and y coords for drawing the link
			int targetChromoX = -1;
			int referenceChromoX = -1;
			
			// if we have only one reference genome we want the targetChromoX to be that chromo'sx plus its width and
			// the referenceChromoX to be the reference chromo's x
			if (MapViewer.winMain.dataContainer.gMapSets.size() == 1 || (MapViewer.winMain.dataContainer.gMapSets.size() == 2 && MapViewer.winMain.dataContainer.gMapSets.indexOf(referenceGMapSet) == 1))
			{
				targetChromoX = Math.round(targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width);
				referenceChromoX = Math.round(referenceGMapSet.xPosition);
			}
			
			// if we have two reference genomes and this is the first reference genome
			else if (MapViewer.winMain.dataContainer.gMapSets.size() == 2 && MapViewer.winMain.dataContainer.gMapSets.indexOf(referenceGMapSet) == 0)
			{
				// we want the referenceChromoX to be the mapsets x plus its width
				referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width);
				// and we want the target genome's x to be the targetChromoX
				targetChromoX = Math.round(targetGMapSet.xPosition);
			}
			
			//y coords
			int y1 = Math.round(gMap1.y + gMap1.currentY + (f1.getStart() * (gMap1.height / gMap1.chromoMap.getStop())));
			int y2 = Math.round(gMap2.y + gMap2.currentY + (f2.getStart() * (gMap2.height / gMap2.chromoMap.getStop())));
			
			//check for chromosome inversion and invert values if necessary
			if(gMap1.isPartlyInverted || gMap1.isFullyInverted)
			{
				y1 = (int) ((gMap1.chromoMap.getStop() - f1.getStart()) / (gMap1.chromoMap.getStop() / gMap1.height)) + (gMap1.y + gMap1.currentY);
			}
			if(gMap2.isPartlyInverted  || gMap2.isFullyInverted)
			{
				y2 = (int) ((gMap2.chromoMap.getStop() - f2.getStart()) / (gMap2.chromoMap.getStop() / gMap2.height)) + (gMap2.y + gMap2.currentY);
			}
			
			// draw the link either as a straight line or a curve
			if(strongEmphasis)
				g2.setColor(Colors.strongEmphasisLinkColour);
			else
				g2.setColor(Colors.mildEmphasisLinkColour);
			drawStraightOrCurvedLink(g2,targetChromoX, y1, referenceChromoX, y2);		
		}
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	
	// Draws the lines between features in a certain range on a chromosome of the target genome and all potential homologues in the compared genome
	public void drawHighlightedLinksInRange(Graphics2D g2)
	{		
		//only do this if we have at least 2 genomes -- otherwise there are no links to deal with
		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
		{					
			// for all  links sets
			for (LinkSet selectedLinks : MapViewer.winMain.dataContainer.allLinkSets)
			{
				// for each link in the linkset
				for (Link link : selectedLinks)
				{
					//get the features of this link
					Feature f1 = link.getFeature1();
					Feature f2 = link.getFeature2();
					
					Vector<Feature> featuresInRange = FeatureSearchHandler.featuresInRange;
					
					Color linkColour = Colors.mildEmphasisLinkColour;
					
					//check whether either of the features for this link are included in the found features list of their maps
					// we also only want to draw this link if it has a BLAST e-value smaller than the cut-off currently selected by the user
					if((featuresInRange.contains(f1) || featuresInRange.contains(f2) ) && link.getBlastScore() <= blastThreshold)							
					{
						//draw the link
						drawHighlightedLink(g2, f1, f2, false);	
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
		//only do this if we have reference genomes -- otherwise there are no links to deal with
		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
		{			
			try
			{
				linkSetLookup = new Hashtable<ChromoMap, Vector<LinkSet>>();
				
				//we need to do this for all linksets we have
				for (LinkSet links : MapViewer.winMain.dataContainer.allLinkSets)
				{
					MapSet targetMapSet = links.getMapSets().get(0);
					MapSet referenceMapSet = links.getMapSets().get(1);
					makeSingleLinkSubset(targetMapSet, referenceMapSet, links);
					
					//now we need to swap the two around so we can have the reciprocal subsets added too
					//i.e. what was the target genome the first time round now becomes the reference genome and vice versa
					targetMapSet = links.getMapSets().get(1);
					referenceMapSet = links.getMapSets().get(0);
					makeSingleLinkSubset(targetMapSet, referenceMapSet, links);		
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
		
	private void makeSingleLinkSubset(MapSet targetMapSet, MapSet referenceMapSet,LinkSet links)
	{
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
				MapViewer.logger.info("adding to linkSetLookup targetMap = " + targetMap.getName()); 
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
				if(linkSubset.size() > 0)
					linkSets.add(linkSubset);
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
//	/**
//	 * This method precomputes subsets of links between each target chromosome and each reference genome so that drawing them is quicker.
//	 */
//	private void precomputeAllLinks()
//	{
//		//only do this if we have reference genomes -- otherwise there are no links to deal with
//		if(MapViewer.winMain.dataContainer.gMapSets.size() > 1)
//		{			
//			
//			try
//			{
//				allLinksLookup = new Hashtable<ChromoMap, LinkSet>();
//				
//				//we need to do this for all linksets between the target and each of the reference genomes
//				for (LinkSet links : MapViewer.winMain.dataContainer.allLinkSets)
//				{
//					MapSet targetMapSet = links.getMapSets().get(0);
//					MapSet referenceMapSet = links.getMapSets().get(1);
//					
//					// for each chromosome in the target mapset
//					for (ChromoMap targetMap : targetMapSet)
//					{
//						LinkSet linkSet = null;
//						
//						//check whether this target map already has a linkset in the lookup table
//						if(allLinksLookup.get(targetMap) == null)
//						{
//							//if it doesn't exist , create it here
//							// create a  single linkset of links between this chromosome and the reference chromosomes
//							linkSet = new LinkSet();
//							//add the linkset as the value for this targetMap in the lookup table
//							allLinksLookup.put(targetMap, linkSet);
//						}
//						else // it already exists, just retrieve it
//						{
//							linkSet = allLinksLookup.get(targetMap);
//						}
//						
//						// for each reference chromosome
//						for (ChromoMap refMap : referenceMapSet)
//						{						
//							// make a linkset that contains only the links between this chromo and the target chromo
//							LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
//							
//							// add the linkset to the list but only if it has links in it
//							if(linkSubset.getLinks().size() > 0)
//								linkSet.combineWithLinkSet(linkSubset);
//						}
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
	
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	//draws a single link, either as a straight line or a curve depending on the value of the linkCurvatureCoeff set at class level
	private void drawStraightOrCurvedLink(Graphics2D g2, int startX, int startY, int endX, int endY)
	{	

		//if the linkCurvatureCoeff is 0 then the line will be straight and the control points are simply moved to either end of the line	
		//ctrlx1 - the X coordinate used to set the first control point of this CubicCurve2D
		//ctrly1 - the Y coordinate used to set the first control point of this CubicCurve2D
		//ctrlx2 - the X coordinate used to set the second control point of this CubicCurve2D
		//ctrly2 - the Y coordinate used to set the second control point of this CubicCurve2D	
		double ctrlx1 = startX;
		double ctrlx2 = endX;		
		double ctrly1 = startY;
		double ctrly2 = endY;
		
		//this is what we do for straight or curved lines
		if(MapViewer.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_CURVED || 
						MapViewer.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_STRAIGHT)
		{
			//if the linkCurvatureCoeff is greater than 0 we make this line into a curve by moving the control points towards the centre	
			if (linkShapeCoeff > 0)
			{
				ctrlx1 = startX + ((endX - startX) * linkShapeCoeff);
				ctrlx2 = endX - ((endX - startX) * (linkShapeCoeff * 2));
			}
			
			// draw CubicCurve2D.Double with set coordinates
			curve.setCurve(startX, startY, ctrlx1, ctrly1, ctrlx2, ctrly2, endX, endY);
			g2.draw(curve);
		}
		//this is what we do for angled lines
		else if(MapViewer.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_ANGLED)
		{
			ctrlx1 = startX + ((endX - startX) * linkShapeCoeff);
			ctrlx2 = endX - ((endX - startX) * linkShapeCoeff);
			
			g2.drawLine(startX, startY, (int)ctrlx1, (int)ctrly1);
			g2.drawLine((int)ctrlx1, (int)ctrly1, (int)ctrlx2, (int)ctrly2);
			g2.drawLine((int)ctrlx2, (int)ctrly2, endX, endY);			
		}
	}
	
	public static double getBlastThresholdExponent()
	{
		return Math.log(blastThreshold);
	}
	
	public static void setBlastThresholdWithExponent(int exponent )
	{
		DecimalFormat df = new DecimalFormat("0.##E0");
		Number score;
		try
		{
			score = df.parse("1.00E" + exponent);
			blastThreshold = score.doubleValue();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setBlastThreshold(double blastThreshold)
	{
		LinkDisplayManager.blastThreshold = blastThreshold;
	}
	
	
}// end class
