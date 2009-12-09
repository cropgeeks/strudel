package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
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
	public Vector<GChromoMap> gMaps;

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
	//greater than the canvas height
	public int centerPoint = 0;

	// the total current height of the genome as drawn on the canvas, in pixels
	public int totalY;

	//the height of a chromosome in this genome, in pixels (all chromos are the same height, always)
	public int chromoHeight;

	// this controls whether we draw chromosome markers
	public boolean paintAllMarkers = false;

	//a zoom factor value above which we can draw all  features
	//this is to reduce the amount of clutter on screen
	public float thresholdAllMarkerPainting = 10;
	//the same for drawing labels
	public float thresholdLabelPainting;

	//the zoom factor at which we would fit a single chromosome (but nothing else) on the visible portion of the canvas
	public float singleChromoViewZoomFactor;

	public TreeMap<Feature, Integer> foundFeatures = new TreeMap<Feature, Integer>();

	//a boolean to indicate whether we should always display markers, regardless of zoom factor
	public boolean overrideMarkersAutoDisplay = false;
	//a boolean to indicate whether we should always display labels, regardless of zoom factor
	public boolean alwaysShowAllLabels = false;

	//the zoom control panel pertaining to this mapset
	public ZoomControlPanel zoomControlPanel;


	// ====================================curve'tors========================================

	public GMapSet(Color mapSetColour, MapSet mapSet, Hashtable<ChromoMap, GChromoMap> gMapLookup)
	{
		this.colour = mapSetColour;
		this.mapSet = mapSet;
		this.name = mapSet.getName();
		numMaps = mapSet.size();

		// init the list of maps contained in this genome
		initialise(gMapLookup);
	}

	// ========================================methods==================================

	// init the list of maps contained in this genome
	public void initialise(Hashtable<ChromoMap, GChromoMap> gMapLookup)
	{
		gMaps = new Vector<GChromoMap>();

		for (int i = 0; i < mapSet.size(); i++)
		{
			ChromoMap cMap = mapSet.getMaps().get(i);
			GChromoMap gMap = new GChromoMap(colour, cMap.getName(), i, this);
			gMaps.add(gMap);

			gMapLookup.put(cMap, gMap);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------

//	public void addSelectedMap(GChromoMap map)
//	{
//		if (!selectedMaps.contains(map))
//		{
//			selectedMaps.add(map);
//			map.drawHighlightOutline = true;
//		}
//	}

	// ---------------------------------------------------------------------------------------------------------------------------

//	public void removeSelectedMap(GChromoMap map)
//	{
//		if (selectedMaps.contains(map))
//		{
//			selectedMaps.remove(map);
//			map.drawHighlightOutline = false;
//		}
//	}

	// ---------------------------------------------------------------------------------------------------------------------------

//	public void selectAllMaps()
//	{
//		for (GChromoMap gMap : gMaps)
//		{
//			addSelectedMap(gMap);
//		}
//	}

	// ---------------------------------------------------------------------------------------------------------------------------

//	public void deselectAllMaps()
//	{
//		for (GChromoMap gMap : gMaps)
//		{
//			removeSelectedMap(gMap);
//		}
//	}

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
}
