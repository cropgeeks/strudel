package sbrn.mapviewer.gui.handlers;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
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
	int clickZoomMillis = 200;
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
	public void processContinuousZoomRequest(float newZoomFactor, float multiplier, GMapSet selectedSet, boolean isSliderRequest)
	{
		//make sure antialiasing is off
		mainCanvas.antiAlias = false;

		//for a request from the sliders we need to work out the multiplier but not the zoom factor
		if(isSliderRequest)
		{
			multiplier = newZoomFactor/selectedSet.zoomFactor;
		}
		//for all the other requests it's the opposite
		else
		{
			newZoomFactor = selectedSet.zoomFactor * multiplier;
		}

		// don't let the zoom factor fall below zero
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

		//update the position lookup arrays for mouseover
		MapViewer.winMain.fatController.initialisePositionArrays();

		//update zoom control position
		MapViewer.winMain.fatController.updateAllZoomControls();

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
	public ClickZoomAnimator processClickZoomRequest(GChromoMap selectedMap)
	{
	
		// figure out the genome it belongs to and increase that genome's zoom factor so that we can
		// just fit the chromosome on screen the next time it is painted
		GMapSet selectedSet = selectedMap.owningSet;

		// animate this by zooming in gradually
		//this is the zoom factor we want to get to
		float finalZoomFactor = mainCanvas.getHeight() / mainCanvas.initialChromoHeight;
		
		//make sure this does not exceed the max zoom factor
		if(finalZoomFactor > Constants.MAX_ZOOM_FACTOR)
			finalZoomFactor = Constants.MAX_ZOOM_FACTOR;
		
		// work out the chromo height and total genome height for when the new zoom factor will have been applied
		int finalChromoHeight = (int) (mainCanvas.initialChromoHeight * finalZoomFactor);
		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
		
		// the total vertical extent of the genome at startup, excluding top and bottom spacers
		int totalY = (selectedSet.numMaps * mainCanvas.initialChromoHeight) + ((selectedSet.numMaps - 1) * mainCanvas.chromoSpacing);
		
		// the new total Y extent of the genome in pixels for after the animation 
		int finalTotalY = (int) (((totalY - combinedSpacers) * finalZoomFactor) + combinedSpacers);

		ClickZoomAnimator clickZoomAnimator = new ClickZoomAnimator(fps, clickZoomMillis, selectedMap,
						mainCanvas, finalZoomFactor, finalTotalY, finalChromoHeight, this);
		clickZoomAnimator.start();
		
		return clickZoomAnimator;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// zooms out to restore original zoom factor of 1
	public  void processZoomResetRequest(GMapSet selectedSet)
	{
		//this is the final zoom factor we want to have here
		selectedSet.zoomFactor = 1;		
		selectedSet.chromoHeight = MapViewer.winMain.mainCanvas.initialChromoHeight;		

		//update overviews
		MapViewer.winMain.fatController.updateOverviewCanvases();

		//update zoom control position
		MapViewer.winMain.fatController.updateAllZoomControls();

		//update the arrays with the position data
		MapViewer.winMain.fatController.initialisePositionArrays();		
		MapViewer.winMain.mainCanvas.zoomHandler.isClickZoomRequest = false;
		
		//now repaint
		MapViewer.winMain.mainCanvas.updateCanvas(true);

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

		// check whether we need to display markers and labels
		if (selectedMap.isShowingOnCanvas)
		{
			mainCanvas.checkMarkerPaintingThresholds(selectedSet);
		}

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
		int relativeTopY = (int) Math.floor((gChromoMap.height / chromoMap.getStop()) * intervalStart);
		int relativeBottomY = (int) Math.ceil((gChromoMap.height / chromoMap.getStop()) * intervalEnd);
		
		//this buffer increases the size of the visible interval slightly so the bounds don't coincide with the canvas bounds
		int buffer = 4;
		int topY = relativeTopY + gChromoMap.y - buffer;
		int bottomY = relativeBottomY + gChromoMap.y + buffer;
		MapViewer.winMain.mainCanvas.zoomHandler.processPanZoomRequest(gChromoMap, topY,
						bottomY, animate);
	}
	

	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// zoom into a range on a chromosome which is defined by biological feature positions (rather than pixel values)
	public void zoomToPixelRange(GChromoMap selectedMap, int top, int bottom)
	{
		int selectedYDist = bottom - top;		
		float finalScalingFactor = mainCanvas.getHeight() / (float) selectedYDist;		
		GMapSet selectedSet = selectedMap.owningSet;
		
		// this is the combined height of all spacers -- does not change with the zoom factor
		int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
		
		// these are the values we want for the last iteration
		float finalZoomFactor = selectedSet.zoomFactor * finalScalingFactor;
		float finalChromoHeight = (int) (selectedSet.chromoHeight * finalScalingFactor);
		// the distance from the top of the chromosome to the mousePressedY location, in pixels
		float initialDistFromTop = (float) (top - selectedMap.boundingRectangle.getY() + (selectedYDist / 2));
		float initialDistFromTopProportion = initialDistFromTop / (float) selectedMap.boundingRectangle.getHeight();
		
		// the new total Y extent of the genome in pixels
		int finalTotalY = (int) (((selectedSet.totalY - combinedSpacers) * finalScalingFactor) + combinedSpacers);		
		selectedSet.zoomFactor = finalZoomFactor;
		adjustZoom(selectedMap, finalTotalY, (int) finalChromoHeight, (int) (finalChromoHeight - (initialDistFromTopProportion * finalChromoHeight)));
		
		//now update the arrays with the position data
		MapViewer.winMain.fatController.initialisePositionArrays();
		//update zoom control position
		MapViewer.winMain.fatController.updateAllZoomControls();
				
		//turn antialiasing on and repaint
		mainCanvas.antiAlias = true;
		mainCanvas.updateCanvas(true);
	}
	

	// -----------------------------------------------------------------------------------------------------------------------------------
	
}// end class
