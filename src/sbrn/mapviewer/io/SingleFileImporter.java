package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

public class SingleFileImporter
{
	//==========================================methods==================================================	
	
	private LinkedList<MapSet> allMapSets = new LinkedList<MapSet>();
	private LinkedList<LinkSet> allLinkSets = new LinkedList<LinkSet>();
	
	//==========================================methods==================================================	
	
	//Reads a file which contains all mapsets and links to be imported
	//the file format has a single line entry for either a feature or a homology
	//the first field says which type it is
	//features are expected first in the file, then links
	//features should be in blocks
	public void parseCombinedFile(File file)
	{
		int lineCount = 0;
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			//parse the file
			String line = null;
			while((line = reader.readLine()) != null)
			{
				if(line.startsWith("feature"))
					processFeatureLine(line);
				else if(line.startsWith("homolog"))
					processLink(line);
				lineCount++;
			}
			
			MapViewer.dataLoaded = true;			
		}
		catch (Exception e)
		{
			System.out.println("error reading line " + lineCount);
			e.printStackTrace();
		}		
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public LinkedList<LinkSet> getAllLinkSets()
	{
		return allLinkSets;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public LinkedList<MapSet> getAllMapSets()
	{
		return allMapSets;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//processes a single feature and adds it to the appropriate mapset
	private void processFeatureLine(String line)
	{
		//the file format is tab delimited text
		String [] tokens = line.split("\t");

		//the order of columns for a feature is:
		//type - genome - chromosome - featureName - featureStart - featureEnd - annotation
		//e.g.: feature	Barley	1H	12_30969	0	<blank>	<blank>
		
		//find out whether the feature's genome (mapset) exists
		String mapsetName = tokens[1].trim();
		MapSet mapset = null;
		for (MapSet ms : allMapSets)
		{
			if(ms.getName().equals(mapsetName))
				mapset = ms;
		}
		//if not, create one
		if(mapset == null)
		{
			mapset = new MapSet(mapsetName);
			//now add it to our list of mapsets held here
			allMapSets.add(mapset);
		}
		
		//do the same  for the chromosome (ChromoMap)
		String chromoName = tokens[2].trim();
		ChromoMap chromoMap = null;
		for (ChromoMap cm : mapset.getMaps())
		{
			if(cm.getName().equals(chromoName))
				chromoMap = cm;
		}
		//if a map doesn't exist, create one
		if(chromoMap == null)
		{
			chromoMap = new ChromoMap(chromoName);
			//and add it to this mapset's gmaps list
			mapset.addMap(chromoMap);
		}		
		
		//make a new Feature object
		Feature feature = new Feature(tokens[3].trim());		
		
		//set the other feature parameters
		
		//start position
		float start = -1;
		try
		{			
			start = Float.parseFloat(tokens[4].trim());		
		}
		catch (NumberFormatException e)
		{
			throw new NumberFormatException("Feature " + feature.getName() + " " + "does not appear to have a valid start position. ");
		}
		feature.setStart(start);

		//feature stop and annotation -- several possible scenarios here:	
		
		//if the array is length 7 then either both fields are used or field 5 is not but field 6 is
		if(tokens.length == 7)
		{
			//featureStop(tokens[5]) is blank + annotation(tokens[6]) is not 
			//if this is the case the length of the tokens array will still be 7
			//tokens[5] should return an empty string in that case
			if(!tokens[5].equals(""))
			{
				feature.setStop(Float.parseFloat(tokens[5].trim()));
			}
			//else the stop field is blank
			//the implication of that is that the feature has a length of 1
			//in this case we set the stop to be the same as the start
			else
			{
				feature.setStop(start);
			}
			
			//set the annotation
			feature.setAnnotation(tokens[6].trim());
		}	
		
		//OR:
		//featureStop(tokens[5]) contains a value but annotation(tokens[6]) is blank
		//if the annotation column (tokens[6]) is blank the length of the tokens array will be 6, rather than 7
		else if(tokens.length == 6)
		{
			float stop = -1;
			try
			{			
				stop = Float.parseFloat(tokens[5].trim());		
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Feature " + feature.getName() + " " + "does not appear to have a valid start position. ");
			}
			feature.setStop(stop);
		}
		
		//OR:
		//both are blank
		//in that case tokens.length should be 5
		//the implication of an empty stop field is that the feature has a length of 1
		//in this case we also set the stop to be the same as the start
		else if(tokens.length == 5)
		{
			feature.setStop(start);
		}
		
		
		//finally add the feature to the map
		chromoMap.addFeature(feature);
		//and vice versa
		feature.setOwningMap(chromoMap);	
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//processes a single link (homolog) and add its it to the appropriate link set
	private void processLink(String line)
	{
		try
		{
			//the file format is tab delimited text
			String [] tokens = line.split("\t");
			
			//order of columns for a homolog/link:
			//type("homolog") - feature1Genome - feature1Name -feature2Genome - feature2Name - eValue - annotation
			//e.g.: homolog	Barley	11_20879	Brachypodium	Bradi3g41470	8.00E-59	none
			
			// Find all features with the first name and all with the second
			LinkedList<Feature> f1List = Utils.getFeaturesByName(tokens[2].trim(), allMapSets);
			LinkedList<Feature> f2List = Utils.getFeaturesByName(tokens[4].trim(), allMapSets);
			
			//extract the names of the genomes involved in this link 
			String genome1Name = tokens[1].trim();
			String genome2Name = tokens[3].trim();
			LinkSet linkSet = null;
			//check whether a linkset between these two genomes exists already
			for (LinkSet ls : allLinkSets)
			{				
				String mapset1 = ls.getMapSets().get(0).getName();
				String mapset2 = ls.getMapSets().get(1).getName();
				if (mapset1.equalsIgnoreCase(genome1Name) && mapset2.equalsIgnoreCase(genome2Name))
				{
					linkSet = ls;
				}
			}
			//if not, make a new linkset and add it to our local list 
			if(linkSet == null)
			{
				linkSet = new LinkSet();
				allLinkSets.add(linkSet);
			}
			
			//the last token in the array contains the annotation but for the user's convenience this may just be left blank
			//need to check for this
			String annotation = null;
			if(tokens.length == 7)
				annotation = tokens[6].trim();
			
			//this method adds the link between the two features to the linkset
			Utils.buildLinkSetFromFeatureLists(linkSet, f1List, f2List, tokens[5].trim(), annotation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
