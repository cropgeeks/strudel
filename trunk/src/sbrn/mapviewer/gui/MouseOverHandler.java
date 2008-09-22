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

	//used for displaying feature labels when the cursor hovers over a feature
	public Vector<Feature> detectMouseOver(int x, int y)
	{
		Vector<Feature> match = null;

		// first figure out which chromosome we are in
		selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, x, y);

		if (selectedMap != null && selectedMap.arraysInitialized)
		{
			//some nasty logic to do with the persistent display of labels
			if(previousMap == null || (previousMap != null && !previousMap.persistHighlightedFeatures))
			{
				clearPreviousMap();

				// figure out where on the chromosome the hit has occurred, in pixels from the top of the chromosome
				int pixelNumberFromTop = (int)(y - selectedMap.boundingRectangle.getY());
				int errorMargin = 1;

				// now look up this value in the feature arrays of the map
				for (int i = 0; i < selectedMap.allFeaturePositions.length; i++)
				{
					//check the current pixel number from the top against the values in the array
					//if there is one that is the same (incl. error margin) then add the corresponding feature to the vector
					if(selectedMap.allFeaturePositions[i] == pixelNumberFromTop ||
									selectedMap.allFeaturePositions[i] == pixelNumberFromTop + errorMargin ||
									selectedMap.allFeaturePositions[i] == pixelNumberFromTop - errorMargin)
					{
						if(match == null)
						{
							match = new Vector<Feature>();
						}
						match.add(selectedMap.allFeatures[i]);
					}
				}

				// we have a match
				if (match != null && selectedMap.owningSet.paintAllMarkers)
				{
					// set the vector object of the selected map and repaint
					selectedMap.highlightedFeatures = match;
					selectedMap.drawHighlightedFeatures = true;
					winMain.mainCanvas.updateCanvas(false);
				}

				//remember this map for the next time
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
//			if (selectedMap.owningSet.equals(winMain.mainCanvas.targetGMapSet))
//			{
//				winMain.targetAnnotationPanel.getAnnotationList().setListData(formattedData);
//			}
//			else
//			{
//				winMain.referenceAnnotationPanel.getAnnotationList().setListData(formattedData);
//			}
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
//			winMain.targetAnnotationPanel.getAnnotationList().setListData(formattedData);
//			winMain.referenceAnnotationPanel.getAnnotationList().setListData(formattedData);
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

			winMain.mainCanvas.updateCanvas(false);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
