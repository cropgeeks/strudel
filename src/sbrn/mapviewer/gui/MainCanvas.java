package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class MainCanvas extends JPanel
{
	
	// ============================vars==================================
	
	// the parent component
	WinMain winMain;
	
	// the map sets we need to draw
	GMapSet targetGMapSet;
	GMapSet referenceGMapSet;
	
	// for convenience purposes, a list that holds these
	public LinkedList<GMapSet> gMapSetList;
	
	// size of the frame
	int canvasHeight;
	int canvasWidth;
	
	// the maximum nuber of chromos in any one of the genomes involved
	int maxChromos;
	
	// space the chromosomes vertically by this fixed amount
	int chromoSpacing = 0;
	
	// these variables determine where the genomes appear on the canvas on the x axis (scaled to 0-1)
	// position is relative to frame size
	float leftGenomeX = 0.3f;
	float rightGenomeX = 0.7f;
	
	// threshold values for the zoom factor above which we want to display markers and labels
	// float thresholdMarkerPainting = 3;
	// float thresholdLabelPainting = 3;
	
	// a hashtable that contains chromomaps from both genomes as keys and LinkedList objects as values, which in
	// turn hold a list of LinkSet objects each, where each Linkset represents the links between the chromomap and a
	// chromomap in the respectively other genome
	Hashtable<ChromoMap, LinkedList<LinkSet>> linkSetLookup;
	
	// a hashtable that holds ChromoMap objects as keys and their corresponding GChromoMap objects as values
	Hashtable<ChromoMap, GChromoMap> gMapLookup = new Hashtable<ChromoMap, GChromoMap>();
	
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	static LinkSet links = null;
	
	// do we need to draw links?
	boolean linksToDraw = false;
	
	// if true, antialias everything
	public boolean antiAlias = false;
	
	// if true, paint a rectangle to indicate the fact that we are panning over a region we want to select for zooming in to
	public boolean drawSelectionRect = false;
	//these are the relevant coordinates for this
	public int mousePressedX = -1;
	public int mousePressedY = -1;
	public int mouseDraggedX = -1;
	public int mouseDraggedY = -1;
	
	//the handler for all zooming related events
	public CanvasZoomHandler zoomHandler;
	
	// ============================c'tors==================================
	
	public MainCanvas(MapSet targetMapset, MapSet referenceMapSet, WinMain winMain, LinkSet links)
	{

		this.winMain = winMain;
		zoomHandler = new CanvasZoomHandler(this);
		this.links = links;
		setUpGenomes(targetMapset, referenceMapSet);
		makeTargetLinkSubSets();
		setBackground(Color.black);

		repaint();
	}
	
	// ============================methods==================================
	
	// initialises the genome objects we want to draw
	private void setUpGenomes(MapSet targetMapset, MapSet referenceMapSet)
	{
		// make new GMapSets from the map sets passed in
		targetGMapSet = new GMapSet(Color.RED, targetMapset, Constants.TARGET_GENOME, true, winMain.leftCanvasScroller, gMapLookup);
		referenceGMapSet = new GMapSet(Color.BLUE, referenceMapSet, Constants.REFERENCE_GENOME, false, winMain.rightCanvasScroller, gMapLookup);
		
		// add the genomes to the list
		gMapSetList = new LinkedList<GMapSet>();
		gMapSetList.add(targetGMapSet);
		gMapSetList.add(referenceGMapSet);
		
		// check which genome has more chromosomes
		maxChromos = 0;
		if (targetGMapSet.numMaps > referenceGMapSet.numMaps)
		{
			maxChromos = targetGMapSet.numMaps;
		}
		else
		{
			maxChromos = referenceGMapSet.numMaps;
		}
		
		// set the thresholds for marker painting here for now
		// TODO add method for calculating thresholds for marker painting automatically
		targetGMapSet.thresholdLinkedMarkerPainting = 4;
		referenceGMapSet.thresholdLinkedMarkerPainting = 4;
		targetGMapSet.thresholdAllMarkerPainting = 4;
		referenceGMapSet.thresholdAllMarkerPainting = 4;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------
	
	// paint the genomes or portions thereof onto this canvas
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		// need to clear the canvas before we draw
		clear(g);
		
		// check whether the user wants antialiasing on
		if (antiAlias)
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		else
		{
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}

		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		chromoSpacing = (int) ((canvasHeight / maxChromos) * 0.25f);
		
		// x position of genome 1 i.e. first column of chromos
		targetGMapSet.xPosition = (int) (canvasWidth * leftGenomeX);
		// x position of genome 2 (second column of chromos)
		referenceGMapSet.xPosition = (int) (canvasWidth * rightGenomeX);
		
		// work out the other coordinates needed
		// these are genome specific because we can have a different zoom factor for each genome
		// for each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			// the total amount of space we have for drawing on vertically, in pixels
			int availableSpaceVertically = canvasHeight - (chromoSpacing * 2);
			// the combined height of all the vertical spaces between chromosomes
			int allSpacers = chromoSpacing * (maxChromos - 1);
			// the height of a chromosome
			gMapSet.chromoHeight = (int) (((availableSpaceVertically - allSpacers) / maxChromos) * gMapSet.zoomFactor);
			// the total vertical extent of the genome, excluding top and bottom spacers
			gMapSet.totalY = (gMapSet.numMaps * gMapSet.chromoHeight) + ((gMapSet.numMaps - 1) * chromoSpacing);
			// the space at the top and bottom -- should be equal
			int topBottomSpacer = (canvasHeight - gMapSet.totalY) / 2;
			
			// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
			// this may be off the visible canvas in a northerly direction
			int currentY = 0;
			
			// this is what we do at a zoom factor of 1 (e.g. at startup)
			if (gMapSet.zoomFactor == 1)
			{
				// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
				currentY = topBottomSpacer;
				
				// set the scrollers to the correct position
				gMapSet.scroller.setValue(50);
				gMapSet.centerPoint = 50;
			}
			// this is what we do when we are zoomed in
			else
			{
				// need to convert the stored value for the offset (%) to pixels
				// this is because we may have a different zoomfactor each time we draw
				int offset = (int) (50 - gMapSet.centerPoint);
				int offsetPixels = (int) ((offset / 100.0f) * gMapSet.totalY);
				
				// start drawing at minus half the total height of the entire genome plus half the canvasheight and
				// plus the offset which can be positive or negative
				// the offset is the amount by which the user has moved the scrollbar
				// if the scrollbar has not been touched the offset will be zero
				currentY = -(gMapSet.totalY / 2) + canvasHeight / 2 + offsetPixels;
			}
			
			// width of chromosomes -- set this to a fixed fraction of the screen width for now
			int chromoWidth = Math.round(canvasWidth / 40);
			// check that this number is even
			boolean evenNumber = chromoWidth % 2 == 0;
			// if it isn't just add 1 -- otherwise we get into trouble with feature line widths exceeding the width of the chromosome
			if (!evenNumber)
				chromoWidth += 1;
			
			// now paint the chromosomes in this genome
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// we use the same x position for all chromosomes in this genome
				int x = gMapSet.xPosition;
				
				// the map draws itself from 0,0 always but we need move the origin of the graphics object to the actual
				// coordinates where we want things drawn
				g2.translate(x, currentY);
				
				// need to set the current height and width and coords on the chromomap before we draw it
				// this is purely so we have it stored somewhere
				gChromoMap.x = x;
				gChromoMap.y = currentY;
				gChromoMap.height = gMapSet.chromoHeight;
				gChromoMap.width = chromoWidth;
				// update its bounding rectangle (used for hit detection)
				gChromoMap.boundingRectangle.setBounds(gChromoMap.x, gChromoMap.y, gChromoMap.width,
								gChromoMap.height);
				
				// make sure we only draw the maps we need to (i.e. those that are at least partially visible)
				selectVisibleMaps();
				
				// get the map to draw itself (from 0,0 always)
				gChromoMap.paintMap(g2);
				
				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-x, -currentY);
				
				// increment the y position so we can draw the next one
				currentY += gMapSet.chromoHeight + chromoSpacing;
			}
		}
		
		// optionally draw lines between chromos
		if (linksToDraw == true)
		{
			drawLinks(g2);
		}
		
		// this optionally draws a rectangle delimiting a region we want to zoom in on
		if (drawSelectionRect)
		{
			g2.setColor(Color.red);
			// draw rectangle
			g2.drawRect(mousePressedX, mousePressedY, mouseDraggedX - mousePressedX,
							mouseDraggedY - mousePressedY);
		}
		
		// also need to update the overview canvases from here
//		winMain.fatController.updateOverviewCanvases();
	}

	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// display the homologies between chromosomes as lines
	public void processLinkDisplayRequest(int x, int y, boolean isCtrlClickSelection)
	{
		GChromoMap selectedMap = null;
		
		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.contains(x, y))
				{
					selectedMap = gChromoMap;
					break;
				}
			}
		}
		
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
				if (selectedMap.owningSet.equals(targetGMapSet))
				{
					// in that case we first clear out the existing vector of selected maps in the target genome
					targetGMapSet.deselectAllMaps();
					// then we add the selected map only
					targetGMapSet.addSelectedMap(selectedMap);
					// now add ALL maps into the vector of selected elements for the reference genome so the links can be drawn
					referenceGMapSet.selectAllMaps();
					linksToDraw = true;
				}
			}
			
			// now check whether we have selected chromosomes in the target genome
			if (targetGMapSet.selectedMaps.size() > 0)
			{
				linksToDraw = true;
			}
			// if not, we don't want to draw links, just display the selected outlines of the reference genome chromsomes
			else
			{
				linksToDraw = false;
			}
			
		}
		// no hit detected
		else
		{
			// don't draw links
			linksToDraw = false;
			// reset the selectedMaps vectors in both genomes -- this removes the highlight frames from the chromosomes
			for (GMapSet mapSet : gMapSetList)
			{
				mapSet.deselectAllMaps();
			}
		}
		
		repaint();
		
		// update overviews
		winMain.fatController.updateOverviewCanvases();
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// clears the canvas completely
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// checks through all available chromosome maps and set their booleans according to whether they are visible on the canvas or not
	public void selectVisibleMaps()
	{
		// create a bounding rectangle the size of the currently visible canvas
		Rectangle canvasBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
		// for all gmapsets
		for (GMapSet gMapSet : gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the bounding rectangle of the map intersects with the canvas bounding rectangle or whether it is contained in it
				// set the boolean accordingly
				if (canvasBounds.contains(gChromoMap.boundingRectangle) || canvasBounds.intersects(gChromoMap.boundingRectangle))
					gChromoMap.isShowingOnCanvas = true;
				else
					gChromoMap.isShowingOnCanvas = false;
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	public void drawLinks(Graphics2D g2)
	{
		// check whether we have selected chromosomes in the target genome
		// if not, we do not want to draw any links at all
		if (targetGMapSet.selectedMaps.size() > 0)
		{
			// for each map in the selectedMaps vector of the target genome
			for (int i = 0; i < targetGMapSet.selectedMaps.size(); i++)
			{
				GChromoMap selectedMap = targetGMapSet.selectedMaps.get(i);
				// get the ChromoMap for the currently selected chromosome
				ChromoMap selectedChromoMap = selectedMap.chromoMap;
				// get all the links between the selected chromosome and the reference mapset
				LinkedList<LinkSet> linkSets = linkSetLookup.get(selectedChromoMap);
				float targetMapStop = selectedChromoMap.getStop();
				// get the real coordinates for the selected chromo and the reference chromo
				int selectedChromoX = targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width;
				int selectedChromoY = selectedMap.y;
				int referenceChromoX = referenceGMapSet.xPosition;
				Color[] colours = Utils.makeColours(maxChromos);
				for (LinkSet selectedLinks : linkSets)
				{
					// check whether this is a linkset we want to draw
					// this depends on which chromosome in the reference genome it points to
					// the linksets are ordered by chromosome index
					int linkSetIndex = linkSets.indexOf(selectedLinks);
					
					// check whether this index matches one of the ones in the vector of selected maps in the reference genome
					boolean draw = false;
					for (GChromoMap gMap : referenceGMapSet.selectedMaps)
					{
						if (gMap.index == linkSetIndex)
							draw = true;
					}
					
					if (draw)
					{
						// set the colour to grey
						g2.setColor(new Color(150, 150, 150));
						
						// for each link in the linkset
						for (Link link : (LinkSet) selectedLinks)
						{
							// we only want to draw this link if it has a BLAST e-value smaller than the cut-off currently selected by the user
							if (link.getBlastScore() <= winMain.controlPanel.blastThreshold)
							{
								
								// get the positional data of feature1 (which is on the selected chromo) and the end point of the map
								float feat1Start = link.getFeature1().getStart();
								
								// get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
								float feat2Start = link.getFeature2().getStart();
								ChromoMap owningMap = link.getFeature2().getOwningMap();
								
								float referenceMapStop = owningMap.getStop();
								int refChromoIndex = owningMap.getOwningMapSet().getMaps().indexOf(
												owningMap);
								int referenceChromoY = referenceGMapSet.gMaps.get(refChromoIndex).y;
								
								// convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
								int targetY = (int) (feat1Start / (targetMapStop / targetGMapSet.gMaps.get(0).height)) + selectedChromoY;
								int referenceY = (int) (feat2Start / (referenceMapStop / referenceGMapSet.gMaps.get(0).height)) + referenceChromoY;
								
								// draw the line
								g2.drawLine(selectedChromoX + 1, targetY,
												referenceChromoX - 1,
												referenceY);
							}
						}
					}
				}
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method precomputes subsets of links between each target chromosome and the reference genome so that drawing them is quicker.
	 */
	private void makeTargetLinkSubSets()
	{
		try
		{
			linkSetLookup = new Hashtable<ChromoMap, LinkedList<LinkSet>>();
			
			MapSet targetMapSet = links.getMapSets().get(0);
			MapSet referenceMapSet = links.getMapSets().get(1);
			
			// for each chromosome in the target mapset
			for (ChromoMap targetMap : targetMapSet)
			{
				// create a new LinkedList which holds all the linksets of links between this chromosome and the reference chromosomes
				LinkedList<LinkSet> linkSets = new LinkedList<LinkSet>();
				// for each reference chromosome
				for (ChromoMap refMap : referenceMapSet)
				{
					// make a linkset that contains only the links between this chromo and the target chromo
					LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
					// add the linkset to the list
					linkSets.add(linkSubset);
				}
				
				// then add the list to the hashtable
				linkSetLookup.put(targetMap, linkSets);
			}
			
			// now set up the lists of linked-to features for each of the gchromomaps
			initLinkedFeatureLists();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	private void initLinkedFeatureLists()
	{
		// this link set holds the all the possible links between all chromos in the target set and
		// all chromos in the reference set
		
		// for each link in the overall link set
		for (Link link : links)
		{
			// get both features and add them to a list
			LinkedList<Feature> features = new LinkedList<Feature>();
			features.add(link.getFeature1());
			if (link.getFeature2() != null)
				features.add(link.getFeature2());
			// for each feature
			for (Feature feature : features)
			{
				// get its owning map
				ChromoMap cMap = feature.getOwningMap();
				// get the corresponding GChromoMap object
				GChromoMap gMap = gMapLookup.get(cMap);
				// add the feature to its list of linked features
				if (gMap != null)
					gMap.linkedFeatureList.add(feature);
			}
		}
		
		// now need to update the GChromoMap objects and init their arrays for drawing
		for (GMapSet gMapSet : gMapSetList)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				gChromoMap.initLinkedFeatureArrays();
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// used to scroll up and down the canvas
	public void moveGenomeViewPort(GMapSet gMapSet, int newCenterPoint)
	{
		// update the centerpoint to the new percentage
		gMapSet.centerPoint = newCenterPoint;
		gMapSet.scroller.setValue(newCenterPoint);
		System.out.println("new centerpoint = " + newCenterPoint);
		repaint();
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void checkMarkerPaintingThresholds(GMapSet selectedSet)
	{
		// check whether we need to display markers and labels
		if (selectedSet.zoomFactor > selectedSet.thresholdAllMarkerPainting)
		{
			selectedSet.paintAllMarkers = true;
			selectedSet.paintLabels = true;
		}
		else if (selectedSet.zoomFactor > selectedSet.thresholdLinkedMarkerPainting && selectedSet.zoomFactor < selectedSet.thresholdAllMarkerPainting)
		{
			selectedSet.paintAllMarkers = false;
			selectedSet.paintLinkedMarkers = true;
			selectedSet.paintLabels = true;
		}
		else
		{
			selectedSet.paintAllMarkers = false;
			selectedSet.paintLinkedMarkers = false;
			selectedSet.paintLabels = false;
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
}// end class
