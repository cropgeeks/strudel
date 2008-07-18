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
//	public File[] otherMapFiles;
	
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
			// System.out.println("loading data");
			// System.out.println("working dir = " + System.getProperty("user.dir"));
			
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			
			referenceData = new File(workingDir + fileSep + "data/riceU35data.txt");
//			referenceData = new File(workingDir + fileSep + "data/rice_pseudo4_os.maps");
			targetData = new File(workingDir + fileSep + "data/Barley_Map(UCR_20080416-2)_barley.txt");
			compData = new File(workingDir + fileSep + "data/Barley_Map(UCR_20080416-2)_homology_data.txt");
//			otherMapFiles = new File[]
//			{ new File(workingDir + fileSep + "data/new_owb_edited.maps"),
//							new File(workingDir + fileSep + "data/new_mxb_edited.maps") };
			
			// System.out.println("referenceData = " + referenceData);
			
			// load data
			DataLoader dLoader = new DataLoader();
			referenceMapset = dLoader.loadMapData(referenceData);
			targetMapset = dLoader.loadMapData(targetData);
			
			// need to set the names of the mapsets
			// for now, set them to the names of the files they were read in from
			targetMapset.setName(targetData.getName().substring(0, targetData.getName().indexOf(".")));
			referenceMapset.setName(referenceData.getName().substring(0, referenceData.getName().indexOf(".")));
			
			// load links
			try
			{
				CMapLinkImporter limp = new CMapLinkImporter(compData);
				limp.addMapSet(referenceMapset);
				limp.addMapSet(targetMapset);
//				MapSet[] otherMapSets = dLoader.loadOtherMapSets(otherMapFiles);
//				for (int i = 0; i < otherMapSets.length; i++)
//				{
//					limp.addMapSet(otherMapSets[i]);
//				}
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
