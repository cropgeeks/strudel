package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;

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
	public int xPosition;
	
	//these control whether we draw chromosome markers and  labels
	public boolean paintMarkers = false;
	public boolean paintLabels = false;
	
	public boolean isTargetGenome= false;
	
	// ====================================c'tors========================================
	
	public GMapSet(Color mapSetColour, MapSet mapSet, int type, String name,boolean isTargetGenome)
	{
		this.colour = mapSetColour;
		this.mapSet = mapSet;
		this.type = type;
		this.name = name;
		this.isTargetGenome = isTargetGenome;
		numMaps = mapSet.size();
		
		// init the list of maps
		initialise();
	}
	
	// ========================================methods==================================
	
	// init the list of maps
	public void initialise()
	{
		gMaps = new LinkedList<GChromoMap>();
		
		for (int i = 0; i < mapSet.size(); i++)
		{
			ChromoMap cMap = (ChromoMap) mapSet.getMaps().get(i);
			GChromoMap gMap = new GChromoMap(colour, cMap.getName(), i, this);
			gMaps.add(gMap);
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------
}
