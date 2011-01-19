package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;

/**
 * Class representing a genome graphically. Holds a list of GChromoMap objects, which are graphical representations of chromosomes.
 */
public class GMapSet
{
	// ====================================vars==============================================

	// the color we use for painting this genome
	public Color colour;

	// a list with the maps (chromosomes) contained in this genome
	public LinkedList<GChromoMap> gMaps;

	// the number of chromosomes in this genome
	public int numMaps;

	// the map set that contains the data we want to show
	public MapSet mapSet;

	// the name of the genome
	public String name;

	// the current zoom factor for the display of this genome
	public float zoomFactor = 1;

	// the x position of this genome on the canvas
	// i.e. an x coordinate in pixels which denotes the left most edge of the genome drawn
	public float xPosition;

	// the percent offset from the top of the genome that represents the topmost point of the genome visible on the canvas
	public int drawingOffset = 0;

	// the point on the genome that is currently seen in the center of the canvas, in pixels, measured from the top of the genome
	//n.b. when we are zoomed in to beyond a level where we can see the entire genome the genome height will be
	//greater than the height of the visible part of the main canvas
	public int centerPoint = 0;

	// the total current height of the genome as drawn on the canvas, in pixels
	public int totalY;

	// this controls whether we draw chromosome markers
	public boolean paintAllMarkers = false;

	//the zoom control panel pertaining to this mapset
	public ZoomControlPanel zoomControlPanel;

	//true if all the chromosomes belonging to this mapset have been selected
	public boolean wholeMapsetIsSelected = false;

	// space the chromosomes vertically by this fixed amount
	public int chromoSpacing = 10;

	//true while we are scrolling
	public boolean isScrolling = false;

	//this indicates whether this map set has been scrolled at all
	//we need so we know whether to recenter the mapset when fully zoomed out
	public boolean hasBeenScrolled = false;

	//the minimum height of a chromosome that we default to when we have more chromosomes than we have space for on the canvas
	//chromos will then fall off the screen above and below
	public int minChromoHeight = 12;

	public OverviewCanvas overviewCanvas;

	//the shortest map in this mapset
	GChromoMap shortestMap = null;

	//the longest map in this mapset
	GChromoMap longestMap = null;

	//the max zoom factor for this mapset -- can be overridden by the user with the spinner control at the bottom of the screen
	public int maxZoomFactor = Constants.MAX_ZOOM_FACTOR;

	//true if this mapset is oversized i.e. we do not have enough pixels to render all maps on screen at the same time
	public boolean isOversized = false;

	// ====================================c'tor========================================

	public GMapSet(MapSet mapSet)
	{
		this.mapSet = mapSet;
		this.name = mapSet.getName();
		numMaps = mapSet.size();

		// init the list of maps contained in this genome
		initialise();
	}

	// ========================================methods==================================

	// init the list of maps contained in this genome
	public void initialise()
	{
		gMaps = new LinkedList<GChromoMap>();

		for (int i = 0; i < mapSet.size(); i++)
		{
			ChromoMap cMap = mapSet.getMaps().get(i);
			GChromoMap gMap = new GChromoMap(cMap.getName(), i, this);
			gMaps.add(gMap);
			gMap.initialIndex = i;

			//also check whether this gMap is shorter than the current shortest map
			if(shortestMap == null && gMap.chromoMap.getStop() != 0)
				shortestMap = gMap;
			else if(shortestMap != null && gMap.chromoMap.getStop() < shortestMap.chromoMap.getStop() && gMap.chromoMap.getStop() != 0)
			{
				shortestMap = gMap;
			}

			//ditto for longest
			if(longestMap == null)
				longestMap = gMap;
			else
			{
				if(gMap.chromoMap.getStop() > longestMap.chromoMap.getStop())
					longestMap = gMap;
			}

		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public Vector<GChromoMap> getVisibleMaps()
	{
		Vector<GChromoMap> visibleMaps = new Vector<GChromoMap>();

		// for all gchromomaps within each mapset
		for (GChromoMap gChromoMap : gMaps)
		{
			if(gChromoMap.isShowingOnCanvas == true)
				visibleMaps.add(gChromoMap);
		}
		return visibleMaps;
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	//this works out the sizes of all maps in this mapset
	//depending on the user preference this will either make all of them the same or scale them to relative size
	public void calculateMapSizes()
	{
//		System.out.println("+++++++calculateMapSizes for mapset " + name);
//		System.out.println("shortest map for mapset " + name + " = " + shortestMap.name);
//		System.out.println("length = " + shortestMap.chromoMap.getStop());
//		System.out.println("longest map for mapset " + name + " = " + longestMap.name);
//		System.out.println("length = " + longestMap.chromoMap.getStop());

		// the total amount of space we have for drawing on vertically, in pixels
		int availableVerticalSpace = Strudel.winMain.mainCanvas.getHeight() - (Strudel.winMain.mainCanvas.topBottomSpacer * 2);

		// the combined height of all the vertical spaces between chromosomes
		int allSpacers = chromoSpacing * (gMaps.size() - 1);

		//this is the number of pixels we have available for actual chromosomes, minus the spacers
		int spaceForChromos = availableVerticalSpace - allSpacers;

		if(spaceForChromos <=0)
			spaceForChromos = availableVerticalSpace;

		//add up all the units in the mapset
		float totalUnits = 0;
		for(GChromoMap map : gMaps)
			totalUnits += map.chromoMap.getStop();
		float averageChromoUnits = totalUnits/gMaps.size();

		//this is the number of pixels  we have available per unit on the chromosome
		float pixelsPerUnit = spaceForChromos/totalUnits;

		//now lets check that the rendering height of the smallest chromo is not below our min chromo height
		int shortestChromoHeight = Math.round(shortestMap.chromoMap.getStop() * pixelsPerUnit);

//		System.out.println("availableVerticalSpace = " + availableVerticalSpace);
//		System.out.println("allSpacers = " + allSpacers);
//		System.out.println("spaceForChromos = " + spaceForChromos);
//		System.out.println("totalUnits = " + totalUnits);
//		System.out.println("shortestChromoHeight = " + shortestChromoHeight);
//		System.out.println("pixelsPerUnit = " + pixelsPerUnit);

		//calculate a scaling factor that will scale all chromos so that none of them ends up shorter than our min chromo height
		float scalingFactor = 1;
		if(shortestChromoHeight < minChromoHeight)
		{
			if(shortestChromoHeight == 0)
				scalingFactor = minChromoHeight;
			else
				scalingFactor = minChromoHeight / shortestChromoHeight;

			overviewCanvas.renderAsOneChromo = true;
			isOversized = true;
		}
		else
			isOversized = false;

		//reset totalY
		totalY = 0;

		//now set all the map sizes
		for(GChromoMap map : gMaps)
		{
			int scaledMapHeight = -1;

			//set the map height appropriately
			if(Prefs.scaleChromosByRelativeSize)
				scaledMapHeight =  Math.round((map.chromoMap.getStop() * pixelsPerUnit) * scalingFactor);
			else
				scaledMapHeight = Math.round(averageChromoUnits * pixelsPerUnit);

			//don't let the map height fall below our cutoff
			if(scaledMapHeight < minChromoHeight)
				map.currentHeight = minChromoHeight;
			else
				map.currentHeight = scaledMapHeight;

			//if we are doing this for the first time remember the initial height
			if(!Strudel.winMain.fatController.mapSetsInited)
				map.initialHeight = map.currentHeight;

			//now we also need to apply the current zoom factor
			map.currentHeight = Math.round(map.currentHeight * zoomFactor);

			//adjust the total vertical extent of the genome by the height of this map
			totalY += map.currentHeight;

			//update the value for the space above the map, which has changed now
			map.spaceAboveMap = Utils.calcSpaceAboveGMap(map);
		}

		// the total vertical extent of the genome, excluding top and bottom spacers
		totalY += allSpacers;
//		System.out.println("totalY for mapset = " + totalY);
	}

	// ---------------------------------------------------------------------------------------------------------------------------
}
