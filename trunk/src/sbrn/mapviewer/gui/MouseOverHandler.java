package sbrn.mapviewer.gui;

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
			// System.out.println("mouseover detected in chromo " + selectedMap.name + " of genome " + selectedMap.owningSet.name);
			
			// figure out where on the chromosome the hit has occurred, in percent of the total height
			// the distance from the top of the chromosome to the hit y location, in percent of the chromosome height
			int percentDistanceFromTop = (int) (((y - selectedMap.boundingRectangle.getY()) / selectedMap.height) * 100);
			
			// now look up this value in the lookup table of the map
			Feature match = selectedMap.linkedFeaturePosLookup.get(percentDistanceFromTop);
			// we have a match
			if (match != null)
			{
				// add this feature and the next two in either direction (up and down) to the hash table
				Vector<Feature> highlightedFeatures = new Vector<Feature>();
				// get the two before and after from the list maintained by the map object and add them too
				LinkedList<Feature> fList = selectedMap.chromoMap.getFeatureList();
				int index = fList.indexOf(match);
				// highlightedFeatures.add(fList.get(index - 2));
				// highlightedFeatures.add(fList.get(index - 1));
				highlightedFeatures.add(match);
				// highlightedFeatures.add(fList.get(index + 1));
				// highlightedFeatures.add(fList.get(index + 2));
				
				// set this object on the selected map and repaint
				selectedMap.highlightedFeatures = highlightedFeatures;
				winMain.mainCanvas.repaint();
				
				// also set the label text in the annotation window unless we are fully zoomed out
				if (selectedMap.owningSet.equals(winMain.mainCanvas.targetGMapSet)  && winMain.mainCanvas.targetGMapSet.paintMarkers)
				{
					winMain.targetAnnotationPanel.getLocusInfo().setText(match.getName());
					winMain.targetAnnotationPanel.getAnnotationTextArea().setText(match.getAnnotation());
				}
				else if(winMain.mainCanvas.referenceGMapSet.paintMarkers)
				{
					winMain.referenceAnnotationPanel.getLocusInfo().setText(match.getName());
					winMain.referenceAnnotationPanel.getAnnotationTextArea().setText(match.getAnnotation());
				}
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
			winMain.mainCanvas.repaint();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
