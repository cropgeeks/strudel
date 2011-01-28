package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class CMapLinkImporter
{
	private final File filename;
	private final LinkSet linkSet = new LinkSet();

	private final LinkedList<MapSet> mapSets = new LinkedList<MapSet>();

	public CMapLinkImporter(File filename)
	{
		this.filename = filename;
	}

	/** Adds a MapSet that (may) contain Features referred to by the data. */
	public void addMapSet(MapSet mapset)
	{
		mapSets.add(mapset);
	}

	public LinkSet loadLinkSet() throws Exception
	{
		try
		{
			// We load feature links by scanning the data file. Other than the first
			// line, expect to see one link (pair of features) per line

			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(filename));
			}
			catch (Exception e)
			{
				TaskDialog.error("File " + filename + " not found. Comparative data not loaded.", "Close");
				e.printStackTrace();
			}
			in.readLine();

			String str = in.readLine();
			while (str != null && str.length() > 0)
			{
				String[] t = str.split("\\t");

				// Find all features with the first name and all with the second
				LinkedList<Feature> f1List = Utils.getFeaturesByName(t[0], mapSets);
				LinkedList<Feature> f2List = Utils.getFeaturesByName(t[1], mapSets);

				//the last token in the array contains the annotation but for the user's convenience this may just be left blank
				//need to check for this
				String annotation = null;
				if(t.length == 4)
					annotation = t[3].trim();

				Utils.buildLinkSetFromFeatureLists(linkSet, f1List, f2List, t[2], annotation);

				str = in.readLine();
			}

			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if(linkSet.size() == 0 && Strudel.winMain.dataSet.gMapSets.size() > 1)
		{
			String message = "Linkset does not contain any links between the genomes specified.\n ";
			throw new Exception(message);
		}

		return linkSet;
	}
}