package sbrn.mapviewer.io;

import java.io.*;
import java.util.*;

import sbrn.mapviewer.data.*;

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
			System.out.println("Time to load data: " + (e-s) + "ms");
			
			s = System.currentTimeMillis();
			LinkSet linkSet = links.loadLinkSet();
			e = System.currentTimeMillis();
			System.out.println("Time to associate links: " + (e-s) + "ms");
			
			System.out.println(linkSet);
						
			System.out.println("LinkSet contains " + linkSet.getLinks().size() + " links");
			
			
			for (Link link: linkSet)
			{
				System.out.println(link);
				System.out.println("  " + linkSet.isLinkUniqueToMapSet(link));
			}
			
			System.out.println();
			
			LinkSet newSet = linkSet.getLinksBetweenAllMapSets();
			for (Link link: newSet)
			{
				System.out.println(link);
			}
			
			System.out.println();
			
			newSet = linkSet.getLinksBetweenMaps(mapSet1.getMap(0), mapSet2.getMap(1));
			for (Link link: newSet)
			{
				System.out.println(link);
			}
		
		}
		catch (Exception e) { System.out.println(e); }
	}
		
	private static void cmapTest(String[] args)
	{
		CMapLinkImporter links = new CMapLinkImporter(new File(args[0]));
		CMapImporter importer = null;
		
		try
		{
			long s = System.currentTimeMillis();
			
			Runtime rt = Runtime.getRuntime();
			System.out.println("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
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
			
			System.out.println("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			importer = new CMapImporter(new File("D:\\Projects\\MapViewer\\cmap formatted files\\rice_pseudo4_os.maps"));			
			MapSet mapSet4 = importer.loadMapSet();
			mapSet4.printSummary();
			links.addMapSet(mapSet4);
			
			System.out.println("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			long e = System.currentTimeMillis();
			System.out.println("Time to load data: " + (e-s) + "ms");
			
			s = System.currentTimeMillis();
			LinkSet linkSet = links.loadLinkSet();
			e = System.currentTimeMillis();
			System.out.println("Time to associate links: " + (e-s) + "ms");
			
			System.out.println(linkSet);
			
			System.out.println("Mem = " + (rt.totalMemory() - rt.freeMemory()));
			
			System.out.println("LinkSet contains " + linkSet.getLinks().size() + " links");
		
			
			
		}
		catch (Exception e) { System.out.println(e); }
	}
}