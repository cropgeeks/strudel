package sbrn.mapviewer.gui.animators;

import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class PanZoomAnimator extends Thread implements ItemListener
{
	// ============================================vars============================================
	
	int fps;
	int millis;
	float finalScalingFactor;
	GChromoMap selectedMap;
	MainCanvas mainCanvas;
	int mousePressedY;
	int mouseReleasedY;
	CanvasZoomHandler zoomHandler;
	boolean animate;
	
	//a checkbox for asking the user whether they want to be reminded each time they have reached the max zoom level through pan zooming
	JCheckBox maxZoomMessageCheckBox = new JCheckBox("Don't show this dialog again"); 
	
	// ============================================curve'tor============================================
	
	public PanZoomAnimator(int fps, int millis, float finalScalingFactor, GChromoMap selectedMap,
					MainCanvas mainCanvas, int mousePressedY, int mouseReleasedY,
					CanvasZoomHandler zoomHandler,boolean animate)
	{
		this.fps = fps;
		this.millis = millis;
		this.finalScalingFactor = finalScalingFactor;
		this.selectedMap = selectedMap;
		this.mainCanvas = mainCanvas;
		this.mousePressedY = mousePressedY;
		this.mouseReleasedY = mouseReleasedY;
		this.zoomHandler = zoomHandler;
		this.animate = animate;
		
		//configure the maxZoomMessageCheckBox
		maxZoomMessageCheckBox.addItemListener(this);
		if(Prefs.showMaxZoomLevelMessage)
			maxZoomMessageCheckBox.setSelected(false);
		else
			maxZoomMessageCheckBox.setSelected(true);
	}
	
	// ============================================methods============================================
	
	public void run()
	{
		zoomHandler.isPanZoomRequest = true;
		
		//turn antialiasing off
		mainCanvas.antiAlias = false;
		
		int totalFrames = Math.round(fps * (millis / 1000.0f));
		
		// divide the difference between a scaling factor of 1 and the final scaling factor by the number of frames we want to use for this
		float increment = (finalScalingFactor - 1) / totalFrames;
		
		GMapSet selectedSet = selectedMap.owningSet;
		
		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
		
		// these are the values we want for the final iteration
		float finalZoomFactor = selectedSet.zoomFactor * finalScalingFactor;		
		float finalChromoHeight = (int) (selectedSet.chromoHeight * finalScalingFactor);
		// the distance from the top of the chromosome to the mousePressedY location, in pixels
		float initialDistFromTop = (float) (mousePressedY - selectedMap.boundingRectangle.getY() + (mouseReleasedY - mousePressedY) / 2);
		float initialDistFromTopProportion = initialDistFromTop / (float) selectedMap.boundingRectangle.getHeight();
		
		// the new total Y extent of the genome in pixels
		int finalTotalY = (int) (((selectedSet.totalY - combinedSpacers) * finalScalingFactor) + combinedSpacers);
		
		// these are the amounts we need to increment things by
		// follows the pattern of: (final value minus current value) divided by the total number of frames
		float zoomFactorIncrement = (finalZoomFactor - selectedSet.zoomFactor) / totalFrames;
		float chromoHeightIncrement = (finalChromoHeight - selectedSet.chromoHeight) / totalFrames;
		float totalYIncrement = (finalTotalY - selectedSet.totalY) / totalFrames;
		boolean maxZoomFactorReached = false;
		
		if (animate)
		{
			// now loop for the number of total frames, zooming in by a bit each time
			for (int i = 0; i < totalFrames; i++)
			{
				// sleep for the amount of animation time divided by the fps value
				try
				{
					Thread.sleep(millis / totalFrames);
				}
				catch (InterruptedException e)
				{
				}
				
				// work out the current scaling factor
				// next zoom factor divided by current zoom factor
				float currentScalingFactor = (selectedSet.zoomFactor + zoomFactorIncrement) / selectedSet.zoomFactor;
				
				// set the new zoom factor
				//if this does not exceed the max zoom factor
				if (selectedSet.zoomFactor < Constants.MAX_ZOOM_FACTOR)
					selectedSet.zoomFactor += zoomFactorIncrement;
				//if this DOES exceed the max zoom factor
				else
				{
					if (Prefs.showMaxZoomLevelMessage)
					{
						TaskDialog.info("Maximum zoom level reached for map set " + selectedSet.name, "Close", maxZoomMessageCheckBox);
					}
					zoomHandler.isPanZoomRequest = false;
					maxZoomFactorReached = true;
					
					break;
				}
				
				// work out the chromo height and total genome height for when the new zoom factor will have been applied
				int newChromoHeight = Math.round(selectedSet.chromoHeight + chromoHeightIncrement);
				
				// the distance from the top of the chromosome to the mousePressedY location, in pixels
				int distFromTop = Math.round(initialDistFromTopProportion * newChromoHeight);
				// the same from the bottom of the chromosome
				int distFromBottom = newChromoHeight - distFromTop;
				
				// the new total Y extent of the genome in pixels
				int newTotalY = Math.round(selectedSet.totalY + totalYIncrement);
				
				// adjust the zoom and increment the scaling factor
				// this call includes the redraw of the main canvas
				zoomHandler.adjustZoom(selectedMap, newTotalY, newChromoHeight, distFromBottom);
				currentScalingFactor += increment;
				
				//update visible zoom info
				Strudel.winMain.fatController.updateAllZoomControls();
			}
		}
		//if we have not reached the max zoom factor with this we need to do one more zoom adjust 
		//explicitly here to make sure we have all the final intended values and have not fallen short
		//of these due to rounding errors etc.
		if (!maxZoomFactorReached)
		{
			selectedSet.zoomFactor = finalZoomFactor;
			zoomHandler.adjustZoom(
							selectedMap,
							finalTotalY,
							(int) finalChromoHeight,
							(int) (finalChromoHeight - (initialDistFromTopProportion * finalChromoHeight)));
		}
		
		//now update the arrays with the position data
		Strudel.winMain.fatController.initialisePositionArrays();
		//update zoom control position
		Strudel.winMain.fatController.updateAllZoomControls();
				
		//repaint
		Utils.repaintAntiAliased();		
		
		zoomHandler.isPanZoomRequest = false;
	}
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//here we handle user actions for the checkbox that decides whether we show the max zoom level reached message each time
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource().equals(maxZoomMessageCheckBox))
		{
			if(maxZoomMessageCheckBox.isSelected())
				Prefs.showMaxZoomLevelMessage = false;
			else
				Prefs.showMaxZoomLevelMessage = true;
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
