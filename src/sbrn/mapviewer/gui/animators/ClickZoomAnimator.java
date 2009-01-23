package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.*;
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
	public boolean done = false;

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
		zoomHandler.isClickZoomRequest = true;

//		MapViewer.logger.fine("ClickZoomAnimator finalZoomFactor = " + finalZoomFactor);
		
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
		
		MapViewer.logger.fine("zoomFactorIncrement = " + zoomFactorIncrement);
		MapViewer.logger.fine("totalFrames = " + totalFrames);
		MapViewer.logger.fine("fps = " + fps);
		MapViewer.logger.fine("millis = " + millis);

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
			
			MapViewer.logger.finest("selectedSet.zoomFactor before adjustment = " + selectedSet.zoomFactor);

			// set the new zoom factor
			selectedSet.zoomFactor = selectedSet.zoomFactor + zoomFactorIncrement;

			//don't let the zoom factor fall below 1
			if(selectedSet.zoomFactor < 1)
				selectedSet.zoomFactor = 1;			
			
			MapViewer.logger.finest("selectedSet.zoomFactor after adjustment = " + selectedSet.zoomFactor);

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
			
			//update zoom control position
			MapViewer.winMain.fatController.updateZoomControls();
		}
		
		//do a final zoom adjust to ensure that we are at the right zoom level
		selectedSet.zoomFactor = finalZoomFactor;
		zoomHandler.adjustZoom(selectedMap, finalTotalY, finalChromoHeight, finalChromoHeight/2);

		//update overviews
		MapViewer.winMain.fatController.updateOverviewCanvases();
		
		//update zoom control position
		MapViewer.winMain.fatController.updateZoomControls();

		//now update the arrays with the position data
		MapViewer.winMain.fatController.initialisePositionArrays();
		
		//enable drawing of markers providing we have zoomed in, not out
		if(selectedSet.zoomFactor > 1)
			selectedSet.thresholdAllMarkerPainting = selectedSet.zoomFactor;

		//turn drawing of map index back on
		selectedMap.drawChromoIndex = true;
		
		//turn antialiasing on and repaint
		mainCanvas.antiAlias = true;
		mainCanvas.updateCanvas(true);
		
		done = true;
		
		zoomHandler.isClickZoomRequest = false;
		
		MapViewer.logger.fine("selectedSet.zoomFactor final = " + selectedSet.zoomFactor);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
