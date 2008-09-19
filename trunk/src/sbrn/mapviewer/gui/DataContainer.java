package sbrn.mapviewer.gui;

import java.io.*;
import java.util.*;

import com.sun.org.apache.bcel.internal.generic.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.io.*;

public class DataContainer
{
	
	//============================================vars==========================================	
	
	//hard code the number of reference genomes for now
	//later this needs tied into file load dialog
	//TODO: remove hard coding
	int numRefGenomes = 2;
	// these Mapsets hold  the data we want to compare the target genome to
	public LinkedList<MapSet> referenceMapsets = new LinkedList<MapSet>();
	
	// data files
	public LinkedList<File>  referenceDataFiles = new  LinkedList<File>();
	public  LinkedList<File>  compDataFiles =new  LinkedList<File>();
	public File targetData;
	
	// this Mapset holds the data we want to find out about
	public MapSet targetMapset = null;
	
	// these link sets hold the all the possible links between all chromos in the target set and all chromos in the reference sets
	public LinkedList<LinkSet> linkSets = new LinkedList<LinkSet>();


//============================================methods==========================================
	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */
	public void loadData()
	{
		try
		{
			// data files - hard coded for now
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			referenceDataFiles.add(new File(workingDir + fileSep + "data/riceHomologs_TIGR5.txt"));
//			referenceDataFiles.add(new File(workingDir + fileSep + "data/riceHomologs_TIGR5.txt"));
			referenceDataFiles.add(new File(workingDir + fileSep + "data/artificial_genome.txt"));
			targetData = new File(workingDir + fileSep + "data/Barley_Map(UCR_20080416-2)_barley.txt");
			compDataFiles.add(new File(workingDir + fileSep + "data/Barley_Map(UCR_20080416-2)_homology_data.txt"));
//			compDataFiles.add(new File(workingDir + fileSep + "data/Barley_Map(UCR_20080416-2)_homology_data.txt"));
			compDataFiles.add(new File(workingDir + fileSep + "data/artificial_genome_homology data.txt"));

			// load data
			DataLoader dLoader = new DataLoader();
			//load reference data sets
			for (int i = 0; i < referenceDataFiles.size(); i++)
			{
				referenceMapsets.add(dLoader.loadMapData(referenceDataFiles.get(i)));
			}
			targetMapset = dLoader.loadMapData(targetData);

			// need to set the names of the mapsets and import the links
			// for now, set them to the names of the files they were read in from
			targetMapset.setName(targetData.getName().substring(0, targetData.getName().indexOf(".")));
			for (int i =0; i< referenceMapsets.size(); i++)
			{
				String refDataName = referenceDataFiles.get(i).getName();
				referenceMapsets.get(i).setName(refDataName.substring(0, refDataName.indexOf(".")));
				
				System.out.println("Importing links for data set  " + referenceMapsets.get(i).getName());
				
				// load links
				try
				{
						//links for reference genome
						CMapLinkImporter limp = new CMapLinkImporter(compDataFiles.get(i));
						limp.addMapSet(targetMapset);
						limp.addMapSet(referenceMapsets.get(i));
						linkSets.add(limp.loadLinkSet());
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			
			System.out.println("linkSets.size() in DC  = " + linkSets.size());
			for (LinkSet linkSet : linkSets)
			{
				System.out.println(linkSet.getMapSets().get(0).getName() + ", " 
								+ linkSet.getMapSets().get(1).getName());
			}



		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}
}
