package sbrn.mapviewer.io;

import java.io.*;
import java.text.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class CMapLinkImporter
{
	private File filename;
	private LinkSet linkSet = new LinkSet();
	
	private LinkedList<MapSet> mapSets = new LinkedList<MapSet>();
	
	public CMapLinkImporter(File filename)
	{
		this.filename = filename;
	}
	
	/** Adds a MapSet that (may) contain Features referred to by the data. */
	public void addMapSet(MapSet mapset)
	{
		mapSets.add(mapset);
	}
	
	public LinkSet loadLinkSet()
		throws IOException, ArrayIndexOutOfBoundsException
	{ 
		try
		{
			MapViewer.logger.fine("loadLinkSet()");
			// We load feature links by scanning the data file. Other than the first
			// line, expect to see one link (pair of features) per line
			
			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(filename));
			}
			catch (Exception e)
			{
				MapViewer.winMain.openFileDialog.dataLoadingDialog.setVisible(false);
				TaskDialog.error("File " + filename + " not found. Comparative data not loaded.", "Close");
				e.printStackTrace();
			}		
			in.readLine();
			
			String str = in.readLine();		
			while (str != null && str.length() > 0)
			{		
				String[] t = str.split("\\t");

				// Find all features with the first name and all with the second
				LinkedList<Feature> f1List = getFeaturesByName(t[0]);
				LinkedList<Feature> f2List = getFeaturesByName(t[1]);

				// Pair up every instance of f1 with f2
				for (Feature f1: f1List)
					for (Feature f2: f2List)
					{
						Link link = new Link(f1, f2);
						linkSet.addLink(link);
						
						// We also add the Link to each Feature so the Feature
						// itself knows about the links it has with others
						f1.getLinks().add(link);
						f2.getLinks().add(link);
						
						//add the BLAST score as evidence
						 DecimalFormat df = new DecimalFormat("0.###E0");
						Number blastScore = df.parse(t[2]);
						MapViewer.logger.finest("link between " + f1.getName() + " and " + f2.getName());
						 MapViewer.logger.finest("blastScore = "+ blastScore.toString());
						link.setBlastScore(blastScore.doubleValue());
											
						// TODO: Do we want to add a list of references Features to
						// the Feature object itself, so it knows who it links to?
						// If so, how do we deal with, eg removing MapSets and
						// keeping these lists (and the LinkSet!) up to date.
					}
				
				str = in.readLine();
			}
			
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MapViewer.logger.fine("returning linkset of size " + linkSet.size());
		
		return linkSet;
	}
	
	// Searches over all MapSets to find every feature whose name matches the
	// one given.
	private LinkedList<Feature> getFeaturesByName(String name) throws Exception
	{		

		LinkedList<Feature> list = new LinkedList<Feature>();
		Feature feature = null;
				
		try
		{
			for (MapSet mapset: mapSets)
			{
				for (ChromoMap map: mapset)
				{ 
					// TODO: Should this be a case-insensitive search?
//					MapViewer.logger.fine("looking for  feature " + name );
					feature = map.getFeature(name);
					if (feature != null)
					{
//						MapViewer.logger.fine("adding feature " + feature.getName());
						list.add(feature);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
}