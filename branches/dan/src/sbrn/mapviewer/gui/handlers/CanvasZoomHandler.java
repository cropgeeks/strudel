package sbrn.mapviewer.gui.handlers;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.animators.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;

public class CanvasZoomHandler
{

	// =====================================vars===================================

	MainCanvas mainCanvas;

	// frame rate
	int fps = 25;

	//animation times in milliseconds
	int clickZoomMillis = 100;
	int panZoomMillis = 300;

	//this boolean is required because we need to check in the stateChanged method of the ZoomControlPanel class whether
	//or not it was invoked manually or indirectly because we programmatically changed the value of the zoom slider
	public boolean isClickZoomRequest = false;
	public boolean isPanZoomRequest = false;

	// =====================================curve'tors===================================

	public CanvasZoomHandler(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
	}

	// =====================================methods===================================

	// gets invoked when the zoom is adjusted by using the sliders
	// adjusts the zoom factor and checks whether we need to display markers and labels
	public void processContinuousZoomRequest(float newZoomFactor, GMapSet selectedSet)
	{
		// update the genome centerpoint to the new percentage and update the scroller position
		selectedSet.zoomFactor = newZoomFactor;

		// don't let the zoom factor fall below zero
		if (newZoomFactor < 1)
			newZoomFactor = 1;

		//remember the previous totalY of the mapset, then recompute the map sizes with the new zoom factor
		int oldTotalY = selectedSet.totalY;
		selectedSet.calculateMapSizes();

		// the new centerpoint needs to be worked out in relation to the current one
		float proportion = selectedSet.centerPoint / (float) oldTotalY;
		float newCenterPoint = selectedSet.totalY * proportion;
		selectedSet.centerPoint = Math.round(newCenterPoint);

//		System.out.println("proportion = " + proportion);
//		System.out.println("selectedSet.totalY = " + selectedSet.totalY);
//		System.out.println("selectedSet.centerPoint = " + selectedSet.centerPoint);

		//update the position lookup arrays for mouseover
		Strudel.winMain.fatController.initialisePositionArrays();

		//update zoom control position
		Strudel.winMain.fatController.updateAllZoomControls();

		Strudel.winMain.mainCanvas.updateCanvas(true);
	}


	// -----------------------------------------------------------------------------------------------------------------------------------

	//zooms in to a region determined by user by drawing a rectangle around it
	public synchronized void processPanZoomRequest(GChromoMap selectedMap, int mousePressedY, int mouseReleasedY,boolean animate)
	{
		// animate this by zooming in gradually
		// figure out the genome it belongs to and increase that genome's zoom factor so that we can
		// just fit the chromosome on screen the next time it is painted
		int selectedYDist = mouseReleasedY - mousePressedY;
		if (selectedYDist > 0)
		{
			float finalScalingFactor = mainCanvas.getHeight() / (float) selectedYDist;
			PanZoomAnimator panZoomAnimator = new PanZoomAnimator(fps, panZoomMillis, finalScalingFactor, selectedMap, mainCanvas, mousePressedY, mouseReleasedY, this, animate);
			panZoomAnimator.start();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// zooms in by a fixed amount on a chromosome the user clicked on (to fill screen with chromosome)
	public void processClickZoomRequest(GChromoMap selectedMap)
	{

		// figure out the genome it belongs to and increase that genome's zoom factor so that we can
		// just fit the chromosome on screen the next time it is painted
		GMapSet selectedSet = selectedMap.owningSet;

		// animate this by zooming in gradually
		//this is the zoom factor we want to get to
		float finalZoomFactor = mainCanvas.getHeight() / selectedMap.initialHeight;

		//make sure this does not exceed the max zoom factor
		if(finalZoomFactor > selectedSet.maxZoomFactor)
			finalZoomFactor = selectedSet.maxZoomFactor;

		// work out the chromo height and total genome height for when the new zoom factor will have been applied
		int finalChromoHeight = (int) (selectedMap.initialHeight * finalZoomFactor);
		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = selectedSet.chromoSpacing * (selectedSet.numMaps - 1);

		// the total vertical extent of the genome at startup, excluding top and bottom spacers
		int totalY = (selectedSet.numMaps * selectedMap.initialHeight) + ((selectedSet.numMaps - 1) * selectedSet.chromoSpacing);

		// the new total Y extent of the genome in pixels for after the animation
		int finalTotalY = (int) (((totalY - combinedSpacers) * finalZoomFactor) + combinedSpacers);

		ClickZoomAnimator clickZoomAnimator = new ClickZoomAnimator(fps, clickZoomMillis, selectedMap,
						mainCanvas, finalZoomFactor, finalTotalY, finalChromoHeight, this);
		clickZoomAnimator.start();
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// zooms out to restore original zoom factor of 1
	public  void processZoomResetRequest(GMapSet selectedSet)
	{
		//this is the final zoom factor we want to have here
		selectedSet.zoomFactor = 1;

		//need to now update the map sizes
		selectedSet.calculateMapSizes();

		//and reset the centerpoint of the mapset
		selectedSet.centerPoint = Math.round(selectedSet.totalY / 2.0f);

		//update overviews
		Strudel.winMain.fatController.updateOverviewCanvases();

		//update zoom control position
		Strudel.winMain.fatController.updateAllZoomControls();

		//update the arrays with the position data
		Strudel.winMain.fatController.initialisePositionArrays();
		Strudel.winMain.mainCanvas.zoomHandler.isClickZoomRequest = false;

		//now repaint
		Strudel.winMain.mainCanvas.updateCanvas(true);

	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//used for adjusting zoom to a particular location and zoom level
	public void adjustZoom(float newZoomFactor, GChromoMap selectedMap, int distFromBottom)
	{
//		System.out.println("ADJUST ZOOM");
		
		GMapSet selectedSet = selectedMap.owningSet;

		//make sure the new zoom factor does not exceed the max allowed
		if(newZoomFactor > selectedSet.maxZoomFactor)
			newZoomFactor = selectedSet.maxZoomFactor;
		//set the new zoom factor
		selectedSet.zoomFactor = newZoomFactor;

		//need to now update the map sizes and the totalY of the mapset
		selectedSet.calculateMapSizes();

		// now we need to work out the percent offset from the top of the center of the chromo that
		// we want to zoom in on in the zoomed genome

		// the distance from the top of the genome to the end of the selected chromo, in pixels
		int spaceToEndOfMapInPixels = Utils.calcSpaceAboveGMap(selectedMap) + selectedMap.currentHeight;

		// the new centerpoint should be a proportion of the total genome height and is defined as
		// the sum of all chromosome heights and the spacer heights minus either half the height of a chromo
		// (for click zoom requests) or a calculated offset (for pan zoom requests)
		float newCenterPoint = spaceToEndOfMapInPixels - distFromBottom;
		
//		System.out.println("selectedMap.currentHeight = " + selectedMap.currentHeight);
//		System.out.println("spaceToEndOfMapInPixels = " + spaceToEndOfMapInPixels);
//		System.out.println("selectedSet.centerPoint = " + newCenterPoint);

		// update the genome centerpoint to the new percentage and update the scroller position
		selectedSet.centerPoint = Math.round(newCenterPoint);

		//update overviews
		Strudel.winMain.fatController.updateOverviewCanvases();

		//update zoom control position
		Strudel.winMain.fatController.updateAllZoomControls();

		//now update the arrays with the position data
		Strudel.winMain.fatController.initialisePositionArrays();

		// repaint the canvas
		mainCanvas.updateCanvas(true);
	}


	// -----------------------------------------------------------------------------------------------------------------------------------

	//zoom into a range on a chromosome which is defined by biological feature positions (rather than pixel values)
	public void zoomIntoRange(GChromoMap gChromoMap, float intervalStart, float intervalEnd, boolean animate)
	{
		//the map that pertains to the gChromoMap object
		ChromoMap chromoMap = gChromoMap.chromoMap;

		//need to know where to zoom into first
		int relativeTopY = (int) Math.floor((gChromoMap.currentHeight / chromoMap.getStop()) * intervalStart);
		int relativeBottomY = (int) Math.ceil((gChromoMap.currentHeight / chromoMap.getStop()) * intervalEnd);

		//this buffer increases the size of the visible interval slightly so the bounds don't coincide with the canvas bounds
		int buffer = 4;
		int topY = relativeTopY + gChromoMap.y - buffer;
		int bottomY = relativeBottomY + gChromoMap.y + buffer;
		Strudel.winMain.mainCanvas.zoomHandler.processPanZoomRequest(gChromoMap, topY,
						bottomY, animate);
	}


	// -----------------------------------------------------------------------------------------------------------------------------------

}// end class