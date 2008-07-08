package sbrn.mapviewer.gui;

import java.util.*;

import sbrn.mapviewer.gui.entities.*;

public class MouseOverHandler
{
	// =========================================vars=======================================
	
	WinMain winMain;
	GChromoMap selectedMap;
	GChromoMap previousMap;

	
	// =========================================c'tors=======================================
	
	public MouseOverHandler(WinMain winMain)
	{
		this.winMain = winMain;
	}
	
	// =========================================methods=======================================
	
	public void detectMouseOver(int x, int y)
	{
		// first figure which chromosome we are in
		selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, x, y);
		if (selectedMap != null)
		{
			previousMap = selectedMap;
//			//System.out.println("mouseover detected in chromo " + selectedMap.name + " of genome " + selectedMap.owningSet.name);
			
			// figure out where on the chromosome the hit has occurred, in percent of the total height		
			//the distance from the top of the chromosome to the hit y location, in percent of the chromosome height
			int percentDistanceFromTop = (int) (((y - selectedMap.boundingRectangle.getY()) / selectedMap.height) * 100);
			//System.out.println("percentDistanceFromTop = " + percentDistanceFromTop);
			
			//now look up this value in the lookup table of the map
			String match = selectedMap.linkedFeaturePosLookup.get(percentDistanceFromTop);
			if(match != null)
			{
				
				TreeMap<Integer,String> highlightedFeatures = new TreeMap<Integer, String>();
				//System.out.println("MATCH FOUND: " + match);				
				//add this feature and the next two in either direction to the hash table
				highlightedFeatures.put(percentDistanceFromTop, match);
				selectedMap.highlightedFeatures = highlightedFeatures;
				winMain.mainCanvas.repaint();
			}
		}
		else
		{
			//System.out.println("no map under mouse");
			//reset the selected map if the mouse is not over it
			if(previousMap != null)
			{
				//System.out.println("clearing selected map");
				previousMap.highlightedFeatures = null;
				previousMap  = null;
				winMain.mainCanvas.repaint();
			}
		}
			

	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
