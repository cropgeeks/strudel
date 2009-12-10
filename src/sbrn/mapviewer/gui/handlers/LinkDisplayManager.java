package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

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

	private static double blastThreshold = 1;

	//degree of link curvature
	public float linkShapeCoeff = Constants.MAX_CURVEDLINK_COEFF;

	// A hashtable that stores information on what lines have been drawn, to
	// ensure that when *different* lines that map to the same pixel space are
	// found, only the first instance is actually drawn. This saves time.
	Hashtable<String, Boolean> linesDrawn = new Hashtable<String, Boolean>();

	//	=====================================c'tor==============================================

	public LinkDisplayManager(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
		initLinkSetLookup();
	}

	//	=====================================methods===========================================

	// display the homologies between chromosomes as lines
	public void processLinkDisplayRequest(int x, int y)
	{
		Vector<GChromoMap> selectedMaps = Strudel.winMain.fatController.selectedMaps;

		//only do this if we have reference genomes -- otherwise there are no links to deal with
		if(Strudel.winMain.dataContainer.gMapSets.size() > 1)
		{
			// first figure out which chromosome we are in
			GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataContainer.gMapSets, x, y);
			GMapSet selectedSet = selectedMap.owningSet;

			// the click has hit a chromosome
			if (selectedMap != null)
			{
				// single click with Ctrl down -- user wants to select individual maps
				// in that case we just add or remove maps to the vector of selected maps as requested
				if (Strudel.winMain.fatController.isCtrlClickSelection)
				{
					// if the map is already added we need to remove it (this is toggle-style functionality)
					if (selectedMaps.contains(selectedMap))
					{
						selectedMaps.remove(selectedMap);
						selectedMap.highlight = false;
					}
					// otherwise we add it
					else
					{
						selectedMaps.add(selectedMap);
						selectedMap.highlight = true;
					}
				}
				// this is just a normal single click -- user wants to do overviews of links from individual target chromosomes, one at a time
				else
				{
					// in that case we first clear out the existing vector of selected maps in the target genome
					selectedMaps.clear();
					Strudel.winMain.fatController.clearMapOutlines();

					// then we add the selected map only
					selectedMaps.add(selectedMap);
					selectedMap.highlight = true;

					mainCanvas.drawLinks = true;
				}

				// now check whether we have selected chromosomes in the target genome
				if (selectedMaps.size() > 0)
				{
					mainCanvas.drawLinks = true;
				}
				// if not, we don't want to draw links, just display the selected outlines of the reference genome chromosomes
				else
				{
					mainCanvas.drawLinks = false;
				}
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public void multicoreDrawAllLinks(final Graphics2D g, final Boolean killMe)
	{
		linesDrawn = new Hashtable<String, Boolean>();

		try
		{
			// Paint the links using multiple cores...
			for (int i = 0; i < MainCanvas.tasks.length; i++)
			{
				final int startIndex = i;

				MainCanvas.tasks[i] = MainCanvas.executor.submit(new Runnable() {
					public void run() {
						drawAllLinks(g, startIndex, killMe);
				}});
			}

			// Wait in the drawing finishing before continuing
			for (Future task: MainCanvas.tasks)
				task.get();
		}
		catch (Exception e) {}
	}

	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	private void drawAllLinks(Graphics2D g2, int startIndex, Boolean killMe)
	{
		int numLinksDrawn = 0;

		//only do this if we have at least 2 genomes -- otherwise there are no links to deal with
		if(Strudel.winMain.dataContainer.gMapSets.size() > 1)
		{
			try
			{
				//potentially each linkset could be drawn from map A to map B and in the reverse direction too
				//this object helps keep track of the linksets already drawn so we don't duplicate the effort
				Vector<LinkSet> drawnLinkSets = new Vector<LinkSet>();

				// for each map in the selectedMaps vector
				for (int i = 0; i < Strudel.winMain.fatController.selectedMaps.size(); i++)
				{
					if (killMe)
						return;

					// get the currently selected map
					GChromoMap selectedMap = Strudel.winMain.fatController.selectedMaps.get(i);

					// get the ChromoMap for the currently selected chromosome
					ChromoMap selectedChromoMap = selectedMap.chromoMap;

					// get all the linksets between the selected chromosome and the reference maps selected
					Vector<LinkSet> linkSets = linkSetLookup.get(selectedChromoMap);

					// for each set of links between the selected chromosome and a reference map
					for (LinkSet selectedLinks : linkSets)
					{
						if (killMe)
							return;

						if(selectedLinks == null)
							continue;

						//find out which mapsets we are dealing with here
						//we know that what was clicked on is our target map and its owning mapset is therefore the target mapset
						GMapSet targetGMapSet = selectedMap.owningSet;
						GMapSet mapset1 = Utils.getGMapSetByName(selectedLinks.getMapSets().get(0).getName());
						GMapSet mapset2 = Utils.getGMapSetByName(selectedLinks.getMapSets().get(1).getName());
						//now find out which mapset the other one is in the current set of links
						GMapSet referenceGMapSet = null;
						if (mapset1 == targetGMapSet)
							referenceGMapSet = mapset2;
						else
							referenceGMapSet = mapset1;

						//find out the maps involved in this linkset
						GChromoMap gMap1 = selectedLinks.getLinks().get(0).getFeature1().getOwningMap().getGChromoMap();
						GChromoMap gMap2 = selectedLinks.getLinks().get(0).getFeature2().getOwningMap().getGChromoMap();
						//need to check whether there is any point in proceeding here
						//if the user is doing Ctrl click selection of maps they will only want to see links between the ones they selected
						//so if we don't have both of the maps in this map set selected, just skip to the next one
						if (Strudel.winMain.fatController.isCtrlClickSelection)
						{
							if (!(Strudel.winMain.fatController.selectedMaps.contains(gMap1) && Strudel.winMain.fatController.selectedMaps.contains(gMap2)) || drawnLinkSets.contains(selectedLinks))
								continue;
						}

						// the x coordinates have to be worked out
						int targetChromoX = 0;
						int referenceChromoX = 0;
						//need to adjust the x positions of the links here
						//if the reference genome is to the right of the target genome (indicated by their positions in the list of mapsets held in the datacontainer)
						if (Strudel.winMain.dataContainer.gMapSets.indexOf(targetGMapSet) < Strudel.winMain.dataContainer.gMapSets.indexOf(referenceGMapSet))
						{
							targetChromoX = Math.round(targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width) + 1;
							referenceChromoX = Math.round(referenceGMapSet.xPosition) -1;
						}
						//the ref genome is to the left of the target genome
						else
						{
							// we want the referenceChromoX to be the mapsets x plus its width
							referenceChromoX = Math.round(referenceGMapSet.xPosition + referenceGMapSet.gMaps.get(0).width) + 1;
							// and we want the target genome's x to be the targetChromoX
							targetChromoX = Math.round(targetGMapSet.xPosition) -1;
						}

						// set the colour
						g2.setColor(Colors.linkColour);

						//add this linkset to our vector of linksets we have drawn already, for tracking
						drawnLinkSets.add(selectedLinks);

						// for each link in the linkset
						for (int li = startIndex, n = selectedLinks.size(); li < n; li += MainCanvas.cores)
						{
							if (killMe)
								return;

							Link link = selectedLinks.getLinks().get(li);

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
								if (link.getFeature1().getOwningMap() == targetGMap.chromoMap)
								{
									referenceGMap = link.getFeature2().getOwningMap().getGChromoMap();
									targetfeature = link.getFeature1();
									referenceFeature = link.getFeature2();
								}
								else
									//if feature 2 is on the target map, feat 1 on the reference
								{
									referenceGMap = link.getFeature1().getOwningMap().getGChromoMap();
									targetfeature = link.getFeature2();
									referenceFeature = link.getFeature1();
								}

								//now we can get on with working out coordinates
								// get the positional data of the features and the end point of the map
								float targetFeatureStart = targetfeature.getStart();
								float referenceFeatureStart = referenceFeature.getStart();

								// convert the y value to scaled coordinates on the canvas by obtaining the coords of the appropriate chromosome object and scaling them appropriately
								int targetY = Utils.getFPosOnScreenInPixels(targetGMap.chromoMap, targetFeatureStart, false);
								int referenceY = Utils.getFPosOnScreenInPixels(referenceGMap.chromoMap, referenceFeatureStart, false);

								//check for chromosome inversion and invert values if necessary
								if (targetGMap.isPartlyInverted)
								{
									targetY = Utils.getFPosOnScreenInPixels(targetGMap.chromoMap, targetFeatureStart, true);
								}
								if (referenceGMap.isPartlyInverted)
								{
									referenceY = Utils.getFPosOnScreenInPixels(referenceGMap.chromoMap, referenceFeatureStart, true);
								}

								//decide whether this link should be drawn or not
								boolean drawLink = false;
								//there is a user preference for filtering the number of links by whether their originating feature is visible on canvas or not (Prefs.drawOnlyLinksToVisibleFeatures)
								//we also only want to draw the links that fall within the canvas boundaries
								if ((Prefs.drawOnlyLinksToVisibleFeatures && (referenceY > 0 && referenceY < mainCanvas.getHeight()) && (targetY > 0 && targetY < mainCanvas.getHeight())) || !Prefs.drawOnlyLinksToVisibleFeatures)
								{
									drawLink = true;
								}

								String key = ""+targetChromoX+ targetY+ referenceChromoX+ referenceY;
								if(linesDrawn.get(key) == null && drawLink)
								{
									linesDrawn.put(key, true);
									// draw the link either as a straight line or a curve
									drawStraightOrCurvedLink(g2, targetChromoX, targetY, referenceChromoX, referenceY);
									numLinksDrawn++;
								}
							}
						}
					}
				}
			}
			catch (RuntimeException e)
			{
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public void drawHighlightedLink(Graphics2D g2, Feature f1, Feature f2, boolean strongEmphasis)
	{
		//get the owning GChromoMap objects associated with this link
		GChromoMap gMap1 = f1.getOwningMap().getGChromoMap();
		GChromoMap gMap2 = f2.getOwningMap().getGChromoMap();

		GMapSet targetGMapSet = gMap1.owningSet;
		GMapSet referenceGMapSet = gMap2.owningSet;

		//work out the x and y coords for drawing the link
		int targetChromoX = -1;
		int referenceChromoX = -1;

		//need to adjust the x positions of the links here
		//if the reference genome is to the right of the target genome (indicated by their positions in the list of mapsets held inthe datacontainer)
		if (Strudel.winMain.dataContainer.gMapSets.indexOf(targetGMapSet) < Strudel.winMain.dataContainer.gMapSets.indexOf(referenceGMapSet) )
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


	// --------------------------------------------------------------------------------------------------------------------------------


	// Draws the lines between features in a certain range on a chromosome of the target genome and all potential homologues in the compared genome
	public void drawHighlightedLinksInRange(Graphics2D g2)
	{
		//only do this if we have at least 2 genomes -- otherwise there are no links to deal with
		if(Strudel.winMain.dataContainer.gMapSets.size() > 1)
		{
			// for all  links sets
			for (LinkSet selectedLinks : Strudel.winMain.dataContainer.allLinkSets)
			{
				// for each link in the linkset
				for (Link link : selectedLinks)
				{
					//get the features of this link
					Feature f1 = link.getFeature1();
					Feature f2 = link.getFeature2();

					//make a vector with the features in this link so we can test for their visibility
					Vector<Feature> featuresInLink = new Vector<Feature>();
					featuresInLink.add(f1);
					featuresInLink.add(f2);
					boolean bothFeaturesVisible = Utils.checkFeatureVisibility(featuresInLink).size() == 2;

					Vector<Feature> featuresInRange = FeatureSearchHandler.featuresInRange;

					//check whether either of the features for this link are included in the found features list of their maps
					// we also only want to draw this link if it has a BLAST e-value smaller than the cut-off currently selected by the user
					boolean eValueBelowThreshold = (featuresInRange.contains(f1) || featuresInRange.contains(f2) ) && link.getBlastScore() <= blastThreshold;

					if(eValueBelowThreshold)
					{
						if (Prefs.drawOnlyLinksToVisibleFeatures)
						{
							if(bothFeaturesVisible)
							{
								//draw the link
								drawHighlightedLink(g2, f1, f2, false);
							}
						}
						else
						{
							//draw the link
							drawHighlightedLink(g2, f1, f2, false);
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
	private void initLinkSetLookup()
	{
		try
		{
			linkSetLookup = new Hashtable<ChromoMap, Vector<LinkSet>>();

			//we need to do this for all linksets we have
			for (LinkSet links : Strudel.winMain.dataContainer.allLinkSets)
			{
				MapSet mapset1 = links.getMapSets().get(0);
				MapSet mapset2 = links.getMapSets().get(1);
				makeLinkSubsets(mapset1, mapset2, links);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	private void makeLinkSubsets(MapSet mapset1, MapSet mapset2, LinkSet allLinksBetweenMapsetsPair)
	{
		int numLinkSetsMade = 0;
		int numLinkSetsAdded = 0;

		// for each map in the first mapset
		for (ChromoMap targetMap : mapset1)
		{
			//a vector of linksets for this map
			Vector<LinkSet> allTargetMapLinksets = null;
			//check whether this map already has an entry in the lookup
			//if yes, use that , otherwise make a new one
			if(linkSetLookup.get(targetMap) == null)
				allTargetMapLinksets = new Vector<LinkSet>();
			else
				allTargetMapLinksets = linkSetLookup.get(targetMap);

			//add the vector as the value for this targetMap in the lookup table
			// then add the list to the hashtable
			linkSetLookup.put(targetMap, allTargetMapLinksets);

			// for each reference chromosome
			for (ChromoMap refMap : mapset2)
			{
				// make a linkset that contains only the links between this chromo and the target chromo
				LinkSet targetRefLinkSubset = allLinksBetweenMapsetsPair.getLinksBetweenMaps(targetMap, refMap);

				// add the linkset to the list but only if it has links in it
				if(targetRefLinkSubset.size() > 0 )
				{
					allTargetMapLinksets.add(targetRefLinkSubset);

					numLinkSetsMade++;
					numLinkSetsAdded++;
				}
			}
		}

		// for each map in the second mapset
		for (ChromoMap refMap : mapset2)
		{
			//a vector of linksets for this map
			Vector<LinkSet> allRefMapLinksets = null;

			//check whether this map already has en entry in the lookup
			//if yes, use that , otherwise make a new one
			if(linkSetLookup.get(refMap) == null)
				allRefMapLinksets = new Vector<LinkSet>();
			else
				allRefMapLinksets = linkSetLookup.get(refMap);

			//add the vector as the value for this targetMap in the lookup table
			// then add the list to the hashtable
			linkSetLookup.put(refMap, allRefMapLinksets);

			// for each map in the first mapset
			for (ChromoMap targetMap : mapset1)
			{
				//find the linkset in the lookup that represents the links between this refMap and this targetMap
				LinkSet existingLinkSet = getLinkSetBetweenMaps(targetMap, refMap);
				//add entry to the lookup with this refMap as the key, pointing to the existing linkset as the value
				if(!allRefMapLinksets.contains(existingLinkSet))
				{
					allRefMapLinksets.add(existingLinkSet);
					numLinkSetsAdded++;
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	private LinkSet getLinkSetBetweenMaps(ChromoMap targetMap, ChromoMap refMap)
	{
		LinkSet linkSet = null;

		//find the targetmap in the lookup
		Vector<LinkSet> targetLinkSets = linkSetLookup.get(targetMap);
		//check through its linksets until you find the one that has the refMap's as its second maps
		for (LinkSet linkSet2 : targetLinkSets)
		{
			//in this linkset entries all links are from the same map
			//so we can extract the first link in this linkset and get its map and this will tell us which maps the linkset is between
			Link link = linkSet2.getLinks().get(0);
			ChromoMap cMap1 = link.getFeature1().getOwningMap();
			ChromoMap cMap2 = link.getFeature2().getOwningMap();

			if(cMap1 == refMap || cMap2 == refMap)
			{
				linkSet = linkSet2;
			}
		}

		return linkSet;
	}

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
		if(Strudel.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_CURVED ||
						Strudel.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_STRAIGHT)
		{
			//if the linkCurvatureCoeff is greater than 0 we make this line into a curve by moving the control points towards the centre
			if (linkShapeCoeff > 0)
			{
				ctrlx1 = startX + ((endX - startX) * linkShapeCoeff);
				ctrlx2 = endX - ((endX - startX) * (linkShapeCoeff * 2));
			}

			// draw CubicCurve2D.Double with set coordinates
			CubicCurve2D curve = new CubicCurve2D.Double();
			curve.setCurve(startX, startY, ctrlx1, ctrly1, ctrlx2, ctrly2, endX, endY);
			g2.draw(curve);
		}
		//this is what we do for angled lines
		else if(Strudel.winMain.toolbar.currentLinkShapeType == Constants.LINKTYPE_ANGLED)
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
