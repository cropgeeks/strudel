package sbrn.mapviewer.io;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

public class IOUtils
{
	public static void addLinkToLinkSet(String genome1Name, String genome2Name, LinkedList<LinkSet> allLinkSets, Feature feature1, Feature feature2,
					String eValueStr, String annotation, Color color) throws Exception
	{
		LinkSet linkSet = null;
		//check whether a linkset between these two genomes exists already
		for (LinkSet ls : allLinkSets)
		{
			if(ls.getMapSets().size() == 0 || ls.getMapSets() == null)
				throw new Exception("Homology cannot be processed - check the features involved have feature entries in the file.");

			String mapset1 = ls.getMapSets().get(0).getName();
			String mapset2 = ls.getMapSets().get(1).getName();

			if ((mapset1.equalsIgnoreCase(genome1Name) && mapset2.equalsIgnoreCase(genome2Name)) ||
							(mapset1.equalsIgnoreCase(genome2Name) && mapset2.equalsIgnoreCase(genome1Name)))
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

		//this method adds the link between the two features to the linkset
		if(linkSet != null && feature1 != null && feature2 != null)
			addLinkToLinkset(linkSet, feature1, feature2, eValueStr, annotation, color);
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static void addLinkToLinkset(LinkSet linkSet, Feature feature1, Feature feature2, String blastScoreStr, String linkAnnotation, Color color)
	{
		// Pair up every instance of f1 with f2
		Link link = new Link(feature1, feature2);
		linkSet.addLink(link);

		// We also add the Link to each Feature so the Feature
		// itself knows about the links it has with others
		feature1.getLinks().add(link);
		feature2.getLinks().add(link);

		//add the BLAST score as evidence
		try
		{
			DecimalFormat df = new DecimalFormat("0.###E0");
			Number blastScore = df.parse(blastScoreStr);
			link.setBlastScore(blastScore.doubleValue());
		}
		catch (ParseException e)
		{
			throw new NumberFormatException("The homology between " + feature1 + " and " + feature2 + " contains an invalid e-Value. ");
		}

		//add the annotation, if there is any
		if(linkAnnotation != null)
			link.setAnnotation(linkAnnotation);

		//add the color, if there is one
		if (color != null)
		{
			link.r = color.getRed();
			link.g = color.getGreen();
			link.b = color.getBlue();
		}

	}
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
