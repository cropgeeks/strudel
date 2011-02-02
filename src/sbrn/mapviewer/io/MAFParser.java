package sbrn.mapviewer.io;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.io.*;

/**
 * Parser class for the multiple aligment format MAF.
 * 
 * See http://genome.ucsc.edu/FAQ/FAQformat#format5
 * 
 * Example file would look like this:

##maf version=1 scoring=tba.v8 
# tba.v8 (((human chimp) baboon) (mouse rat)) 
                   
a score=23262.0     
s hg18.chr7    27578828 38 + 158545518 AAA-GGGAATGTTAACCAAATGA---ATTGTCTCTTACGGTG
s panTro1.chr6 28741140 38 + 161576975 AAA-GGGAATGTTAACCAAATGA---ATTGTCTCTTACGGTG
s baboon         116834 38 +   4622798 AAA-GGGAATGTTAACCAAATGA---GTTGTCTCTTATGGTG
s mm4.chr6     53215344 38 + 151104725 -AATGGGAATGTTAAGCAAACGA---ATTGTCTCTCAGTGTG
s rn3.chr4     81344243 40 + 187371129 -AA-GGGGATGCTAAGCCAATGAGTTGTTGTCTCTCAATGTG
                   
a score=5062.0                    
s hg18.chr7    27699739 6 + 158545518 TAAAGA
s panTro1.chr6 28862317 6 + 161576975 TAAAGA
s baboon         241163 6 +   4622798 TAAAGA 
s mm4.chr6     53303881 6 + 151104725 TAAAGA
s rn3.chr4     81444246 6 + 187371129 taagga

a score=6636.0
s hg18.chr7    27707221 13 + 158545518 gcagctgaaaaca
s panTro1.chr6 28869787 13 + 161576975 gcagctgaaaaca
s baboon         249182 13 +   4622798 gcagctgaaaaca
s mm4.chr6     53310102 13 + 151104725 ACAGCTGAAAATA

The MAF files contain multiple blocks of alignments, each one representing a separate feature/region, and each one consisting
of several lines each of which represents a genome.
 */
public class MAFParser extends AbtractFileParser
{
	
	//this is where we store the data
	DataSet dataSet = new DataSet();
	
	int alignmentCount = 0;
	

	//-----------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void parseFile() throws Exception
	{
		try
		{
			//parse the file
			String line = null;
			while((line = readLine()) != null && okToRun)
			{
				if(line.startsWith("a "))
				{
					parseAlignmentBlock(line);
					alignmentCount++;
				}
			}

			dataSet.setUpGMapSets(dataSet.allLinkSets, dataSet.allMapSets);
			dataSet.fileName = getFile().getName();
			Strudel.winMain.dataSet = dataSet;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String errorMessage = "Error reading line " + lineCount + ".\n" + e.getMessage();
			throw  new IOException(errorMessage);
		} 
	}
	
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * A block of alignments looks like this:
	 * 
		a score=5062.0                    
		s hg18.chr7    27699739 6 + 158545518 TAAAGA
		s panTro1.chr6 28862317 6 + 161576975 TAAAGA
		s baboon         241163 6 +   4622798 TAAAGA 
		s mm4.chr6     53303881 6 + 151104725 TAAAGA
		s rn3.chr4     81444246 6 + 187371129 taagga
	 
	 *We need to parse this and create links between the features on the chromosomes involved in this alignment.
	 *
	 */
	private void parseAlignmentBlock(String line) throws Exception
	{
	
		//parse the header line
		//looks like this: a score=6636.0
		String [] headerTokens = line.split(" ");
		
		//new alignment 
		float score = Float.parseFloat(headerTokens[1].substring(6));
		LinkedList<Feature> featuresInAlignment = new LinkedList<Feature>();
				
		//read on and parse the individual sequence lines
		while(line!= null)
		{
			line = readLine();
			if(line == null)
				break;			
			else if(line.startsWith("s "))
			{
				Feature feature = parseSequenceLine(line);
				featuresInAlignment.add(feature);
			}
			else if(line.trim() == null || line.trim().equals(""))
				break;
		}
		
		//now make links between all the features in this alignment
		createLinksBetweenFeaturesInAlignment(featuresInAlignment, score);
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void createLinksBetweenFeaturesInAlignment(LinkedList<Feature> featuresInAlignment, float score) throws Exception
	{
		int count = 0;
		for (Feature feature : featuresInAlignment)
		{
			Feature targetFeature = featuresInAlignment.get(count);
			
			//iterate over all features and make a link with each
			for (Feature refFeature : featuresInAlignment)
			{
				//don't make links with the feature itself
				if(targetFeature == refFeature)
					continue;
				//make a new link and add it to both features
				IOUtils.addLinkToLinkSet(targetFeature.getOwningMapSet().getName(), refFeature.getOwningMapSet().getName(), dataSet.allLinkSets, 
								targetFeature, refFeature,"0", "", Colors.linkColour);
			}
			
			count++;
		}
	}
	
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 *A sequence line looks like this:
	 
		s hg18.chr7    27699739 6 + 158545518 TAAAGA
	
	The 's' lines together with the 'a' lines define a multiple alignment. The 's' lines have the following fields which are defined by position rather than name=value pairs.

    * src -- The name of one of the source sequences for the alignment. For sequences that are resident in a browser assembly, the form 'database.chromosome' allows automatic creation of links to other assemblies. Non-browser sequences are typically reference by the species name alone.
    * start -- The start of the aligning region in the source sequence. This is a zero-based number. If the strand field is '-' then this is the start relative to the reverse-complemented source sequence.
    * size -- The size of the aligning region in the source sequence. This number is equal to the number of non-dash characters in the alignment text field below.
    * strand -- Either '+' or '-'. If '-', then the alignment is to the reverse-complemented source.
    * srcSize -- The size of the entire source sequence, not just the parts involved in the alignment.
    * text -- The nucleotides (or amino acids) in the alignment and any insertions (dashes) as well.
	
	 */
	private Feature parseSequenceLine(String line)
	{		
		String [] tokens = line.split(" +");
		
		boolean isGenomeDotChromeNotation = tokens[1].contains(".");
		String genomeName = null;
		String chromosomeName = null;
		if(isGenomeDotChromeNotation)
		{
			genomeName = tokens[1].substring(0, tokens[1].indexOf("."));
			chromosomeName = tokens[1].substring(tokens[1].indexOf(".")+1);
		}
		else
		{
			genomeName = tokens[1];
			chromosomeName = tokens[1];
		}
		
		//either get an existing matching mapset or make a new one if necessary
		MapSet mapset = Utils.getMapSetByName(genomeName, dataSet.allMapSets);
		if(mapset == null) 
		{
			mapset = new MapSet(genomeName);
			//add this to the dataset
			dataSet.allMapSets.add(mapset);
		}
		
		//retrieve the matching chromosome and add the sequence to it
		ChromoMap chromoMap = mapset.getMapByName(chromosomeName);
		if(chromoMap == null)
		{
			chromoMap = new ChromoMap(chromosomeName);
			mapset.addMap(chromoMap);
		}		
		
		//for now we use the start of the sequence as its name
		String name = tokens[2];
		float start = Float.parseFloat(tokens[2]);
		int size = Integer.parseInt(tokens[3]);
		
		//now add the sequence as a feature to the map
		Feature feature = new Feature(name);
		feature.setStart(start);
		feature.setStop((start+size));
		chromoMap.addFeature(feature);
		
		return feature;
	}
	
}
