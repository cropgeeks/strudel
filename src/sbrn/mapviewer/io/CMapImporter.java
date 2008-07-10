package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.data.*;

/** File handler class for dealing with CMap-formatted data files. */
public class CMapImporter
{
	// The file we're trying to load from
	private File filename;
	
	private MapSet mapset = new MapSet();
	
	public static void main(String[] args)
		throws Exception
	{
		CMapImporter importer = new CMapImporter(new File(args[0]));
		
		MapSet mapset = importer.loadMapSet();
	}
	
	public CMapImporter(File filename)
		{ this.filename = filename;	}
	
	/** Alternative constructor to force loading into an existing MapSet. */
	public CMapImporter(File filename, MapSet mapset)
	{
		this.filename = filename;
		this.mapset = mapset;
	}
	
	/**
	 * Loads map and feature data from CMap, storing it within a MapSet object
	 * that is returned when done.
	 */
	public MapSet loadMapSet()
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		// Ignore the first line of the file
		in.readLine();
		String str = in.readLine();	
				
		while (str != null)
		{
			StringTokenizer st = new StringTokenizer(str, "\t");
			
			// Find the map in question for this line
			ChromoMap map = getMapByName(st.nextToken());
			
			// Read (and create) the Feature
			Feature f = new Feature(st.nextToken());
							
			// And its distance value
			float distance = 0;
			try { distance = Float.parseFloat(st.nextToken()); }
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Feature " + f.getName() + " "
					+ "does not appear to have a valid distance");
			}			
			f.setStart(distance);
			f.setStop(distance);
			
			// Feature aliases (if any)
//			String[] aliases = st.nextToken().split(",");
//			for (String s: aliases)
//				f.addAlias(s);
			
			map.addFeature(f);
			
			str = in.readLine();
		}
		
		in.close();
		
		return mapset;
	}
	
	// Searches the MapSet for a map with this name, returning either the map
	// or a new instance of a map if it doesn't exist.
	private ChromoMap getMapByName(String name)
	{
		ChromoMap map = mapset.getMapByName(name);
		
		if (map == null)
		{
			map = new ChromoMap(name);
			mapset.addMap(map);
		}
		
		return map;
	}
}