package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.Scroller;

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

	// the type of genome for the purpose of this application
	// can be either "target" i.e. the genome we have little info for and only a genetic map, or
	// "reference", i.e. the genome we have a physical map and annotation for
	// see Constants.java for options
	public int type;

	// the name of the genome
	public String name;

	// the current zoom factor for the display of this genome
	public float zoomFactor = 1;

	// the x position of this genome on the canvas
	// i.e. an x coordinate in pixels which denotes the left most edge of the genome drawn
	public float xPosition;

	// true if this is the target genome, false if it the reference genome
	public boolean isTargetGenome = false;

	// the percent offset from the top of the genome that represents the topmost point of the genome visible on the canvas
	public int drawingOffset = 0;

	// the point on the genome that is currently seen in the center of the canvas, in pixels, measured from the top of the genome
	public int centerPoint = 0;

	// the total current height of the genome as drawn on the canvas, in pixels
	public int totalY;

	//the height of a chromosome in this genome, in pixels (all chromos are the same height, always)
	public int chromoHeight;

	// a vector containing GChromoMap objects that have been selected by the user through mouse clicks
	public Vector<GChromoMap> selectedMaps = new Vector<GChromoMap>();


	// these control whether we draw chromosome markers and labels
	public boolean paintLinkedMarkers = false;
	public boolean paintAllMarkers = false;
	public boolean paintLabels = false;

	//a zoom factor value above which we can draw all  features
	//this is to reduce the amount of clutter on screen
	public float thresholdAllMarkerPainting;
	//the same for drawing only features that are involved in links
	public float thresholdLinkedMarkerPainting;
	//the same for drawing labels
	public float thresholdLabelPainting;

	//a vector of maps in this mapset that are currently visible
//	public Vector<GChromoMap> visibleMaps = new Vector<GChromoMap>();

	// ====================================c'tors========================================

	public GMapSet(Color mapSetColour, MapSet mapSet, int type, boolean isTargetGenome, 
					Hashtable<ChromoMap, GChromoMap> gMapLookup)
	{
		this.colour = mapSetColour;
		this.mapSet = mapSet;
		this.type = type;
		this.name = mapSet.getName();
		this.isTargetGenome = isTargetGenome;
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
			ChromoMap cMap = (ChromoMap) mapSet.getMaps().get(i);
			GChromoMap gMap = new GChromoMap(colour, cMap.getName(), i, this);
			gMaps.add(gMap);

			gMapLookup.put(cMap, gMap);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	public void addSelectedMap(GChromoMap map)
	{
		if (!selectedMaps.contains(map))
		{
			selectedMaps.add(map);
			map.drawHighlightOutline = true;
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	public void removeSelectedMap(GChromoMap map)
	{
		if (selectedMaps.contains(map))
		{
			selectedMaps.remove(map);
			map.drawHighlightOutline = false;
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	public void selectAllMaps()
	{
		for (GChromoMap gMap : gMaps)
		{
			addSelectedMap(gMap);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	public void deselectAllMaps()
	{
		for (GChromoMap gMap : gMaps)
		{
			removeSelectedMap(gMap);
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
}
