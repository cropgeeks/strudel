package sbrn.mapviewer.gui;

import java.io.*;
import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.io.*;
import scri.commons.gui.*;

public class DataContainer
{

	// ============================================vars==========================================

	// data file
	public File inputFile;

	// these link sets hold the all the possible links between all chromos in the target set and all chromos in the reference sets
	public LinkedList<LinkSet> allLinkSets = new LinkedList<LinkSet>();

	// these Mapsets hold the data we want to compare the target genome to
	public LinkedList<MapSet> allMapSets = new LinkedList<MapSet>();

	// a list that holds all the gmapsets
	public LinkedList<GMapSet> gMapSets = new LinkedList<GMapSet>();

	// a hashtable that holds ChromoMap objects as keys and their corresponding GChromoMap objects as values
	Hashtable<ChromoMap, GChromoMap> gMapLookup = new Hashtable<ChromoMap, GChromoMap>();

	// the maximum number of chromos in any one of the genomes involved
	public int maxChromos;

	// ============================================c'tor==========================================

	public DataContainer(File inputFile)
	{
		loadDataFromSingleFile(inputFile);
		setUpGMapSets();
	}

	// ============================================methods==========================================

	// Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	public void loadDataFromSingleFile(File inputFile)
	{
		try
		{
			SingleFileImporter singleFileImporter = new SingleFileImporter();
			singleFileImporter.parseCombinedFile(inputFile);
			allMapSets = singleFileImporter.getAllMapSets();
			allLinkSets = singleFileImporter.getAllLinkSets();
		}
		catch (Exception e)
		{
			// reset the cancel flag as the user might now want to try again
			Strudel.winMain.fatController.dataLoadCancelled = false;
			Strudel.winMain.dataContainer = null;
			// hide the data loading progress dialog
			if (Strudel.winMain.dataLoadingDialog != null)
				Strudel.winMain.dataLoadingDialog.setVisible(false);

			TaskDialog.error("Data load failed: " + e.toString() + "\nPlease correct your data and try again.", "Close");
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	// initialises the genome objects we want to draw
	public void setUpGMapSets()
	{
		// make new GMapSets from the map sets passed in and add them to the list
		// the order is significant here
		// if we have no reference mapsets

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
