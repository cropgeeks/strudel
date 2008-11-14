package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;

public class ClickZoomAnimator extends Thread
{
	// ============================================vars============================================

	int fps;
	int millis;
	GChromoMap selectedMap;
	MainCanvas mainCanvas;
	float finalZoomFactor;
	int finalChromoHeight;
	int finalTotalY;
	CanvasZoomHandler zoomHandler;

	// ============================================c'tor============================================

	public ClickZoomAnimator(int fps, int millis, GChromoMap selectedMap, MainCanvas mainCanvas,
					float finalZoomFactor, int finalTotalY, int finalChromoHeight, CanvasZoomHandler zoomHandler)
	{
		this.fps = fps;
		this.millis = millis;
		this.selectedMap = selectedMap;
		this.mainCanvas = mainCanvas;
		this.finalZoomFactor = finalZoomFactor;
		this.finalChromoHeight = finalChromoHeight;
		this.zoomHandler = zoomHandler;
		this.finalTotalY = finalTotalY;
	}

	// ============================================methods============================================

	public void run()
	{
		//turn antialiasing off
		mainCanvas.antiAlias = false;
		
		//turn drawing of map index off
		selectedMap.drawChromoIndex = false;

		//the total number of frames we need to render
		int totalFrames = Math.round(fps * (millis / 1000.0f));
		GMapSet selectedSet = selectedMap.owningSet;

		// these are the amounts we need to increment things by
		// follows the pattern of: (final value minus current value) divided by the total number of frames
		float zoomFactorIncrement = (finalZoomFactor - selectedSet.zoomFactor) / totalFrames;
		float chromoHeightIncrement = (finalChromoHeight - selectedSet.chromoHeight) / totalFrames;
		float totalYIncrement = (finalTotalY - selectedSet.totalY) / totalFrames;

		// now loop for the number of total frames, zooming in by a bit each time
		for (int i = 0; i < totalFrames; i++)
		{
			// sleep for the amount of animation time divided by the totalFrames value
			try
			{
				Thread.sleep((long) (millis / totalFrames));
			}
			catch (InterruptedException e)
			{
			}

			// set the new zoom factor
			selectedSet.zoomFactor += zoomFactorIncrement;

			//don't let the zoom factor fall below 1
			if(selectedSet.zoomFactor < 1)
				selectedSet.zoomFactor = 1;

			// work out the chromo height and total genome height for when the new zoom factor will have been applied
			int newChromoHeight = Math.round(selectedSet.chromoHeight + chromoHeightIncrement);

			// distance from the bottom of the chromosome -- is half the height of the chromosome as we want it centered
			int distFromBottom = newChromoHeight/2;

			// the new total Y extent of the genome in pixels
			int newTotalY = Math.round(selectedSet.totalY + totalYIncrement);

			// adjust the zoom
			// this call includes the redraw of the main canvas
			zoomHandler.adjustZoom(selectedMap, newTotalY, newChromoHeight, distFromBottom);
			
			//now update the arrays with the position data
			MapViewer.winMain.fatController.initialisePositionArrays();
			
			MapViewer.logger.finest("selectedSet.zoomFactor = " + selectedSet.zoomFactor);
		}
		
		//do a final zoom adjust to ensure that we are at the right zoom level
		selectedSet.zoomFactor = finalZoomFactor;
		zoomHandler.adjustZoom(selectedMap, finalTotalY, finalChromoHeight, finalChromoHeight/2);

		//update overviews
		MapViewer.winMain.fatController.updateOverviewCanvases();

		//now update the arrays with the position data
		MapViewer.winMain.fatController.initialisePositionArrays();

		//turn drawing of map index back on
		selectedMap.drawChromoIndex = true;
		
		//turn antialiasing on and repaint
		mainCanvas.antiAlias = true;
		mainCanvas.updateCanvas(true);
		
		MapViewer.logger.finest("selectedSet.zoomFactor final = " + selectedSet.zoomFactor);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
