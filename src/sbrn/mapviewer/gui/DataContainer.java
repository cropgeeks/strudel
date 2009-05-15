package sbrn.mapviewer.gui;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.io.*;
import scri.commons.gui.*;

public class DataContainer
{
	
	//============================================vars==========================================

	
	// data files
	public LinkedList<File>  referenceDataFiles = new  LinkedList<File>();
	public  LinkedList<File>  compDataFiles =new  LinkedList<File>();
	public File targetData;
	public File refGenome1FeatData;
	public File refGenome1HomData;
	public File refGenome2FeatData;
	public File refGenome2HomData;
	
	// this Mapset holds the data we want to find out about
	public MapSet targetMapset = null;
	
	// these link sets hold the all the possible links between all chromos in the target set and all chromos in the reference sets
	public LinkedList<LinkSet> linkSets = new LinkedList<LinkSet>();
	
	//true when data is loaded
	public boolean dataLoaded = false;

	// these Mapsets hold  the data we want to compare the target genome to
	public LinkedList<MapSet> referenceMapSets = new LinkedList<MapSet>();
	
	//a list that holds all the mapsets
	public Vector<GMapSet> gMapSetList = new Vector<GMapSet>();
	
	// a subset of these that contains only the reference mapsets
	public LinkedList<GMapSet> referenceGMapSets = new LinkedList<GMapSet>();
	
	//the index in the gMapSetList of the target gmapset
	public int targetGMapSetIndex = -1;
	
	// a hashtable that holds ChromoMap objects as keys and their corresponding GChromoMap objects as values
	Hashtable<ChromoMap, GChromoMap> gMapLookup = new Hashtable<ChromoMap, GChromoMap>();
	
	// the maximum number of chromos in any one of the genomes involved
	public int maxChromos;
	


	
	//============================================c'tor==========================================
	
	public DataContainer()
	{
		MapViewer.logger.fine("============== making new data container");
		MapViewer.logger.fine("num mapsets prior to initing = " + gMapSetList.size());
		loadData();
		setUpGenomes();
	}
	
	
	//============================================methods==========================================

	//Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	public void loadData()
	{
		MapViewer.logger.fine("initing new dataset");
		MapViewer.logger.fine("loadOwnData = "  + MapViewer.winMain.fatController.loadOwnData);
		
		try
		{
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			
			//need to check where the files for loading come from
			
			//in the first case user wants the example data provided with the app
			if(!MapViewer.winMain.fatController.loadOwnData)
			{
				//load the example data that ships with the application
				targetData = new File(workingDir + fileSep + Constants.exampleTargetData);
				refGenome1FeatData = new File(workingDir + fileSep + Constants.exampleRefGenome1FeatData);
				refGenome1HomData = new File(workingDir + fileSep + Constants.exampleRefGenome1HomData);
				refGenome2FeatData = new File(workingDir + fileSep + Constants.exampleRefGenome2FeatData);
				refGenome2HomData = new File(workingDir + fileSep + Constants.exampleRefGenome2HomData);
			}
			
			//in this next case the user wants to provide their own data files
			else
			{
				MTOpenFilesPanel openFilesPanel = MapViewer.winMain.openFileDialog.openFilesPanel;
				
				//for each file, check whether we have a file chosen by the user -- if not, the respective
				//text field should be empty
				if(!openFilesPanel.getTargetfeatFileTF().getText().equals(""))
					targetData = new File(openFilesPanel.getTargetfeatFileTF().getText());				
				if(!openFilesPanel.getRefGen1FeatFileTF().getText().equals(""))
					refGenome1FeatData = new File(openFilesPanel.getRefGen1FeatFileTF().getText());				
				if(!openFilesPanel.getRefGen1HomFileTF().getText().equals(""))
					refGenome1HomData = new File(openFilesPanel.getRefGen1HomFileTF().getText());				
				if(!openFilesPanel.getRefGen2FeatFileTF().getText().equals(""))
					refGenome2FeatData = new File(openFilesPanel.getRefGen2FeatFileTF().getText());				
				if(!openFilesPanel.getRefGen2HomFileTF().getText().equals(""))
					refGenome2HomData = new File(openFilesPanel.getRefGen2HomFileTF().getText());					
				
				//check whether user has specified files correctly				
				//missing target data file
				if(targetData == null)
				{
					String errorMessage = "The target data file has not been specified. Please try again.";
					TaskDialog.error(errorMessage, "Close");
					throw new Exception(errorMessage);
				}			
				//if reference datasets are to be used, we need to have both the feature file and the homology file
				//for each of them
				if(refGenome1FeatData != null && refGenome1HomData == null ||
								refGenome1FeatData == null && refGenome1HomData != null ||
								refGenome2FeatData != null && refGenome2HomData == null ||
								refGenome2FeatData == null && refGenome2HomData != null)
				{
					String errorMessage = "One of the files required for a reference genome has not been specified. Please specify both the feature file and the homology file.";
					TaskDialog.error(errorMessage, "Close");
					throw new Exception(errorMessage);
				}

				
			}
			
			//add reference data 1 if appropriate
			if(refGenome1FeatData != null && refGenome1HomData != null)
			{
				referenceDataFiles.add(refGenome1FeatData);
				compDataFiles.add(refGenome1HomData);
			}		
			//add reference data 2 if appropriate
			if(refGenome2FeatData != null && refGenome2HomData != null)
			{
				referenceDataFiles.add(refGenome2FeatData);
				compDataFiles.add(refGenome2HomData);
			}
			
			
			// load data
			DataLoader dLoader = new DataLoader();
			//load reference data sets
			for (int i = 0; i < referenceDataFiles.size(); i++)
			{
				referenceMapSets.add(dLoader.loadMapData(referenceDataFiles.get(i)));
			}
			targetMapset = dLoader.loadMapData(targetData);
			
			// need to set the names of the mapsets and import the links
			// for now, set them to the names of the files they were read in from
			targetMapset.setName(targetData.getName().substring(0, targetData.getName().indexOf(".")));
			for (int i =0; i< referenceMapSets.size(); i++)
			{
				String refDataName = referenceDataFiles.get(i).getName();
				referenceMapSets.get(i).setName(refDataName.substring(0, refDataName.indexOf(".")));
				
				MapViewer.logger.fine("Importing links for data set  " + referenceMapSets.get(i).getName());
				
				// load links
				try
				{
					//links for reference genome
					CMapLinkImporter limp = new CMapLinkImporter(compDataFiles.get(i));
					limp.addMapSet(targetMapset);
					limp.addMapSet(referenceMapSets.get(i));
					linkSets.add(limp.loadLinkSet());
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			
			dataLoaded = true;
			
			MapViewer.logger.fine("mapsets loaded:");
			MapViewer.logger.fine(targetData.getName());
			for (MapSet mapSet : referenceMapSets)
			{
				MapViewer.logger.fine(mapSet.getName());
			}
			
			
			MapViewer.logger.fine("linkSets.size() in DC  = " + linkSets.size());
			for (LinkSet linkSet : linkSets)
			{
				MapViewer.logger.fine(linkSet.getMapSets().get(0).getName() + ", "
								+ linkSet.getMapSets().get(1).getName());
			}
		}
		catch (Exception e)
		{
			MapViewer.winMain.openFileDialog.dataLoadingDialog.setVisible(false);
			TaskDialog.error("Data loading failed: " + e.toString(), "Close");
			e.printStackTrace();
		}
	}
	
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------
	
	// initialises the genome objects we want to draw
	public void setUpGenomes()
	{
		// make new GMapSets from the map sets passed in and add them to the list
		//the order is significant here
		//if we have no reference mapsets
		if(referenceMapSets.size() == 0)
		{
			//add the target genome only
			gMapSetList.add(new GMapSet(Colors.targetGenomeColour, targetMapset, Constants.TARGET_GENOME, true,  gMapLookup));
			targetGMapSetIndex = 0;
		}
		//if we have only one reference genome
		else if(referenceMapSets.size() == 1)
		{
			//add the target genome first, then the single reference genome
			gMapSetList.add(new GMapSet(Colors.targetGenomeColour, targetMapset, Constants.TARGET_GENOME, true,  gMapLookup));
			gMapSetList.add(new GMapSet(Colors.referenceGenomeColour, referenceMapSets.get(0), Constants.REFERENCE_GENOME, false, gMapLookup));
			targetGMapSetIndex = 0;
		}
		//if we have two reference genomes
		else if(referenceMapSets.size() == 2)
		{
			//add the first reference genome first, then the target genome, then the other ref genome
			gMapSetList.add(new GMapSet(Colors.referenceGenomeColour, referenceMapSets.get(0), Constants.REFERENCE_GENOME, false, gMapLookup));
			gMapSetList.add(new GMapSet(Colors.targetGenomeColour, targetMapset, Constants.TARGET_GENOME, true, gMapLookup));
			gMapSetList.add(new GMapSet(Colors.referenceGenomeColour, referenceMapSets.get(1), Constants.REFERENCE_GENOME, false,  gMapLookup));
			targetGMapSetIndex = 1;
		}
		
		//init the local list of reference GMapSets
		for (GMapSet gMapSet : gMapSetList)
		{
			if(!gMapSet.isTargetGenome)
				referenceGMapSets.add(gMapSet);
		}
		
		//various other initing stuff
		maxChromos = 0;
		for (GMapSet gMapSet : gMapSetList)
		{
			// check which genome has the most chromosomes
			if(gMapSet.numMaps > maxChromos)
				maxChromos = gMapSet.numMaps;
		}
		
	}
	
}
