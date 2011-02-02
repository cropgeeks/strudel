package sbrn.mapviewer.gui.handlers;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class MouseHandler implements MouseInputListener, MouseWheelListener
{
	// =================================================vars========================================

	WinMain winMain;
	int mouseDragPosY = 0;
	int mouseDragPosX = 0;
	int mousePressedX = -1;
	int mousePressedY = -1;
	public MouseOverHandler mouseOverHandler;

	long timeOfMouseDown = 0;

	private final boolean isOSX = SystemUtils.isMacOS();

	int scrollIncrement = 100;

	int lastMouseDragYPos = -1;


	// ===============================================curve'tors===========================================

	public MouseHandler(WinMain winMain)
	{
		this.winMain = winMain;
		mouseOverHandler = new MouseOverHandler(winMain);
	}

	// =================================================methods=======================================

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// used for selecting chromosomes for display of links and for zooming
	public void mouseClicked(MouseEvent e)
	{
		//place the focus on this window so we can listen to keyboard events too
		winMain.mainCanvas.requestFocusInWindow();

		//double click means zoom into single chromo so it fills the screen
		if (e.getClickCount() == 2)
		{
			GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataSet.gMapSets, e.getX(),
							e.getY());

			if (selectedMap != null)
				winMain.mainCanvas.zoomHandler.processClickZoomRequest(selectedMap);
			return;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mouseEntered(MouseEvent e)
	{

	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mouseExited(MouseEvent e)
	{
		Strudel.winMain.fatController.clearMouseOverLabels();
		HintPanel.clearLabel();
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mousePressed(MouseEvent e)
	{
		mousePressedX = e.getX();
		mousePressedY = e.getY();

		lastMouseDragYPos = e.getY();
		timeOfMouseDown = System.currentTimeMillis();

		GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataSet.gMapSets, e.getX(), e.getY());
		
		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(selectedMap != null)
			{
				processPopupTrigger(selectedMap, e);
			}
			return;
		}
		else if (!isOSX && SwingUtilities.isRightMouseButton(e))
			return;

		//simple click on a target genome chromosome means display all links between this and all the reference chromos
		if (!isMetaClick(e) && !e.isShiftDown())
		{
			Strudel.winMain.fatController.isCtrlClickSelection = false;

			//if we have clicked on a map, display links between this map and all others
			if(selectedMap != null)
			{
				winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(selectedMap);
			}
			//otherwise -- if we clicked on the background -- clear all links displayed
			else
			{
				Strudel.winMain.fatController.clearMapHighlighting();
				//reset selected maps
				Strudel.winMain.fatController.selectedMaps.clear();
			}
		}
		//CTRL+click on a chromosome means display all links between this and all other clicked chromos
		else if (isMetaClick(e) && !e.isShiftDown())
		{
			//if we have clicked on a map, display links between this map and all others
			if(selectedMap != null)
			{
				Strudel.winMain.fatController.isCtrlClickSelection = true;
				//also reset the wholeGenomeSelected flag
				selectedMap.owningSet.wholeMapsetIsSelected  = false;
				winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(selectedMap);
			}
		}
		else if(e.isShiftDown() && isMetaClick(e))
		{
			//if we had a feature selection rectangle on screen previously we need to now clear it
			Strudel.winMain.fatController.clearSelectionRectangle();
		}

		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	public void mouseReleased(MouseEvent e)
	{
		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataSet.gMapSets, e.getX(), e.getY());
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(selectedMap != null)
			{
				processPopupTrigger(selectedMap, e);
			}
			return;
		}
		else if (SwingUtilities.isRightMouseButton(e))
			return;

		//this is when we do pan-and-zoom and we release the mouse at the end of the panning
		// in that case we want to trigger a zoom event which zooms into the selected region
		if (e.isShiftDown() && !isMetaClick(e))
		{
			//request zooming for the selected map with the given set of coordinates
			//get the selected set first
			int gMapSetIndex = Utils.getSelectedSetIndex(e);
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, mousePressedY);
			//if this has not selected a map we need to see whether the map to be selected is under the point where the mouse button was released
			//this can happen if we start drawing the selection rectangle above a chromosome
			if(selectedMap == null)
				selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, e.getY());
			winMain.mainCanvas.zoomHandler.processPanZoomRequest(selectedMap, mousePressedY, e.getY(), true);
		}

		//clear the zoom selection rectangle
		winMain.mainCanvas.drawZoomSelectionRectangle = false;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// used for zooming and scrolling
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();

		// figure out which genome this event pertains to (i.e. which section of the canvas on x are we in)
		int index = Utils.getSelectedSetIndex(e);
		GMapSet gMapSet = Strudel.winMain.dataSet.gMapSets.get(index);

		// mouse is getting dragged  with SHIFT or CTRL-SHIFT down -- draw a rectangle for  selection (zooming/range selection)
		
		//this is what we do for drawing a selection rectangle
		if(e.isShiftDown() && isMetaClick(e))
		{	
			//the chromosome -- if any - this event pertains to (i.e. where on the canvas on y are we)
			GChromoMap mouseOverMap = Utils.getSelectedMap(Strudel.winMain.dataSet.gMapSets, (int)(gMapSet.xPosition), y);

			//this is what we need to do the first time we select a map in this current round
			//if nothing is selected we assume we want to select the map we are current mousing over
			if(Strudel.winMain.fatController.selectedMap == null)
				Strudel.winMain.fatController.selectedMap = mouseOverMap;

			//if a map is selected
			if(Strudel.winMain.fatController.selectedMap != null)
			{
				// +ve y
				if (y > mousePressedY)
				{
					Strudel.winMain.fatController.selectedMap.selectionRectTopY = mousePressedY - Strudel.winMain.fatController.selectedMap.boundingRectangle.y;
					Strudel.winMain.fatController.selectedMap.selectionRectBottomY = y - Strudel.winMain.fatController.selectedMap.boundingRectangle.y;
					Strudel.winMain.fatController.selectedMap.chromoHeightOnSelection = Strudel.winMain.fatController.selectedMap.boundingRectangle.height;
				}
				// -ve y
				else if (y <= mousePressedY)
				{
					Strudel.winMain.fatController.selectedMap.selectionRectTopY = y - Strudel.winMain.fatController.selectedMap.boundingRectangle.y;
					Strudel.winMain.fatController.selectedMap.selectionRectBottomY = mousePressedY - Strudel.winMain.fatController.selectedMap.boundingRectangle.y;
					Strudel.winMain.fatController.selectedMap.chromoHeightOnSelection = Strudel.winMain.fatController.selectedMap.boundingRectangle.height;
				}				

				//need to check here whether we are dragging to select a second map accidentally
				//we can only ever have one of these rectangles at any one time
				if(Strudel.winMain.fatController.selectedMap != null && mouseOverMap != null && Strudel.winMain.fatController.selectedMap != mouseOverMap)
					return;

				//let the MAP draw this rectangle -- we want to have this rect associated with the map and redrawn when the map is rendered
				Strudel.winMain.fatController.selectedMap.drawFeatureSelectionRectangle = true;
				
				//draw links as we select
				float intervalStart = Utils.pixelsOnChromoToFeaturePositionOnChromomap(Strudel.winMain.fatController.selectedMap, (int)Strudel.winMain.fatController.selectedMap.selectionRectTopY);
				float intervalEnd = Utils.pixelsOnChromoToFeaturePositionOnChromomap(Strudel.winMain.fatController.selectedMap, (int)Strudel.winMain.fatController.selectedMap.selectionRectBottomY);
				Strudel.winMain.mainCanvas.drawLinksOriginatingInRange = true;
				Vector<Feature> selectedFeatures = Utils.getFeaturesByInterval(Strudel.winMain.fatController.selectedMap.chromoMap, intervalStart, intervalEnd);
				Strudel.winMain.mainCanvas.linkDisplayManager.featuresSelectedByRange = selectedFeatures;
				
				//redraw
				winMain.mainCanvas.updateCanvas(true);
				winMain.mainCanvas.requestFocusInWindow();
			}
		}

		//this is what we do for drawing a pan zoom rectangle
		if(e.isShiftDown() && !isMetaClick(e))
		{
			// +ve x
			if (x >= mousePressedX)
			{
				winMain.mainCanvas.selectionRect.x = mousePressedX;
				winMain.mainCanvas.selectionRect.width = x - mousePressedX;
			}
			// -ve x
			else if (x < mousePressedX)
			{
				winMain.mainCanvas.selectionRect.x = x;
				winMain.mainCanvas.selectionRect.width = mousePressedX - x;
			}
			// +ve y
			if (y >= mousePressedY)
			{
				winMain.mainCanvas.selectionRect.y = mousePressedY;
				winMain.mainCanvas.selectionRect.height = y - mousePressedY;
			}
			// -ve y
			else if (y < mousePressedY)
			{
				winMain.mainCanvas.selectionRect.y = y;
				winMain.mainCanvas.selectionRect.height = mousePressedY - y;
			}
			//let the MAIN CANVAS draw this rectangle -- we only ever have one of these at a time and we do not need to store
			//its coordinates for any length of time
			winMain.mainCanvas.drawZoomSelectionRectangle = true;
			winMain.mainCanvas.updateCanvas(false);
		}

		// update the current drag positions
		mouseDragPosX = e.getX();
		mouseDragPosY = e.getY();
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mouseMoved(MouseEvent e)
	{
		mouseOverHandler.detectMouseOver(e.getX(), e.getY());
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// mouse scrolling of canvas
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// figure out which genome we are moving
		int index = Utils.getSelectedSetIndex(e);
		GMapSet selectedSet = Strudel.winMain.dataSet.gMapSets.get(index);

		// work out in which direction we have moved the mouse
		int notches = e.getWheelRotation();

		//if Ctrl is down we zoom
		if(isMetaClick(e))
		{
			float newZoomFactor = -1;
			float increment = selectedSet.maxZoomFactor / 100;

			//zooming out
			if (notches < 0)
				newZoomFactor = selectedSet.zoomFactor - increment;
			//zooming in
			else
				newZoomFactor = selectedSet.zoomFactor + increment;

			//if the new zoom factor is negative, do nothing
			if(newZoomFactor < 0)
				return;

			Strudel.winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(newZoomFactor, 0, selectedSet, true);
			Strudel.winMain.fatController.updateAllZoomControls();
		}
		//otherwise we scroll
		else
		{
			//check whether all maps in the mapset are visible -- if yes, do not scroll
			if(selectedSet.getVisibleMaps().size() < selectedSet.gMaps.size())
			{
				//scrolling up
				if (notches < 0)
				{
					Strudel.winMain.mainCanvas.scroll(true, selectedSet, scrollIncrement);
				}
				//scrolling down
				else
				{
					Strudel.winMain.mainCanvas.scroll(false, selectedSet, scrollIncrement);
				}
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void processPopupTrigger(GChromoMap selectedMap, MouseEvent e)
	{
		//check whether the 	showAllLabelsItem should be selected or not for this map
		Strudel.winMain.chromoContextPopupMenu.showAllLabelsItem.setSelected(selectedMap.alwaysShowAllLabels);

		//clear any feature labels that might be hanging around here
		selectedMap.drawMouseOverFeatures = false;
		Strudel.winMain.mainCanvas.updateCanvas(true);

		winMain.fatController.invertMap = selectedMap;
		winMain.fatController.selectedMap = selectedMap;

		//if we have got here because we had first drawn a selection rectangle for including all features in a range for inclusion
		//in the results table, then we want the context menu to only have the option for this, and not inverting chromos etc
		if(selectedMap.drawFeatureSelectionRectangle)
		{
			winMain.chromoContextPopupMenu.showAnnotationItem.setVisible(true);
			winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(false);
			winMain.chromoContextPopupMenu.fitChromoMenuItem.setVisible(false);
		}
		else
		{
			winMain.chromoContextPopupMenu.showAnnotationItem.setVisible(false);
			winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(true);
			winMain.chromoContextPopupMenu.fitChromoMenuItem.setVisible(true);
		}
		winMain.chromoContextPopupMenu.show(winMain.mainCanvas, e.getX(), e.getY());
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
