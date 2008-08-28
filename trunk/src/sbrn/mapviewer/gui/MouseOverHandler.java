package sbrn.mapviewer.gui;

import java.text.*;
import java.util.*;

import sbrn.mapviewer.data.*;
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
	
	public Vector<Feature> detectMouseOver(int x, int y)
	{
		Vector<Feature> match = null;
		
		// first figure out which chromosome we are in
		selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, x, y);
		
		if (selectedMap != null)
		{
			if(previousMap == null || (previousMap != null && !previousMap.persistHighlightedFeatures))
			{
				
				clearPreviousMap();
				
				// figure out where on the chromosome the hit has occurred, in percent of the total height
				// the distance from the top of the chromosome to the hit y location, in percent of the chromosome height
				int percentDistanceFromTop = (int) (((y - selectedMap.boundingRectangle.getY()) / selectedMap.height) * 100);
				
				// now look up this value in the lookup table of the map
				match = selectedMap.allFeaturesPosLookup.get(percentDistanceFromTop);
				// we have a match
				if (match != null)
				{			
					// set the vector object of the selected map and repaint
					selectedMap.highlightedFeatures = match;
					selectedMap.drawHighlightedFeatures = true;
					winMain.mainCanvas.repaint();
				}
				
				previousMap = selectedMap;
			}
		}
		else
		{
			clearPreviousMap();
		}
		
		return match;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	// update the annotation info displayed
	public void updateAnnotationDisplay(Vector<Feature> selectedFeatures, int x, int y)
	{
		Vector<String> formattedData = new Vector<String>();
		GChromoMap selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, x, y);
		//if we got here by clicking on a map
		if (selectedMap != null)
		{
			//set the vector of selected features for this map
			selectedMap.highlightedFeatures = selectedFeatures;
			selectedMap.persistHighlightedFeatures = true;
			
			// format the data for the JList
			// we need the name of the feature and the annotation info strung together so 
			//that we can display it in a single column
			for (Feature feature : selectedMap.highlightedFeatures)
			{
				formattedData.add(feature.getName() + " : " + feature.getAnnotation());
			}
			
			// now set this vector to be the new list data for the JList in the annotation panel
			if (selectedMap.owningSet.equals(winMain.mainCanvas.targetGMapSet))
			{
				winMain.targetAnnotationPanel.getAnnotationList().setListData(formattedData);
			}
			else
			{
				winMain.referenceAnnotationPanel.getAnnotationList().setListData(formattedData);
			}
		}
		//if we got here by clicking on the background
		else
		{
			if (previousMap != null)
			{
				previousMap.persistHighlightedFeatures = false;
				clearPreviousMap();
			}
			
			//clear the currently displayed data by just pointing it at the empty vector
			winMain.targetAnnotationPanel.getAnnotationList().setListData(formattedData);
			winMain.referenceAnnotationPanel.getAnnotationList().setListData(formattedData);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------	
	
	private void clearPreviousMap()
	{
		// reset the selected map if the mouse is not over it
		if (previousMap != null  && !previousMap.persistHighlightedFeatures)
		{
			previousMap.highlightedFeatures = null;
			previousMap.drawHighlightedFeatures = false;
			previousMap = null;
			
			winMain.mainCanvas.repaint();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
