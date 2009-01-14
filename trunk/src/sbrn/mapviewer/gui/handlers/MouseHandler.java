package sbrn.mapviewer.gui.handlers;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

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
	MouseOverHandler mouseOverHandler;

	long timeOfMouseDown = 0;
	
	private boolean isOSX = SystemUtils.isMacOS();
	
	// ===============================================c'tors===========================================
	
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
		MapViewer.logger.finest("mouse clicked");
		
		//mouse click with alt held down means zoom into single chromo so it fills the screen
		if (e.isAltDown())
		{
			GChromoMap selectedMap = Utils.getSelectedMap(MapViewer.winMain.dataContainer.gMapSetList, e.getX(),
							e.getY());
			
			if (selectedMap != null)
				winMain.mainCanvas.zoomHandler.processClickZoomRequest(selectedMap);
			return;
		}
		
		//turn antialiasing on and repaint
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mousePressed(MouseEvent e)
	{
		MapViewer.logger.finest("mouse pressed");
		
		mousePressedX = e.getX();
		mousePressedY = e.getY();
		
		timeOfMouseDown = System.currentTimeMillis();
		
		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(mouseOverHandler.selectedMap != null)
			{
				// get the selected set first
				GChromoMap selectedMap = Utils.getSelectedMap(winMain, Utils.getSelectedSet(e), mousePressedY);
				winMain.fatController.invertMap = selectedMap;
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
			GChromoMap selectedMap = Utils.getSelectedMap(MapViewer.winMain.dataContainer.gMapSetList, e.getX(), e.getY());
			//if we have clicked on a map, display links between this map and all others
			if(selectedMap != null)
			{
				winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY(), false);
			}
			//otherwise -- if we clicked on the background -- clear all links displayed
			else
			{
				for(GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSetList)
				{
					//reset selected maps
					gMapSet.selectedMaps.clear();
					
					//for all maps within mapset
					for(GChromoMap gMap: gMapSet.gMaps)
					{			
						//clear the outline
						gMap.drawHighlightOutline = false;
					}
				}
				winMain.mainCanvas.drawLinks = false;
				winMain.mainCanvas.updateCanvas(true);
			}
		}
		//CTRL+click on a chromosome means display all links between this and all other clicked chromos
		else if (isMetaClick(e))
		{
			winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY(), true);
		}
		
		//turn antialiasing off and repaint
		winMain.mainCanvas.antiAlias = false;
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	
	public void mouseReleased(MouseEvent e)
	{
		MapViewer.logger.fine("mouse released at " + e.getY());

		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(mouseOverHandler.selectedMap != null)
			{
				// get the selected set first
				GChromoMap selectedMap = Utils.getSelectedMap(winMain, Utils.getSelectedSet(e), mousePressedY);
				winMain.fatController.invertMap = selectedMap;
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
			int gMapSetIndex = Utils.getSelectedSet(e);
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, mousePressedY);
			winMain.mainCanvas.zoomHandler.processPanZoomRequest(selectedMap, mousePressedY, e.getY());
		}
		
		MapViewer.logger.finest("repainting after mouse released");
		//turn antialiasing on and repaint
		winMain.mainCanvas.drawSelectionRect = false;
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// used for zooming and scrolling
	public void mouseDragged(MouseEvent e)
	{
		MapViewer.logger.finest("mouse dragged");
		
		int x = e.getX();
		int y = e.getY();

		//mouse is getting dragged without shift held down -- scroll the canvas up or down
		if (!e.isShiftDown())
		{
			// figure out which genome the user is zooming 
			int index = Utils.getSelectedSet(e);
			GMapSet gMapSet = MapViewer.winMain.dataContainer.gMapSetList.get(index);
			
			//include a time delay before dragging so we can prevent accidental drags that were in fact intended to be mouse clicks
			long now = System.currentTimeMillis();
			if (now - timeOfMouseDown < 200)
				return;
				
			// mouse is getting dragged up -- zoom in
			if (y < mouseDragPosY)
			{
				// the multiplier is the amount by which we multiply the current zoom factor to increase it
				float multiplier = 1.01f;
				winMain.mainCanvas.moveGenomeViewPort(gMapSet, (int) (gMapSet.centerPoint * multiplier));
			}
			// mouse is getting dragged down -- zoom out
			if (y > mouseDragPosY)
			{
				// the multiplier is the amount by which we multiply the current zoom factor to decrease it
				float multiplier = 0.99f;
				winMain.mainCanvas.moveGenomeViewPort(gMapSet, (int) (gMapSet.centerPoint * multiplier));
			}
		}

		// mouse is getting dragged horizontally with SHIFT down -- draw a rectangle for zoom selection
		if (e.isShiftDown())
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
		//turn antialiasing off for faster scrolling
		winMain.mainCanvas.antiAlias = false;
		
		// figure out whether the user is zooming the left or right genome
		int index = Utils.getSelectedSet(e);
		GMapSet selectedSet = MapViewer.winMain.dataContainer.gMapSetList.get(index);
		
		// work out by how much we have moved the mouse and in which direction
		int notches = e.getWheelRotation();
		float differential = 0;
		if (notches < 0)
		{
			differential = -(selectedSet.totalY / selectedSet.zoomFactor) * 0.3f;
		}
		else
		{
			differential = (selectedSet.totalY / selectedSet.zoomFactor) * 0.3f;
		}
		
		int newCenterPoint = (int) (selectedSet.centerPoint + differential);
		winMain.mainCanvas.moveGenomeViewPort(selectedSet, newCenterPoint);
		
		//turn antialiasing back on and repaint
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
		
	}

	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
