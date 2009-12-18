package sbrn.mapviewer.gui;

import java.util.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class DataContainer
{

	// ============================================vars==========================================

	// these link sets hold the all the possible links between all chromos in the target set and all chromos in the reference sets
	public LinkedList<LinkSet> allLinkSets;

	// these Mapsets hold the data we want to compare the target genome to
	public LinkedList<MapSet> allMapSets;

	// a list that holds all the gmapsets
	public LinkedList<GMapSet> gMapSets;

	// a hashtable that holds ChromoMap objects as keys and their corresponding GChromoMap objects as values
	Hashtable<ChromoMap, GChromoMap> gMapLookup = new Hashtable<ChromoMap, GChromoMap>();

	// the maximum number of chromos in any one of the genomes involved
	public int maxChromos;

	// ============================================methods==========================================

	// initialises the genome objects we want to draw
	public void setUpGMapSets(LinkedList<LinkSet> allLinkSets, LinkedList<MapSet> allMapSets)
	{
		// make new GMapSets from the map sets passed in and add them to the list
		// the order is significant here
		// if we have no reference mapsets
		this.allLinkSets = allLinkSets;
		this.allMapSets = allMapSets;
		gMapSets = new LinkedList<GMapSet>();

		for (MapSet mapset : allMapSets)
		{
			gMapSets.add(new GMapSet(Colors.referenceGenomeColour, mapset, gMapLookup));
		}

		// other initing stuff
		maxChromos = 0;
		for (GMapSet gMapSet : gMapSets)
		{
			// check which genome has the most chromosomes
			if (gMapSet.numMaps > maxChromos)
				maxChromos = gMapSet.numMaps;
		}

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
