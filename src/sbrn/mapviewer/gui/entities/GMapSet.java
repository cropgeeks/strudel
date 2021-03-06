package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.*;
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
	//greater than the height of the visible part of the main canvas
	public int centerPoint = 0;

	// the total current height of the genome as drawn on the canvas, in pixels
	public int totalY;

	//the height of a chromosome in this genome, in pixels (all chromos are the same height, always)
	public int chromoHeight;

	// this controls whether we draw chromosome markers
	public boolean showAllFeatures = false;

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

	//the zoom control panel pertaining to this mapset
	public ZoomControlPanel zoomControlPanel;

	//true if all the chromosomes belonging to this mapset have been selected
	public boolean wholeMapsetIsSelected = false;
	
	//flags used for feature position indexing
	public boolean mapSetScrolled = false;
	public boolean mapSetZoomed = false;
	
	//true while we are scrolling
	public boolean isScrolling = false;

	//this indicates whether this map set has been scrolled at all
	//we need so we know whether to recenter the mapset when fully zoomed out
//	public boolean hasBeenScrolled = false;
	
	//the max zoom factor for this mapset -- can be overridden by the user with the spinner control at the bottom of the screen
	public int maxZoomFactor = Constants.MAX_ZOOM_FACTOR;
	

	// ====================================c'tor========================================

	public GMapSet(MapSet mapSet)
	{
		this.mapSet = mapSet;
		this.name = mapSet.getName();
		numMaps = mapSet.size();
		
		//also set the mapset's gmapset to this
		mapSet.gMapSet = this;

		// init the list of maps contained in this genome
		initialise();
	}

	// ========================================methods==================================

	// init the list of maps contained in this genome
	public void initialise()
	{
		gMaps = new Vector<GChromoMap>();

		for (int i = 0; i < mapSet.size(); i++)
		{
			ChromoMap cMap = mapSet.getMaps().get(i);
			GChromoMap gMap = new GChromoMap(cMap.getName(), i, this);
			gMaps.add(gMap);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//updates the positions of all features of all chromosomes
	//this is necessary because zooming and scrolling changes the actual position values
	public void initialisePositionArraysForMapSet()
	{		
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMaps)
				gChromoMap.initArrays();
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
}
