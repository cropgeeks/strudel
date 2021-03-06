package sbrn.mapviewer.gui.animators;

import javax.swing.*;
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

	// ============================================curve'tor============================================

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

	@Override
	public void run()
	{
		GMapSet selectedSet = selectedMap.owningSet;

		zoomHandler.isClickZoomRequest = true;

		//turn drawing of map index off
		selectedMap.drawChromoIndex = false;

		//the total number of frames we need to render
		int totalFrames = Math.round(fps * (millis / 1000.0f));

		// these are the amounts we need to increment things by
		// follows the pattern of: (final value minus current value) divided by the total number of frames
		float zoomFactorIncrement = (finalZoomFactor - selectedSet.zoomFactor) / totalFrames;
		float chromoHeightIncrement = (finalChromoHeight - selectedSet.chromoHeight) / totalFrames;
		float totalYIncrement = (finalTotalY - selectedSet.totalY) / totalFrames;

		// now loop for the number of total frames, zooming in by a bit each time
		for (int i = 0; i < totalFrames; i++)
		{
			// sleep for the amount of animation time divided by the totalFrames value


			// set the new zoom factor
			selectedSet.zoomFactor = selectedSet.zoomFactor + zoomFactorIncrement;

			//don't let the zoom factor fall below 1
			if (selectedSet.zoomFactor < 1)
				selectedSet.zoomFactor = 1;

			// work out the chromo height and total genome height for when the new zoom factor will have been applied
			int newChromoHeight = Math.round(selectedSet.chromoHeight + chromoHeightIncrement);

			// distance from the bottom of the chromosome -- is half the height of the chromosome as we want it centered
			int distFromBottom = newChromoHeight / 2;

			// the new total Y extent of the genome in pixels
			int newTotalY = Math.round(selectedSet.totalY + totalYIncrement);
			
			//set this flag to update the position lookup arrays for mouseover
			selectedSet.mapSetZoomed = true;

			// adjust the zoom
			// this call includes the redraw of the main canvas
			zoomHandler.adjustZoom(selectedMap, newTotalY, newChromoHeight, distFromBottom);

			//update zoom control position
			Strudel.winMain.fatController.updateAllZoomControls();

			try
			{
				Thread.sleep(millis / totalFrames);
			}
			catch (InterruptedException e)
			{
			}
		}


		//if we have not reached the max zoom factor with this we need to do one more zoom adjust
		//explicitly here to make sure we have all the final intended values and have not fallen short
		//of these due to rounding errors etc.
		selectedSet.zoomFactor = finalZoomFactor;
		zoomHandler.adjustZoom(selectedMap, finalTotalY, finalChromoHeight,	 Math.round(finalChromoHeight/2.0f));

		//update overviews
		Strudel.winMain.fatController.updateOverviewCanvases();

		//update zoom control position
		Strudel.winMain.fatController.updateAllZoomControls();

		//set this flag to update the position lookup arrays for mouseover
		selectedSet.mapSetZoomed = true;

		//enable drawing of markers providing we have zoomed in, not out
		if (selectedSet.zoomFactor > 1)
			selectedSet.thresholdAllMarkerPainting = selectedSet.zoomFactor;

		//turn drawing of map index back on
		selectedMap.drawChromoIndex = true;

		zoomHandler.isClickZoomRequest = false;

		//repaint
		// TODO: AA check
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				Strudel.winMain.mainCanvas.updateCanvas(true);
			}
		});

		done = true;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
