package sbrn.mapviewer.gui;

import java.io.File;
import java.io.IOException;

import sbrn.mapviewer.data.LinkSet;
import sbrn.mapviewer.data.MapSet;
import sbrn.mapviewer.io.CMapImporter;
import sbrn.mapviewer.io.CMapLinkImporter;

/** Loads data from file for use with map viewer
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class DataLoader
{

//==================================methods=============================	
	
	/**
	 * Loads all other required mapsets beyong the target and the reference one
	 */
	public MapSet [] loadOtherMapSets(File [] otherMapFiles)
	{
		MapSet [] mapSets = new MapSet [otherMapFiles.length];
		
		for(int i = 0; i< otherMapFiles.length; i++)
		{
			CMapImporter importer = new CMapImporter(otherMapFiles[i]);			
			MapSet mapSet = null;
			try
			{
				mapSet = importer.loadMapSet();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			mapSets[i] = mapSet;
		}
		return mapSets;
	}
	
//-----------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Loads a MapSet given its source file
	 */
	public MapSet loadMapData(File file)
	{
		MapSet referenceMapset = null;		
		try
		{
			referenceMapset = new CMapImporter(file).loadMapSet();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
		return referenceMapset;

	}

	// ---------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Loads a set of comparative data given the file path
	 */
	public LinkSet loadComparativeData(File file)
	{
		CMapLinkImporter limp = new CMapLinkImporter(file);
		LinkSet links = null;
		try
		{
			links = limp.loadLinkSet();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		return links;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
}
