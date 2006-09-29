package sbrn.mapviewer.gui.tests;

import java.io.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.io.*;

public class MapSetGraph
{
	public static void main(String[] args)
	{
		MapSetGraph graph = new MapSetGraph();
		
		graph.run();
	}
	
	public MapSetGraph()
	{
	}
	
	public void run()
	{
		MapSet barley, rice;
		LinkSet linkSet;
		
		try
		{
			CMapImporter mapImporter;
									
			mapImporter = new CMapImporter(
				new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_owb_edited.maps")); // 667 links
//				new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_mxb_edited.maps")); // 597 links
//				new File("D:\\Projects\\MapViewer\\cmap formatted files\\new_sxm_edited.maps")); // 509 links
			barley = mapImporter.loadMapSet();
			
			mapImporter = new CMapImporter(
				new File("D:\\Projects\\MapViewer\\cmap formatted files\\rice_pseudo4_os.maps"));
			rice = mapImporter.loadMapSet();
			
			CMapLinkImporter linkImporter = new CMapLinkImporter(
				new File("D:\\Projects\\MapViewer\\cmap formatted files\\barley_SNPS_vs_rice4_corr.data"));
			linkImporter.addMapSet(barley);
			linkImporter.addMapSet(rice);
			linkSet = linkImporter.loadLinkSet();
		}
		catch (Exception e)
		{
			System.out.println(e);
			return;
		}
		
//		System.out.println(linkSet);
		System.out.println("LinkSet=" + linkSet.size());
		
		ChromoMap rice3 = rice.getMapByName("Rice, TIGR v4 Chr. 3");
		System.out.println(rice3.countFeatures());
		LinkSet mapSetLinks = linkSet.getLinksBetweenMapandMapSet(rice3, barley);
		System.out.println("MapSetLinks = " + mapSetLinks.size());
		
		java.text.DecimalFormat d = new java.text.DecimalFormat("#.#####");
		
		for (Link link: mapSetLinks)
		{
			float x, y;
			
			if (link.getFeature1().getOwningMapSet() == barley)
			{
				x = link.getFeature1().getStart();
				y = link.getFeature2().getStart();
			}
			else
			{
				x = link.getFeature2().getStart();
				y = link.getFeature1().getStart();
			}
			
			System.out.println(d.format(x) + ", " + d.format(y));
		}

	}
}