package sbrn.mapviewer.gui;

import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class DataSet
{

	// ============================================vars==========================================

	// these link sets hold the all the possible links between all chromos in the target set and all chromos in the reference sets
	public LinkedList<LinkSet> allLinkSets = new LinkedList<LinkSet>();

	// these Mapsets hold the data we want to compare the target genome to
	public LinkedList<MapSet> allMapSets = new LinkedList<MapSet>();

	// a list that holds all the gmapsets
	public LinkedList<GMapSet> gMapSets = new LinkedList<GMapSet>();

	// the maximum number of chromos in any one of the genomes involved
	public int maxChromos;

	// ============================================methods==========================================

	public void reconfigureGMapSets(LinkedList<String> gMapsetNames)
	{
		//clear the associated gChromoMaps for this mapset, if necessary
		for(MapSet mapset : Strudel.winMain.dataSet.allMapSets)
		{
			for(ChromoMap cMap : mapset.getMaps())
			{
				if(cMap.getGChromoMaps().size() > 0)
					cMap.clearCurrentGChromoMaps();
			}
		}

		gMapSets = new LinkedList<GMapSet>();

		for (String mapsetName : gMapsetNames)
		{
			MapSet mapset = Utils.getMapSetByName(mapsetName);
			gMapSets.add(new GMapSet(mapset));
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

	//---------------------------------------------------------------------------------------------------------------------------------------------------------

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
			gMapSets.add(new GMapSet(mapset));
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
