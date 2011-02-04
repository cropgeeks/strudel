package sbrn.mapviewer.gui.handlers;

import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;

public class MouseOverHandler
{
	// =========================================vars=======================================

	WinMain winMain;
	public GChromoMap selectedMap;
	GChromoMap previousMap;

	//the margin of error used for allowing detects during mouseover, in pixels
	//a feature will be detected if the mouse is over its position +/- the errorMargin value
	int errorMargin = 1;

	// =========================================curve'tors=======================================

	public MouseOverHandler(WinMain winMain)
	{
		this.winMain = winMain;
	}

	// =========================================methods=======================================

	//used for displaying feature labels when the cursor hovers over a feature
	public Vector<Feature> detectMouseOver(int x, int y)
	{
		Vector<Feature> matches = null;

		try
		{
			// first figure out which chromosome we are in
			selectedMap = Utils.getSelectedMap(Strudel.winMain.dataSet.gMapSets, x, y);
			
			//make this mapset the selected set and update the zoom control panel
			if(selectedMap != null)
				Strudel.winMain.genomeLabelPanel.selectGMapSet(selectedMap.owningSet);

			//update the hint panel
			HintPanel.upDate();

			//a map has been selected i.e. the mouse is over it
			if (selectedMap != null && selectedMap.arraysInitialized)
			{
				//this additional check clears any existing labels if we are just moving the mouse within the same map
				if(selectedMap == previousMap && selectedMap.drawMouseOverFeatures)
				{
					matches = null;
					selectedMap.drawMouseOverFeatures = false;
					winMain.mainCanvas.updateCanvas(false);
				}
				
				//these are conditions for the persistent display of labels
				if (previousMap == null || (previousMap != null && !previousMap.persistHighlightedFeatures))
				{

					// figure out where on the chromosome the hit has occurred, in pixels from the top of the chromosome
					int pixelNumberFromTop = (int) (y - selectedMap.boundingRectangle.getY());

					// now look up this value in the feature arrays of the map
					for (int i = 0; i < selectedMap.allFeaturePositions.length; i++)
					{
						//check the current pixel number from the top against the values in the array
						//if there is one that is the same (incl. error margin) then add the corresponding feature to the vector
						if (selectedMap.allFeaturePositions[i] <= pixelNumberFromTop + errorMargin &&
										selectedMap.allFeaturePositions[i] >= pixelNumberFromTop - errorMargin)
						{
							if (matches == null)
							{
								matches = new Vector<Feature>();
							}
							if(selectedMap.allFeatures[i] != null)
							{
								matches.add(selectedMap.allFeatures[i]);
							}
						}
					}

					// we have a match
					if (matches != null && selectedMap.owningSet.showAllFeatures)
					{
						// set the vector object of the selected map and repaint
						selectedMap.mouseOverFeatures = matches;
						selectedMap.drawMouseOverFeatures = true;
						winMain.mainCanvas.updateCanvas(false);
					}

					//remember this map for the next time
					previousMap = selectedMap;
				}
			}
			else
			{
				clearPreviousMap();

				HintPanel.setLabel(HintPanel.overviewStr);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return matches;
	}


	// ------------------------------------------------------------------------------------------------------------------------------------------------

	private void clearPreviousMap()
	{
		
		// reset the selected map if the mouse is not over it
		if (previousMap != null  && !previousMap.persistHighlightedFeatures && previousMap.owningSet.showAllFeatures)
		{
			previousMap.mouseOverFeatures.clear();
			previousMap.drawMouseOverFeatures = false;
			previousMap = null;
			winMain.mainCanvas.updateCanvas(false);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
