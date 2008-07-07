package sbrn.mapviewer.gui;

import java.io.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.tests.syntenyviewer3d.*;
import sbrn.mapviewer.io.*;

public class DataContainer
{
	// data files
	public File referenceData;
	public File targetData;
	public File compData;
	public File[] otherMapFiles;
	
	// this Mapset holds all the data we want to compare against
	public MapSet referenceMapset = null;
	// this Mapset holds the data we want to find out about
	public MapSet targetMapset = null;
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	public LinkSet links = null;
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */
	public void loadData()
	{
		try
		{
			// data files - hard coded for now
//			System.out.println("loading data");
//			System.out.println("working dir = " + System.getProperty("user.dir"));
			
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			
			referenceData = new File(workingDir + fileSep + "data/rice_pseudo4_os.maps");
			targetData = new File(workingDir + fileSep + "data/new_sxm_edited.maps");
			compData = new File(workingDir + fileSep + "data/barley_SNPS_vs_rice4_corr.data");
			otherMapFiles = new File[]
			{ new File(workingDir + fileSep + "data/new_owb_edited.maps"),
							new File(workingDir + fileSep + "data/new_mxb_edited.maps") };
			
//			System.out.println("referenceData = " + referenceData);
			
			// load data
			DataLoader dLoader = new DataLoader();
			referenceMapset = dLoader.loadMapData(referenceData);
			targetMapset = dLoader.loadMapData(targetData);
			
			// need to set the names of the mapsets
			// for now, extract them from the name of the first map in the set
			// the first token in the map name will be the taxon name
			// TODO: check that CMAP data always comes in this same format i.e. the map name starts with the taxon name
			// might want to change this later -- not a very good solution for now
			String targetSetName = targetMapset.getMap(0).getName();
			// chop off everything after the first space
			targetSetName = targetSetName.substring(0, targetSetName.indexOf(","));
			targetMapset.setName(targetSetName);
			String referenceSetName = referenceMapset.getMap(0).getName();
			// chop off everything after the first space
			referenceSetName = referenceSetName.substring(0, referenceSetName.indexOf(","));
			referenceMapset.setName(referenceSetName);
			
			// load links
			CMapLinkImporter limp = new CMapLinkImporter(compData);
			limp.addMapSet(referenceMapset);
			limp.addMapSet(targetMapset);
			MapSet[] otherMapSets = dLoader.loadOtherMapSets(otherMapFiles);
			for (int i = 0; i < otherMapSets.length; i++)
			{
				limp.addMapSet(otherMapSets[i]);
			}
			
			try
			{
				links = limp.loadLinkSet();
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
	}
}
