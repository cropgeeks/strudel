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
	LinkedList<GMapSet> gMapSetList;
	
	// size of the frame
	int canvasHeight;
	int canvasWidth;
	
	// size of the chromos
	int chromoHeight;
	
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
	float thresholdMarkerPainting = 8;
	float thresholdLabelPainting = 8;
	
	//index of a chromosome in the target map set that we want to see links from
	int selectedChromoIndex = -1;
	
	// a hashtable that contains chromomaps from the target genome as keys and LinkedList objects as values which in
	// turn hold a list of LinkSet objects each, where each Linkset represents the links between the chromomap and a chromomap in the reference
	// genome
	Hashtable<ChromoMap, LinkedList> linkSetLookup;
	
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	private static LinkSet links = null;
	
	// ============================c'tors==================================
	
	public MainCanvas(MapSet targetMapset, MapSet referenceMapSet, WinMain winMain,LinkSet links)
	{
		this.winMain = winMain;
		this.links = links;
		setUpGenomes(targetMapset, referenceMapSet);
		makeLinkSubSets();
		setBackground(Color.black);
	}
	
	// ============================methods==================================
	
	private void setUpGenomes(MapSet targetMapset, MapSet referenceMapSet)
	{
		// make new GMapSets from the map sets passed in
		// TODO remove hardcoding
		targetGMapSet = new GMapSet(Color.RED, targetMapset, Constants.TARGET_GENOME, "Barley", true);
		referenceGMapSet = new GMapSet(Color.BLUE, referenceMapSet, Constants.REFERENCE_GENOME, "Rice", false);
		
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
	
	public void paintComponent(Graphics g)
	{
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
		
		// work out the other coords accordingly:
		// these are genome specific because we can have a different zoom factor for each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			// the combined height of a chromosome and the space below it extending to the next chromosome in the column
			chromoUnit = canvasHeight / (maxChromos + 1); // adding 1 gives us a buffer space
			
			// zoom
			chromoUnit = (int) (chromoUnit * gMapSet.zoomFactor);
			
			// height of chromosomes
			chromoHeight = chromoUnit - chromoSpacing;
			
			// now work out the y positions for each chromosome in each genome
			
			// first set the distance from the top of the frame to the top of the first chromo
			int spacer = (canvasHeight - (gMapSet.numMaps * chromoUnit)) / 2;
			
			// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
			int currentY = -1;
			if (gMapSet.zoomFactor == 1)
				currentY = spacer;
			else
			{
				// now need to work out where we start painting
				// this can be a negative value
				// need to multiply the current chromosome height with the number of chromos
				// this plus the spaces between the chromosomes gives us the total number of pixels we draw
				// regardless of whether this is on the canvas or off
				int totalY = gMapSet.numMaps * chromoUnit;
				
				// testing only: center the genome in the center of the canvas each time we zoom in
				currentY = -(totalY / 2) + canvasHeight / 2;
			}
			
			// width of chromosomes -- set this to a fixed fraction of the screen width initially
			int chromoWidth = canvasWidth / 150;
//			// increase the width very slightly as the zoom factor increases -- log works best here
//			if (gMapSet.zoomFactor > 1)
//			chromoWidth = (int) (chromoWidth + (Math.log(gMapSet.zoomFactor) * 3));
			
			//System.out.println("current chromo width and height = " + chromoWidth + "," + chromoHeight + " , mapset " + gMapSet.name);
			
			// now paint the chromosomes
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// we use the same x position for all chromosomes in this genome
				int x = gMapSet.xPosition;
				
				// move the origin of the Graphics object to the desired current position of the map
				g2.translate(x, currentY);
				
				// need to set the current height and width and coords on the map before we draw it
				// this is purely so that we have it stored somewhere
				// the map itself draws itself from 0,0 always but we move the origin of the graphics object to the actual
				// coordinates where we want things drawn
				gChromoMap.x = x;
				gChromoMap.y = currentY;
				gChromoMap.height = chromoHeight;
				gChromoMap.width = chromoWidth;
				// update the bounding rectangle
				gChromoMap.boundingRectangle.setBounds(x, currentY, chromoWidth * 2, chromoHeight);
				
				//make sure we only draw the maps we need to (i.e. those that are visible)
				checkForVisibleMaps();
				
				// get the map to draw itself (from 0,0 always)
				gChromoMap.paintMap(g2);
				
				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-x, -currentY);
				
				// increment the y position so we can draw the next one
				currentY += chromoUnit;
			}
			
			// optionally draw lines between chromos if mouse over has been detected
			if (selectedChromoIndex != -1)
			{
				drawLinks(g2);
			}
			
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void processSliderZoomRequest(float zoomFactor, int genomeIndex)
	{
		GMapSet selectedSet = gMapSetList.get(genomeIndex);
		selectedSet.zoomFactor = zoomFactor;
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
//					System.out.println("=========hit in rect " + gChromoMap.boundingRectangle);
					selectedMap = gChromoMap;
//					System.out.println("processing link display request for chromosome " + selectedMap.name);
					break;
				}
			}
		}
		
		// the click has hit a chromosome
		if (selectedMap != null)
		{			
			//set the selected chromo index if the selected chromo is in the target map set
			if(selectedMap.owningSet.equals(gMapSetList.get(0)))
			{
				System.out.println("setting index of selected chromo to " + selectedMap.index);
				this.selectedChromoIndex = selectedMap.index;
			}
		}
		else
		{
			//no map selected -- reset index of selected map to -1
			selectedChromoIndex = -1;
		}
		
		repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void processClickZoomRequest(int x, int y)
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
					System.out.println("=========hit in rect " + gChromoMap.boundingRectangle);
					selectedMap = gChromoMap;
					System.out.println("processing click zoom request for chromosome " + selectedMap.name);
					break;
				}
			}
		}
		
		// the click has hit a chromosome
		if (selectedMap != null)
		{
			
			//set the selected chromo index if the selected chromo is in the target map set
			if(selectedMap.owningSet.equals(gMapSetList.get(0)))
			{
				this.selectedChromoIndex = selectedMap.index;
			}
			
//			// figure out the genome it belongs to and increase that genome's zoom factor
//			selectedMap.owningSet.zoomFactor = selectedMap.owningSet.zoomFactor * 2;
//			System.out.println("new zoom factor for genome " + selectedMap.owningSet.name + " = " + selectedMap.owningSet.zoomFactor);
//			
//			// check whether we need to display markers and labels
//			if (selectedMap.owningSet.zoomFactor > thresholdMarkerPainting && selectedMap.isShowingOnCanvas)
//			{
//			selectedMap.owningSet.paintMarkers = true;
//			}
//			if (selectedMap.owningSet.zoomFactor > thresholdLabelPainting && selectedMap.isShowingOnCanvas)
//			{
//			selectedMap.owningSet.paintLabels = true;
//			}
//			
//			// make sure the zoom factor currently displayed is up to date
//			winMain.zoomControlPanel.updateZoomInfo();
//			
//			// repaint the canvas
//			repaint();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// check through all avaliable chromosome maps and set their booleans according to whether they are visible on the canvas or not
	public void checkForVisibleMaps()
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
				if(canvasBounds.contains(gChromoMap.boundingRectangle) || canvasBounds.intersects(gChromoMap.boundingRectangle))
					gChromoMap.isShowingOnCanvas = true;
				else
					gChromoMap.isShowingOnCanvas = false;
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	 */
	public void drawLinks(Graphics2D g2)
	{
		// System.out.println("drawing lines for selected chromosome " + selectedChromoIndex);
		
		// get the map for the currently selected chromosome
		MapSet targetMapSet = this.targetGMapSet.mapSet;
		ChromoMap selectedMap = targetMapSet.getMap(selectedChromoIndex);
		
		// get all the links between the selected chromosome and the reference mapset
		LinkedList<LinkSet> linkSets = linkSetLookup.get(selectedMap);
		
		float targetMapStop = selectedMap.getStop();
		// get the real coordinates for the selected chromo and the reference chromo
		int selectedChromoX = targetGMapSet.xPosition + targetGMapSet.gMaps.get(0).width * 2;
		int selectedChromoY = targetGMapSet.gMaps.get(selectedChromoIndex).y;
		int referenceChromoX = referenceGMapSet.xPosition;
		
		Color [] colours = Utils.makeColours(maxChromos);
		
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
		linkSetLookup = new Hashtable<ChromoMap, LinkedList>();
		
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
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
}// end class
