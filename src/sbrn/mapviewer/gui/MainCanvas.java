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
	
	//the parent component
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
	float leftGenomeX = 0.35f;
	float rightGenomeX = 0.65f;
	
	//threshold values for the zoom factor above which we want to display markers and labels
	float thresholdMarkerPainting = 6;
	float thresholdLabelPainting = 6;
	
	// ============================c'tors==================================
	
	public MainCanvas(MapSet targetMapset, MapSet referenceMapSet,WinMain winMain)
	{
		this.winMain = winMain;
		setUpGenomes(targetMapset, referenceMapSet);
		setBackground(Color.black);
	}
	
	// ============================methods==================================
	
	private void setUpGenomes(MapSet targetMapset, MapSet referenceMapSet)
	{
		// make new GMapSets from the map sets passed in
		// TODO remove hardcoding
		targetGMapSet = new GMapSet(Color.RED, targetMapset, Constants.TARGET_GENOME, "Barley");
		referenceGMapSet = new GMapSet(Color.BLUE, referenceMapSet, Constants.REFERENCE_GENOME, "Rice");
		
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
				// System.out.println("currentY = " + currentY);
			}
			
			// width of chromosomes -- set this to a fixed fraction of the screen width initially
			int chromoWidth = canvasWidth / 150;
			// increase the width very slightly as the zoom factor increases -- log works best here
			if (gMapSet.zoomFactor > 1)
				chromoWidth = (int) (chromoWidth + (Math.log(gMapSet.zoomFactor)*3));
			
			// System.out.println("current chromo width and height = " + chromoWidth + "," + chromoHeight);
			
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
				
				// get the map to draw itself (from 0,0 always)
				gChromoMap.paintMap(g2);
				
				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-x, -currentY);
				
				// increment the y position so we can draw the next one
				currentY += chromoUnit;
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void processSliderZoomRequest(int zoomFactor, int genomeIndex)
	{	
		GMapSet selectedSet = gMapSetList.get(genomeIndex);
		selectedSet.zoomFactor = zoomFactor;
		if(zoomFactor > thresholdMarkerPainting)
		{
			selectedSet.paintMarkers = true;
		}
		else
		{
			selectedSet.paintMarkers = false;
		}
		
		if(zoomFactor > thresholdLabelPainting)
		{
			selectedSet.paintLabels = true;
		}
		else
		{
			selectedSet.paintLabels = false;
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
			// figure out the genome it belongs to and increase that genome's zoom factor
			selectedMap.owningSet.zoomFactor = selectedMap.owningSet.zoomFactor * 2;
			System.out.println("new zoom factor for genome " + selectedMap.owningSet.name + " = " + selectedMap.owningSet.zoomFactor);
			
			//check whether we need to display markers and labels
			if(selectedMap.owningSet.zoomFactor > thresholdMarkerPainting)
			{
				selectedMap.owningSet.paintMarkers = true;
			}
			if(selectedMap.owningSet.zoomFactor > thresholdLabelPainting)
			{
				selectedMap.owningSet.paintLabels = true;
			}
			
			//make sure the zoom factor currently displayed is up to date
			winMain.zoomControlPanel.updateZoomInfo();
			
			// repaint the canvas
			repaint();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// private void initChromoLookup()
	// {
	// // init the lookup table with the bounding rectangles
	// chromoLookup = new HashMap<Rectangle, GChromoMap>();
	// // for each map set
	// for (GMapSet gMapSet : gMapSetList)
	// {
	// // for each chromosome in the mapset
	// for (GChromoMap gChromoMap : gMapSet.gMaps)
	// {
	// // add the bounding rectangle and map to the lookup
	// chromoLookup.put(gChromoMap.boundingRectangle, gChromoMap);
	// System.out.println("coords for bounding rect of map " + gChromoMap.name + " = " + gChromoMap.boundingRectangle.getX() + "," + gChromoMap.boundingRectangle.getY() +
	// "," + gChromoMap.boundingRectangle.getWidth() + "," + gChromoMap.boundingRectangle.getHeight());
	// }
	// }
	// }
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
}
