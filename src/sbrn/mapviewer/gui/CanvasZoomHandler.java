package sbrn.mapviewer.gui;

import sbrn.mapviewer.gui.entities.GChromoMap;
import sbrn.mapviewer.gui.entities.GMapSet;

public class CanvasZoomHandler
{

	// =====================================vars===================================

	MainCanvas mainCanvas;

	// =====================================c'tors===================================

	public CanvasZoomHandler(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
	}

	// =====================================methods===================================

	// gets invoked when the zoom is adjusted by using the sliders or the drag-and-zoom functionality
	// adjusts the zoom factor and checks whether we need to display markers and labels
	public void processContinuousZoomRequest(float newZoomFactor, float multiplier, int genomeIndex)
	{
		GMapSet selectedSet = mainCanvas.gMapSetList.get(genomeIndex);

		newZoomFactor = selectedSet.zoomFactor * multiplier;
		// don't let this fall below zero
		if (newZoomFactor < 1)
			newZoomFactor = 1;

		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);

		// the new total Y extent of the genome in pixels
		int newTotalY = (int) (((selectedSet.totalY - combinedSpacers) * multiplier) + combinedSpacers);

		// the new centerpoint needs to be worked out in relation to the current one
		float proportion = selectedSet.centerPoint / (float) selectedSet.totalY;
		float newCenterPoint = newTotalY * proportion;
		int newChromoHeight = Math.round(selectedSet.chromoHeight * multiplier);

		// update the genome centerpoint to the new percentage and update the scroller position
		selectedSet.zoomFactor = newZoomFactor;
		selectedSet.totalY = newTotalY;
		selectedSet.centerPoint = Math.round(newCenterPoint);
		selectedSet.chromoHeight = newChromoHeight;
		selectedSet.scroller.setValues(Math.round(newCenterPoint), 0, 0, newTotalY);

		// check whether we need to display markers and labels
		mainCanvas.checkMarkerPaintingThresholds(selectedSet);

		mainCanvas.repaint();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//zooms in to a region determined by user by drawing a rectangle around it
	public void processPanZoomRequest(GChromoMap selectedMap, int mousePressedY, int mouseReleasedY)
	{
		// figure out the genome it belongs to and increase that genome's zoom factor so that we can
		// just fit the chromosome on screen the next time it is painted
		int selectedYDist = mouseReleasedY - mousePressedY;
		float finalScalingFactor = mainCanvas.getHeight() / (float) selectedYDist;

		// animate this by zooming in gradually
		// frame rate
		int fps = 30;
		// the length of time we want the animation to last in milliseconds
		int millis = 1000;
		PanZoomAnimator panZoomAnimator = new PanZoomAnimator(fps, millis, finalScalingFactor, selectedMap, mainCanvas, mousePressedY, mouseReleasedY, this);
		panZoomAnimator.start();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// zooms in by a fixed amount on a chromosome the user clicked on (to fill screen with chromosome)
	public void processClickZoomRequest(GChromoMap selectedMap)
	{
		// figure out the genome it belongs to and increase that genome's zoom factor so that we can
		// just fit the chromosome on screen the next time it is painted
		GMapSet selectedSet = selectedMap.owningSet;

		// animate this by zooming in gradually
		// frame rate
		int fps = 30;
		// the length of time we want the animation to last in milliseconds
		int millis = 1000;

		float finalZoomFactor = mainCanvas.getHeight() / selectedSet.chromoHeight;
		// work out the chromo height and total genome height for when the new zoom factor will have been applied
		int finalChromoHeight = (int) (mainCanvas.initialChromoHeight * finalZoomFactor);
		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
		// the new total Y extent of the genome in pixels
		int finalTotalY = (int) (((selectedSet.totalY - combinedSpacers) * finalZoomFactor) + combinedSpacers);

		ClickZoomAnimator clickZoomAnimator = new ClickZoomAnimator(fps, millis, selectedMap,
						mainCanvas, finalZoomFactor, finalTotalY, finalChromoHeight, this);
		clickZoomAnimator.start();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//helper method used for adjusting zoom to a particular location and zoom level
	public void adjustZoom(GChromoMap selectedMap, int newTotalY, int newChromoHeight, int distFromBottom)
	{
		GMapSet selectedSet = selectedMap.owningSet;

		selectedSet.totalY = newTotalY;

		// now we need to work out the percent offset from the top of the center of the chromo that
		// we want to zoom in on in the zoomed genome
		// the index of the selected chromo in the genome, starting at 1 rather than zero (for multiplication purposes below)
		int chromoIndex = selectedSet.gMaps.indexOf(selectedMap) + 1;

		// the distance from the top of the genome to the end of the selected chromo, in pixels
		int chromoOffset = chromoIndex * newChromoHeight;

		// the combined distance of all spacers between the top of the genome and our selected chromo
		int spacingOffset = chromoIndex * mainCanvas.chromoSpacing - mainCanvas.chromoSpacing;

		// the new centerpoint should be a proportion of the total genome height and is defined as
		// the sum of all chromosome heights and the spacer heights minus either half the height of a chromo
		// (for click zoom requests) or a calculated offset (for pan zoom requests)
		float newCenterPoint = chromoOffset + spacingOffset - distFromBottom;

		// update the genome centerpoint to the new percentage and update the scroller position
		selectedSet.centerPoint = Math.round(newCenterPoint);
		selectedSet.chromoHeight = newChromoHeight;
		selectedSet.scroller.setValues(Math.round(newCenterPoint), 0, 0, newTotalY);

		// check whether we need to display markers and labels
		if (selectedMap.isShowingOnCanvas)
		{
			mainCanvas.checkMarkerPaintingThresholds(selectedSet);
		}

		// repaint the canvas
		mainCanvas.repaint();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

}// end class
