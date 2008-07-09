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
	int chromoSpacing = 15;
	
	// the height of a chromosome and a vertical spacer interval combined
	int chromoUnit;
	
	// a value for a minimum space in pixels between the topmost and bottommost chromosomes and the edge of the canvas
	int minVertBuffer = 50;
	
	// these variables determine where the genomes appear on the canvas on the x axis (scaled to 0-1)
	// position is relative to frame size
	float leftGenomeX = 0.3f;
	float rightGenomeX = 0.7f;
	
	// threshold values for the zoom factor above which we want to display markers and labels
	float thresholdMarkerPainting = 3;
	float thresholdLabelPainting = 3;
	
	// index of a chromosome in the target map set that we want to see links from
	int selectedChromoIndex = -1;
	
	// a hashtable that contains chromomaps from the target genome as keys and LinkedList objects as values which in
	// turn hold a list of LinkSet objects each, where each Linkset represents the links between the chromomap and a chromomap in the reference
	// genome
	Hashtable<ChromoMap, LinkedList<LinkSet>> linkSetLookup;
	
	// a hashtable that holds ChromoMap objects as keys and their corresponding GChromoMap objects as values
	Hashtable<ChromoMap, GChromoMap> gMapLookup = new Hashtable<ChromoMap, GChromoMap>();
	
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	private static LinkSet links = null;
	
	// ============================c'tors==================================
	
	public MainCanvas(MapSet targetMapset, MapSet referenceMapSet, WinMain winMain, LinkSet links)
	{
		this.winMain = winMain;
		this.links = links;
		setUpGenomes(targetMapset, referenceMapSet);
		makeLinkSubSets();
		setBackground(Color.black);
	}
	
	// ============================methods==================================
	
	// initialises the genome objects we want to draw
	private void setUpGenomes(MapSet targetMapset, MapSet referenceMapSet)
	{
		// make new GMapSets from the map sets passed in
		// TODO remove hardcoding
		targetGMapSet = new GMapSet(Color.RED, targetMapset, Constants.TARGET_GENOME, "Barley", true, winMain.leftCanvasScroller, gMapLookup);
		referenceGMapSet = new GMapSet(Color.BLUE, referenceMapSet, Constants.REFERENCE_GENOME, "Rice", false, winMain.rightCanvasScroller, gMapLookup);
		
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
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------
	
	// paint the genomes or portions thereof onto this canvas
	public void paintComponent(Graphics g)
	{
		// need to clear the canvas before we draw
		clear(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		// x position of genome 1 i.e. first column of chromos
		targetGMapSet.xPosition = (int) (canvasWidth * leftGenomeX);
		// x position of genome 2 (second column of chromos)
		referenceGMapSet.xPosition = (int) (canvasWidth * rightGenomeX);
		
		// work out the other coordinates needed
		// these are genome specific because we can have a different zoom factor for each genome
		// for each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			// chromoUnit is the combined height of a chromosome and the space below it extending to the next chromosome in the column
			chromoUnit = canvasHeight / (maxChromos + 1); // adding 1 gives us buffer space between chromosomes vertically
			
			// apply zoom factor
			chromoUnit = (int) (chromoUnit * gMapSet.zoomFactor);
			
			// height of chromosomes
			int chromoHeight = chromoUnit - chromoSpacing;
			
			// now need to work out where we start painting
			// this can be a negative value
			// need to multiply the current chromosome height with the number of chromos
			// this plus the spaces between the chromosomes gives us the total number of pixels we draw
			// regardless of whether this is on the canvas or off
			gMapSet.totalY = gMapSet.numMaps * chromoUnit;
			
			// first set the distance from the top of the frame to the top of the first chromo
			int spacer = (canvasHeight - (gMapSet.numMaps * chromoUnit)) / 2;
			
			// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
			// this may be off the visible canvas in a northerly direction
			int currentY = 0;
			
			// this is what we do at a zoom factor of 1 (e.g. at startup)
			if (gMapSet.zoomFactor == 1)
			{
				// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
				currentY = spacer;
				
				// set the scrollers to the correct position
				gMapSet.scroller.setValue(50);
				gMapSet.centerPoint = 50;
			}
			// this is what we do when we are zoomed in
			else
			{
				// need to convert the stored value for the offset (%) to pixels
				// this is because we may have a different zoomfactor each time we draw
				int offset = 50 - gMapSet.centerPoint;
				int offsetPixels = (int) ((offset / 100.0f) * gMapSet.totalY);
				
				// start drawing at minus half the total height of the entire genome plus half the canvasheight and
				// plus the offset which can be positive or negative
				// the offset is the amount by which the user has moved the scrollbar
				// if the scrollbar has not been touched the offset will be zero
				currentY = -(gMapSet.totalY / 2) + canvasHeight / 2 + offsetPixels;
			}
			
			// width of chromosomes -- set this to a fixed fraction of the screen width for now
			int chromoWidth = canvasWidth / 60;
			
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
				gChromoMap.height = chromoHeight;
				gChromoMap.width = chromoWidth;
				// update its bounding rectangle (used for hit detection)
				gChromoMap.boundingRectangle.setBounds(x, currentY, chromoWidth * 2, chromoHeight);
				
				// make sure we only draw the maps we need to (i.e. those that are at least partially visible)
				selectVisibleMaps();
				
				// get the map to draw itself (from 0,0 always)
				gChromoMap.paintMap(g2);
				
				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-x, -currentY);
				
				// increment the y position so we can draw the next one
				currentY += chromoUnit;
			}
			
			// optionally draw lines between chromos
			if (selectedChromoIndex != -1)
			{
				drawLinks(g2);
			}
			
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// gets invoked when the zoom is adjusted by using the sliders or the drag-and-zoom functionality
	// adjusts the zoom factor and checks whether we need to now display markers and labels
	public void processSliderZoomRequest(float zoomFactor, int genomeIndex)
	{
		GMapSet selectedSet = gMapSetList.get(genomeIndex);
		selectedSet.zoomFactor = zoomFactor;
		
		// determine whether we need to draw markers and labels
		if (zoomFactor > thresholdMarkerPainting)
		{
			selectedSet.paintMarkers = true;
		}
		else
		{
			selectedSet.paintMarkers = false;
		}
		
		if (zoomFactor > thresholdLabelPainting)
		{
			selectedSet.paintLabels = true;
		}
		else
		{
			selectedSet.paintLabels = false;
		}
		
		// make sure the zoom factor currently displayed is up to date
		winMain.zoomControlPanel.updateZoomInfo();
		
		repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// display the homologies between chromosomes as lines
	public void processLinkDisplayRequest(int x, int y)
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
			// set the selected chromo index if the selected chromo is in the target map set
			if (selectedMap.owningSet.equals(gMapSetList.get(0)))
			{
				this.selectedChromoIndex = selectedMap.index;
			}
		}
		else
		{
			// no map selected -- reset index of selected map to -1
			selectedChromoIndex = -1;
		}
		
		repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// zooms in by a fixed amount on a chromosome the user clicked on
	public void processClickZoomRequest(int x, int y)
	{
		GChromoMap selectedMap = Utils.getSelectedMap( gMapSetList, x, y);
		
		// the click has hit a chromosome
		if (selectedMap != null)
		{		
			GMapSet selectedSet = selectedMap.owningSet;
			
			// set the selected chromo index if the selected chromo is in the target map set
			if (selectedSet.equals(gMapSetList.get(0)))
			{
				this.selectedChromoIndex = selectedMap.index;
			}
			
			// figure out the genome it belongs to and increase that genome's zoom factor so that we can just fit an entire 
			selectedSet.zoomFactor = maxChromos;

			// update the centerpoint to the new percentage
			selectedSet.centerPoint = 100/(selectedSet.numMaps) * (selectedSet.gMaps.indexOf(selectedMap));
			System.out.println("selectedSet.centerPoint uncorrected = "+ selectedSet.centerPoint );
			
			//convert half the canvas height to a percentage of the total and add this
			int combinedSpacers = chromoSpacing*selectedSet.numMaps-1;
			int newTotalY = ((selectedSet.totalY - combinedSpacers)*maxChromos) + combinedSpacers;			
			int halfCanvasHeightPercent = (int) (((float)(getHeight()/2)/newTotalY) * 100);

			//now set the new centerpoint for the genome
			selectedSet.centerPoint = selectedSet.centerPoint + halfCanvasHeightPercent;			
			selectedSet.scroller.setValue(selectedSet.centerPoint);
		
			// check whether we need to display markers and labels
			if (selectedSet.zoomFactor > thresholdMarkerPainting && selectedMap.isShowingOnCanvas)
			{
				selectedSet.paintMarkers = true;
			}
			if (selectedSet.zoomFactor > thresholdLabelPainting && selectedMap.isShowingOnCanvas)
			{
				selectedSet.paintLabels = true;
			}
			
			// make sure the zoom factor currently displayed is up to date
			winMain.zoomControlPanel.updateZoomInfo();
			
			// repaint the canvas
			repaint();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// clears the canvas completely
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// checks through all avaliable chromosome maps and set their booleans according to whether they are visible on the canvas or not
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
		// get the map for the currently selected chromosome
		MapSet targetMapSet = this.targetGMapSet.mapSet;
		ChromoMap selectedMap = targetMapSet.getMap(selectedChromoIndex);
		
		// get all the links between the selected chromosome and the reference mapset
		LinkedList<LinkSet> linkSets = linkSetLookup.get(selectedMap);
		
		float targetMapStop = selectedMap.getStop();
		// get the real coordinates for the selected chromo and the reference chromo
		int selectedChromoX = targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width;
		int selectedChromoY = targetGMapSet.gMaps.get(selectedChromoIndex).y;
		int referenceChromoX = referenceGMapSet.xPosition;
		
		Color[] colours = Utils.makeColours(maxChromos);
		
		for (Object selectedLinks : linkSets)
		{
			// change the colour of the graphics object so that each link subset is colour coded
			int index = linkSets.indexOf(selectedLinks);
			g2.setColor(colours[index]);
			
			// for each link in the linkset
			for (Link link : (LinkSet) selectedLinks)
			{
				// get the positional data of feature1 (which is on the selected chromo) and the end point of the map
				float feat1Start = link.getFeature1().getStart();
				
				// get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
				float feat2Start = link.getFeature2().getStart();
				ChromoMap owningMap = link.getFeature2().getOwningMap();
				float referenceMapStop = owningMap.getStop();
				int refChromoIndex = owningMap.getOwningMapSet().getMaps().indexOf(owningMap);
				int referenceChromoY = referenceGMapSet.gMaps.get(refChromoIndex).y;
				
				// convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
				int targetY = (int) (feat1Start / (targetMapStop / targetGMapSet.gMaps.get(0).height)) + selectedChromoY;
				int referenceY = (int) (feat2Start / (referenceMapStop / referenceGMapSet.gMaps.get(0).height)) + referenceChromoY;
				
				// draw the line
				g2.drawLine(selectedChromoX, targetY, referenceChromoX, referenceY);
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method precomputes subsets of links between each target chromosome and the reference genome so that drawing them is quicker.
	 */
	private void makeLinkSubSets()
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
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	private void initLinkedFeatureLists()
	{
		// this link set holds the all the possible links between all chromos in the target set and
		// all chromos in the reference set
		// private static LinkSet links = null;
		
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
//				else
//					System.out.println("unassigned feature found");
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
		repaint();
	}
	// -----------------------------------------------------------------------------------------------------------------------------------
}// end class
