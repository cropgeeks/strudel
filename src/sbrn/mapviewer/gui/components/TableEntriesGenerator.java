package sbrn.mapviewer.gui.components;

import java.util.*;
import sbrn.mapviewer.data.*;

public class TableEntriesGenerator
{
	
	public static LinkedList<ResultsTableEntry> makeTableEntries(Vector<Feature> features)
	{
		LinkedList<ResultsTableEntry> tableEntries = new LinkedList<ResultsTableEntry>();
		
		for (Feature feature : features)
		{
			//check whether this feature is involved in any links
			Vector<Link> links = feature.getLinks();
			if(links.size() > 0)
			{
				//for each link make a new table entry
				for (Link link : links)
				{
					ResultsTableEntry resultsTableEntry = new ResultsTableEntry();
					resultsTableEntry.setTargetFeature(feature);
					//our feature could be feature 1 or feature 2 in the link 
					//set the homolog feature accordingly
					if(feature == link.getFeature1())
					{
						resultsTableEntry.setHomologFeature(link.getFeature2());
					}
					else if(feature == link.getFeature2())
					{
						resultsTableEntry.setHomologFeature(link.getFeature1());
					}
					
					//set the link itself
					resultsTableEntry.setLink(link);
					
					//add to our list
					tableEntries.add(resultsTableEntry);
				}
			}
			else
			{
				//otherwise just generate a single table entry with the feature itself but no homolog
				ResultsTableEntry resultsTableEntry = new ResultsTableEntry();
				resultsTableEntry.setTargetFeature(feature);
				//add to our list
				tableEntries.add(resultsTableEntry);
			}
		}
		
		
		return tableEntries;
	}
	
	
}
