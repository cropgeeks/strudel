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
	
	public void detectMouseOver(int x, int y)
	{
		// first figure out which chromosome we are in
		selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, x, y);
		if (selectedMap != null)
		{
			clearPreviousMap();
			previousMap = selectedMap;

			// figure out where on the chromosome the hit has occurred, in percent of the total height
			// the distance from the top of the chromosome to the hit y location, in percent of the chromosome height
			int percentDistanceFromTop = (int) (((y - selectedMap.boundingRectangle.getY()) / selectedMap.height) * 100);
			
			// now look up this value in the lookup table of the map
			LinkedList<Feature> match = selectedMap.allFeaturesPosLookup.get(percentDistanceFromTop);
			// we have a match
			if (match != null)
			{
//				System.out.println("match found: "  + match.toString());
				
				// set the vector object of the selected map and repaint
				selectedMap.highlightedFeatures = match;
				winMain.mainCanvas.repaint();
				
//				// also set the label text in the annotation window unless we are fully zoomed out
//				if (selectedMap.owningSet.equals(winMain.mainCanvas.targetGMapSet) && winMain.mainCanvas.targetGMapSet.paintLinkedMarkers)
//				{
//					winMain.targetAnnotationPanel.getLocusInfo().setText(match.getName());
//					winMain.targetAnnotationPanel.getAnnotationTextArea().setText(
//									match.getAnnotation());
//				}
//				else if (winMain.mainCanvas.referenceGMapSet.paintLinkedMarkers)
//				{
//					winMain.referenceAnnotationPanel.getLocusInfo().setText(match.getName());
//					winMain.referenceAnnotationPanel.getAnnotationTextArea().setText(
//									match.getAnnotation());
//				}
			}
		}
		else
		{
			clearPreviousMap();
		}
		
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void clearPreviousMap()
	{
		// reset the selected map if the mouse is not over it
		if (previousMap != null)
		{
			previousMap.highlightedFeatures = null;
			previousMap = null;
			
			// reset all the annotation labels so they show nothing
			winMain.targetAnnotationPanel.getLocusInfo().setText("");
			winMain.targetAnnotationPanel.getAnnotationTextArea().setText("");
			winMain.referenceAnnotationPanel.getLocusInfo().setText("");
			winMain.referenceAnnotationPanel.getAnnotationTextArea().setText("");
			
			winMain.mainCanvas.repaint();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
