package sbrn.mapviewer.gui;

import sbrn.mapviewer.gui.entities.*;

public class PanZoomAnimator extends Thread
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

	// ============================================c'tor============================================

	public PanZoomAnimator(int fps, int millis, float finalScalingFactor, GChromoMap selectedMap,
					MainCanvas mainCanvas, int mousePressedY, int mouseReleasedY,
					CanvasZoomHandler zoomHandler)
	{
		this.fps = fps;
		this.millis = millis;
		this.finalScalingFactor = finalScalingFactor;
		this.selectedMap = selectedMap;
		this.mainCanvas = mainCanvas;
		this.mousePressedY = mousePressedY;
		this.mouseReleasedY = mouseReleasedY;
		this.zoomHandler = zoomHandler;
	}

	// ============================================methods============================================

	public void run()
	{
		//turn antialiasing off
		mainCanvas.antiAlias = false;

		float totalFrames = fps * (millis / 1000);

		// divide the difference between a scaling factor of 1 and the final scaling factor by the number of frames we want to use for this
		float increment = (finalScalingFactor - 1) / totalFrames;

		GMapSet selectedSet = selectedMap.owningSet;

		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);

		// these are the values we want for the last iteration
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

		// now loop for the number of total frames, zooming in by a bit each time
		for (int i = 0; i < totalFrames; i++)
		{
			// sleep for the amount of animation time divided by the fps value
			try
			{
				Thread.sleep((long) (millis / totalFrames));
			}
			catch (InterruptedException e)
			{
			}

			// work out the current scaling factor
			// next zoom factor divided by current zoom factor
			float currentScalingFactor = (selectedSet.zoomFactor + zoomFactorIncrement) / selectedSet.zoomFactor;

			// set the new zoom factor
			selectedSet.zoomFactor += zoomFactorIncrement;

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
			mainCanvas.winMain.fatController.updateZoomControls();
		}
		
		//one more zoom adjust  to be done explicitly here to make sure we have all the final intended values and have not fallen short
		//of these due to rounding errors etc.
		selectedSet.zoomFactor = finalZoomFactor;
		zoomHandler.adjustZoom(selectedMap, finalTotalY, (int)finalChromoHeight, (int)(finalChromoHeight - (initialDistFromTopProportion * finalChromoHeight)));

		//now update the arrays with the position data
		mainCanvas.winMain.fatController.initialisePositionArrays();

		//turn antialiasing on and repaint
		mainCanvas.antiAlias = true;
		mainCanvas.updateCanvas(true);

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
