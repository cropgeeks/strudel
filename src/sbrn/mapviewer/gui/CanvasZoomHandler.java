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
	
	public void processPanZoomRequest(GChromoMap selectedMap, int mousePressedY, int mouseReleasedY)
	{
		System.out.println("===========================");
		System.out.println("processPanZoomRequest -- zooming in");
		if (selectedMap != null)
			System.out.println("selected Map is " + selectedMap.name);
		System.out.println("y coords selected = " + mousePressedY + " ," + mouseReleasedY);
		
		if (selectedMap != null)
		{
			// figure out the genome it belongs to and increase that genome's zoom factor so that we can
			// just fit the chromosome on screen the next time it is painted
			GMapSet selectedSet = selectedMap.owningSet;
			
			int selectedYDist = mouseReleasedY - mousePressedY;
			float scalingFactor = mainCanvas.getHeight() / (float)selectedYDist;
			selectedSet.zoomFactor  *= scalingFactor;
			
			
			// this is the combined height of all spacers -- does not change with the zoom factor
			int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
			// work out the chromo height and total genome height for when the new zoom factor will have been applied
			int newChromoHeight = (int) (selectedSet.chromoHeight * selectedSet.zoomFactor);
			int newTotalY = (int) (((selectedSet.totalY - combinedSpacers) * selectedSet.zoomFactor) + combinedSpacers);
			
			// now we need to work out the percent offset from the top of the center of the chromo that
			// we want to zoom in on in the zoomed genome
			// the index of the selected chromo in the genome, starting at 1 rather than zero (for multiplication purposes below)
			int chromoIndex = selectedSet.gMaps.indexOf(selectedMap) + 1;
			// the distance from the top of the genome to the end of the selected chromo, in pixels
			int chromoOffset = chromoIndex * newChromoHeight;
			// the combined distance of all spacers between the top of the genome and our selected chromo
			int spacingOffset = chromoIndex * mainCanvas.chromoSpacing - mainCanvas.chromoSpacing;
			
			// the distance from the top of the chromosome to the mousePressedY location, in percent of the chromosome height
//			double percentDistanceFromTop = ((mousePressedY - selectedMap.boundingRectangle.getY()) / selectedMap.height) * 100;
			float distFromTop = (float) (mousePressedY - selectedMap.boundingRectangle.getY() + (mouseReleasedY - mousePressedY)/2);
			float distFromBottom = newChromoHeight - (distFromTop* selectedSet.zoomFactor);
			
			// the new centerpoint should be a proportion of the total genome height and is defined as
			// the sum of all chromosome heights and the spacer heights minus the distance from the bottom of
			//the chromosome to the new centerpoint
			float newCenterPoint = ((chromoOffset + spacingOffset - distFromBottom) / (float)newTotalY) * 100;
			
			System.out.println("selectedSet.zoomFactor = " + selectedSet.zoomFactor);
			System.out.println("spacingOffset = " + spacingOffset);
			System.out.println("chromoOffset = " + chromoOffset);
			System.out.println("newTotalY = " + newTotalY);
			System.out.println("newCenterPoint = " + newCenterPoint);
			System.out.println("selectedYDist = " + selectedYDist);
			System.out.println("scalingFactor = " + scalingFactor);
			System.out.println("distFromBottom = " + distFromBottom);			
			
			// update the genome centerpoint to the new percentage and update the scroller position
			selectedSet.centerPoint = newCenterPoint;
			selectedSet.scroller.setValue(Math.round(newCenterPoint));
			
			// check whether we need to display markers and labels
			if (selectedMap.isShowingOnCanvas)
			{
				mainCanvas.checkMarkerPaintingThresholds(selectedSet);
			}
			
			// repaint the canvas
			mainCanvas.repaint();
			
			// update overviews
			mainCanvas.winMain.fatController.updateOverviewCanvases();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// gets invoked when the zoom is adjusted by using the sliders or the drag-and-zoom functionality
	// adjusts the zoom factor and checks whether we need to now display markers and labels
	public void processContinuousZoomRequest(float zoomFactor, int genomeIndex)
	{
		GMapSet selectedSet = mainCanvas.gMapSetList.get(genomeIndex);
		selectedSet.zoomFactor = zoomFactor;
		
		// check whether we need to display markers and labels
		mainCanvas.checkMarkerPaintingThresholds(selectedSet);

		mainCanvas.repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// zooms in by a fixed amount on a chromosome the user clicked on
	public void processClickZoomRequest(int x, int y)
	{
		GChromoMap selectedMap = Utils.getSelectedMap(mainCanvas.gMapSetList, x, y);
		
		// if the click has hit a chromosome
		if (selectedMap != null)
		{
			// figure out the genome it belongs to and increase that genome's zoom factor so that we can
			// just fit the chromosome on screen the next time it is painted
			GMapSet selectedSet = selectedMap.owningSet;
			selectedSet.zoomFactor = mainCanvas.maxChromos;
			
			// this is the combined height of all spacers -- does not change with the zoom factor
			int combinedSpacers = mainCanvas.chromoSpacing * (selectedSet.numMaps - 1);
			// work out the chromo height and total genome height for when the new zoom factor will have been applied
			int newChromoHeight = (int) (selectedSet.chromoHeight * selectedSet.zoomFactor);
			int newTotalY = (int) (((selectedSet.totalY - combinedSpacers) * selectedSet.zoomFactor) + combinedSpacers);
			
			// now we need to work out the percent offset from the top of the center of the chromo that
			// we want to zoom in on in the zoomed genome
			// the index of the selected chromo in the genome, starting at 1 rather than zero (for multiplication purposes below)
			int chromoIndex = selectedSet.gMaps.indexOf(selectedMap) + 1;
			// the distance from the top of the genome to the end of the selected chromo, in pixels
			int chromoOffset = chromoIndex * newChromoHeight;
			// the combined distance of all spacers between the top of the genome and our selected chromo
			int spacingOffset = chromoIndex * mainCanvas.chromoSpacing - mainCanvas.chromoSpacing;
			float halfChromoHeight = newChromoHeight / 2;
			// the new centerpoint should be a proportion of the total genome height and is defined as
			// the sum of all chromosome heights and the spacer heights minus half the height of a chromo
			float newCenterPoint = ((chromoOffset + spacingOffset - halfChromoHeight) / newTotalY) * 100;
			// update the genome centerpoint to the new percentage and update the scroller position
			selectedSet.centerPoint = newCenterPoint;
			selectedSet.scroller.setValue(Math.round(newCenterPoint));
			
			// check whether we need to display markers and labels
			if (selectedMap.isShowingOnCanvas)
			{
				mainCanvas.checkMarkerPaintingThresholds(selectedSet);
			}
			
			// repaint the canvas
			mainCanvas.repaint();
			
			// update overviews
			mainCanvas.winMain.fatController.updateOverviewCanvases();
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
}// end class
