package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

public class CMapLinkImporterTest
{
	public static void main(String[] args)
	{
		CMapLinkImporter links = new CMapLinkImporter(
			new File("D:\\Projects\\MapViewer\\cmap formatted files\\simple-corr.txt"));
		CMapImporter importer = null;
		
		try
		{
			long s = System.currentTimeMillis();
									
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\simple-map12.txt"));			
			MapSet mapSet1 = importer.loadMapSet();
			mapSet1.printSummary();
			links.addMapSet(mapSet1);
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\simple-map23.txt"));			
			MapSet mapSet2 = importer.loadMapSet();
			mapSet2.printSummary();
			links.addMapSet(mapSet2);
			
			long e = System.currentTimeMillis();
			Strudel.logger.fine("Time to load data: " + (e-s) + "ms");
			
			s = System.currentTimeMillis();
			LinkSet linkSet = links.loadLinkSet();
			e = System.currentTimeMillis();
			Strudel.logger.fine("Time to associate links: " + (e-s) + "ms");
			
			Strudel.logger.fine(linkSet.toString());
						
			Strudel.logger.fine("LinkSet contains " + linkSet.getLinks().size() + " links");
			
			
			for (Link link: linkSet)
			{
				Strudel.logger.fine(link.toString());
				Strudel.logger.fine("  " + linkSet.isLinkUniqueToMapSet(link));
			}
			
			Strudel.logger.fine("\n");
			
			LinkSet newSet = linkSet.getLinksBetweenAllMapSets();
			for (Link link: newSet)
			{
				Strudel.logger.fine(link.toString());
			}
			
			Strudel.logger.fine("\n");
			
			newSet = linkSet.getLinksBetweenMaps(mapSet1.getMap(0), mapSet2.getMap(1));
			for (Link link: newSet)
			{
				Strudel.logger.fine(link.toString());
			}
		
		}
		catch (Exception e) { Strudel.logger.fine(e.toString()); }
	}
		
	private static void cmapTest(String[] args)
	{
		CMapLinkImporter links = new CMapLinkImporter(new File(args[0]));
		CMapImporter importer = null;
		
		try
		{
			long s = System.currentTimeMillis();
			
			Runtime rt = Runtime.getRuntime();
			Strudel.logger.fine("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_mxb_edited.maps"));			
			MapSet mapSet1 = importer.loadMapSet();
			mapSet1.printSummary();
			links.addMapSet(mapSet1);
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_owb_edited.maps"));			
			MapSet mapSet2 = importer.loadMapSet();
			mapSet2.printSummary();
			links.addMapSet(mapSet2);
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_sxm_edited.maps"));			
			MapSet mapSet3 = importer.loadMapSet();
			mapSet3.printSummary();
			links.addMapSet(mapSet3);
			
			Strudel.logger.fine("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\rice_pseudo4_os.maps"));			
			MapSet mapSet4 = importer.loadMapSet();
			mapSet4.printSummary();
			links.addMapSet(mapSet4);
			
			Strudel.logger.fine("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			long e = System.currentTimeMillis();
			Strudel.logger.fine("Time to load data: " + (e-s) + "ms");
			
			s = System.currentTimeMillis();
			LinkSet linkSet = links.loadLinkSet();
			e = System.currentTimeMillis();
			Strudel.logger.fine("Time to associate links: " + (e-s) + "ms");
			
			Strudel.logger.fine(linkSet.toString());
			
			Strudel.logger.fine("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			Strudel.logger.fine("LinkSet contains " + linkSet.getLinks().size() + " links");
		
			
			
		}
		catch (Exception e) { Strudel.logger.fine(e.toString()); }
	}
}