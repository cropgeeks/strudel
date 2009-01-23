package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

/** File handler class for dealing with JoinMap-formatted data files. */
public class JoinMapImporter
{
	// The file we're trying to load from
	private File filename;
	
	private MapSet mapset = new MapSet();
	
	public static void main(String[] args)
		throws Exception
	{
		JoinMapImporter importer = new JoinMapImporter(new File(args[0]));
		
		MapSet mapset = importer.loadMapSet();
		
		
		for (ChromoMap map: mapset)
		{
			MapViewer.logger.fine("\n");
			MapViewer.logger.fine(map.getName());
			for (Feature feature: map)
				MapViewer.logger.fine("  " + feature.getName() + "\t" + feature.getStart());
		}
	}
	
	public JoinMapImporter(File filename)
		{ this.filename = filename;	}
	
	/** Alternative constructor to force loading into an existing MapSet. */
	public JoinMapImporter(File filename, MapSet mapset)
	{
		this.filename = filename;
		this.mapset = mapset;
	}
	
	public MapSet loadMapSet()
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		ChromoMap currentMap = null;
		
		String str = in.readLine();
		while (str != null)
		{
			MapViewer.logger.fine(str);
			
			StringTokenizer st = new StringTokenizer(str);
			
			if (st.countTokens() == 2)
			{
				String s1 = st.nextToken();
				String s2 = st.nextToken();
				
				// Stupid hack to ensure we know when a new group is beginning
				if (s1.equals("group") || str.equals("[Chart Options]"))
				{				
					// Add the previous Map to the MapSet
					if (currentMap != null)
						mapset.addMap(currentMap);
				
					// Then create a new Map for the data to come
					currentMap = new ChromoMap(s1 + " " + s2);
					
					// Quit scanning at this point
					if (str.equals("[Chart Options]"))
						break;
				}
				else
				{
					float distance = 0;
				
					try { distance = Float.parseFloat(s2); }
					catch (NumberFormatException e)
					{
						throw new NumberFormatException("Feature " + s1 + " "
							+ "does not appear to have a valid distance");
					}
					
					Feature f = new Feature(s1);
					f.setStart(distance);
					f.setStop(distance);
				
					currentMap.addFeature(f);
				}
			}
			
			str = in.readLine();
		}
		
		in.close();
		
		return mapset;
	}
}