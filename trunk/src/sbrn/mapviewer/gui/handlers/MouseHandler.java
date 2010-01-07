package sbrn.mapviewer.gui.handlers;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
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
	MouseOverHandler mouseOverHandler;

	long timeOfMouseDown = 0;

	private final boolean isOSX = SystemUtils.isMacOS();

	int scrollGenomeIncrement = 100;

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

		//mouse click with alt held down means zoom into single chromo so it fills the screen
		if (e.isAltDown())
		{
			GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataContainer.gMapSets, e.getX(),
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
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mousePressed(MouseEvent e)
	{
		mousePressedX = e.getX();
		mousePressedY = e.getY();
		lastMouseDragYPos = e.getY();
		timeOfMouseDown = System.currentTimeMillis();

		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(mouseOverHandler.selectedMap != null)
			{
				// get the selected set first
				GChromoMap selectedMap = Utils.getSelectedMap(winMain, Utils.getSelectedSetIndex(e), mousePressedY);

				//clear any feature labels that might be hanging around here
				selectedMap.drawMouseOverFeatures = false;
				Strudel.winMain.mainCanvas.updateCanvas(true);

				winMain.fatController.invertMap = selectedMap;
				winMain.fatController.selectionMap = selectedMap;
				winMain.chromoContextPopupMenu.show(winMain.mainCanvas, e.getX(), e.getY());
			}
			return;
		}
		else if (SwingUtilities.isRightMouseButton(e))
			return;

		//simple click on a target genome chromosome means display all links between this and all the reference chromos
		if (!isMetaClick(e) && !e.isAltDown() && !e.isShiftDown())
		{
			// first figure out which chromosome we are in
			GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataContainer.gMapSets, e.getX(), e.getY());
			//if we have clicked on a map, display links between this map and all others
			if(selectedMap != null)
			{
				Strudel.winMain.fatController.isCtrlClickSelection = false;
				winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY());
			}
			//otherwise -- if we clicked on the background -- clear all links displayed
			else
			{
				Strudel.winMain.fatController.isCtrlClickSelection = false;
				Strudel.winMain.fatController.clearMapOutlines();

				//reset selected maps
				Strudel.winMain.fatController.selectedMaps.clear();
			}
		}
		//CTRL+click on a chromosome means display all links between this and all other clicked chromos
		else if (isMetaClick(e) && !e.isShiftDown())
		{
			Strudel.winMain.fatController.isCtrlClickSelection = true;
			winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY());
		}

		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	public void mouseReleased(MouseEvent e)
	{
		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(mouseOverHandler.selectedMap != null)
			{
				// get the selected map first
				GChromoMap selectedMap = Utils.getSelectedMap(winMain, Utils.getSelectedSetIndex(e), mousePressedY);
				winMain.fatController.selectionMap = selectedMap;
				winMain.fatController.invertMap = selectedMap;

				//if we have got here because we had first drawn a selection rectangle for including all features in a range for inclusion
				//in the results table, then we want the context menu to only have the option for this, and not inverting chromos etc
				if(selectedMap.drawSelectionRect)
				{
					winMain.chromoContextPopupMenu.addAllFeaturesItem.setVisible(true);
					winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(false);
					winMain.chromoContextPopupMenu.fitChromoMenuItem.setVisible(false);
				}
				else
				{
					winMain.chromoContextPopupMenu.addAllFeaturesItem.setVisible(false);
					winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(true);
					winMain.chromoContextPopupMenu.fitChromoMenuItem.setVisible(true);
				}

				//show the context menu
				winMain.chromoContextPopupMenu.show(winMain.mainCanvas, e.getX(), e.getY());
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

		winMain.mainCanvas.drawSelectionRect = false;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// used for zooming and scrolling
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();

		// figure out which genome this event pertains to (i.e. which section of the canvas on x are we in)
		int index = Utils.getSelectedSetIndex(e);
		GMapSet gMapSet = Strudel.winMain.dataContainer.gMapSets.get(index);

		//the chromosome -- if any - this event pertains to (i.e. where on the canvas on y are we)
		GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.dataContainer.gMapSets, (int)(gMapSet.xPosition), y);

		//mouse is getting dragged without shift held down -- scroll the canvas up or down
		if (!e.isShiftDown())
		{
			//include a time delay before dragging so we can prevent accidental drags that were in fact intended to be mouse clicks
			long now = System.currentTimeMillis();
			if (now - timeOfMouseDown < 200)
				return;

			//this is the amount by which we drag the canvas at a time
			// a fixed amount seems to work best as it moves the canvas the same way across all zoom levels
			int distanceDragged = Math.abs(lastMouseDragYPos - y);

			// mouse is getting dragged up
			if (y < mouseDragPosY)
			{
				winMain.mainCanvas.moveGenomeViewPort(gMapSet, gMapSet.centerPoint + distanceDragged);
			}
			// mouse is getting dragged down
			if (y > mouseDragPosY)
			{
				winMain.mainCanvas.moveGenomeViewPort(gMapSet, gMapSet.centerPoint - distanceDragged);
			}

			lastMouseDragYPos = y;
		}


		// mouse is getting dragged  with SHIFT or CTRL-SHIFT down -- draw a rectangle for  selection (zooming/range selection)

		//this is what we do for drawing a selection rectangle
		if(e.isShiftDown() && isMetaClick(e))
		{
			if(selectedMap != null)
			{
				// +ve y
				if (y >= mousePressedY)
				{
					selectedMap.selectionRectTopY = mousePressedY - selectedMap.boundingRectangle.y;
					selectedMap.selectionRectBottomY = y - selectedMap.boundingRectangle.y;
					selectedMap.chromoHeightOnSelection = selectedMap.boundingRectangle.height;
				}
				// -ve y
				else if (y < mousePressedY)
				{
					selectedMap.selectionRectTopY = y - selectedMap.boundingRectangle.y;
					selectedMap.selectionRectBottomY = mousePressedY - selectedMap.boundingRectangle.y;
					selectedMap.chromoHeightOnSelection = selectedMap.boundingRectangle.height;
				}

				Strudel.winMain.fatController.selectionMap = selectedMap;
				//let the MAP draw this rectangle -- we want to have this rect associated with the map and redrawn when the map is rendered
				selectedMap.drawSelectionRect = true;

				//update the context menus according to what we intend to do with this selection rectangle
				//if we have an existing results set we want to add the features in the rectangle to this
				//otherwise we want to create a new results table
				if(Strudel.winMain.ffResultsPanel.getFFResultsTable().getModel().getRowCount() > 0)
					Strudel.winMain.chromoContextPopupMenu.addAllFeaturesItem.setText(Strudel.winMain.chromoContextPopupMenu.addAllFeaturesStr);
				else
					Strudel.winMain.chromoContextPopupMenu.addAllFeaturesItem.setText(Strudel.winMain.chromoContextPopupMenu.webInfoStr);

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
			winMain.mainCanvas.drawSelectionRect = true;
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
		GMapSet selectedSet = Strudel.winMain.dataContainer.gMapSets.get(index);

		//this moves the genome center point up and down
		int newCenterPoint = -1;

		//this boolean dictates whether we should actually move the viewport or not
		//it is set to false if moving the viewport would make all maps disappear from the canvas completely
		boolean moveViewport = false;

		// work out in which direction we have moved the mouse
		int notches = e.getWheelRotation();

		//scrolling up
		if (notches < 0)
		{
			newCenterPoint = selectedSet.centerPoint  - scrollGenomeIncrement;
			//don't let the centerpoint become negative or the first chromosome will disappear off the canvas
			if(newCenterPoint > 0)
				moveViewport = true;
		}
		//scrolling down
		else
		{
			newCenterPoint = selectedSet.centerPoint  + scrollGenomeIncrement;
			//this buffer is a hack that allows us to keep the last chromosome in the set visible on the canvas at all times
			//if we don't apply this then it can disappear altogether, which we want to avoid
			//the buffer needs to be proportional to the size of the genome or else it won't
			float buffer = selectedSet.totalY*0.005f;
			if(newCenterPoint < (selectedSet.totalY - buffer))
				moveViewport = true;
		}

		//move the genome viewport but make sure we do not scroll off the canvas
		//i.e. we want to be able to always see at least one chromosome
		if(moveViewport)
		{
			winMain.mainCanvas.moveGenomeViewPort(selectedSet, newCenterPoint);
		}

		//repaint
		// TODO: AA check
		winMain.mainCanvas.updateCanvas(true);
	}


	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
