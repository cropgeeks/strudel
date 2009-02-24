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
	
	private boolean isOSX = SystemUtils.isMacOS();
	
	int scrollGenomeIncrement = 200;
	
	int lastMouseDragYPos = -1;

	
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

		//place the focus on this window so we can listen to keyboard events too
		winMain.mainCanvas.requestFocusInWindow();
				
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
		MapViewer.logger.finest("mouse released at " + e.getY());
		
		//check whether this is a popup request -- needs to be done both in mousePressed and in mouseReleased due to platform dependent nonsense
		if (e.isPopupTrigger())
		{
			//this is for bringing up a context menu when the mouse is over a chromosome
			if(mouseOverHandler.selectedMap != null)
			{
				// get the selected map first
				GChromoMap selectedMap = Utils.getSelectedMap(winMain, Utils.getSelectedSet(e), mousePressedY);
				winMain.fatController.invertMap = selectedMap;
				
				//if we have got here because we had first drawn a selection rectangle for including all features in a range for inclusion 
				//in the results table, then we want the context menu to only have the option for this, and not inverting chromos etc
				if(selectedMap.drawSelectionRect)
				{
					winMain.chromoContextPopupMenu.addAllFeaturesItem.setVisible(true);
					winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(false);
				}
				else
				{
					winMain.chromoContextPopupMenu.addAllFeaturesItem.setVisible(false);
					winMain.chromoContextPopupMenu.invertChromoMenuItem.setVisible(true);
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
			int gMapSetIndex = Utils.getSelectedSet(e);
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, e.getY());
			winMain.mainCanvas.zoomHandler.processPanZoomRequest(selectedMap, mousePressedY, e.getY());
			
		}

		//turn antialiasing on and repaint		
		MapViewer.logger.finest("repainting after mouse released");
		winMain.mainCanvas.drawSelectionRect = false;
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// used for zooming and scrolling
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		MapViewer.logger.fine("mouseDragged at (x,y) = "+ x + "," + y);
		MapViewer.logger.fine("last mouse pressed coords (mousePressedX, mousePressedY) = " + mousePressedX + "," + mousePressedY);
		
		// figure out which genome this event pertains to (i.e. which section of the canvas on x are we in)
		int index = Utils.getSelectedSet(e);
		GMapSet gMapSet = MapViewer.winMain.dataContainer.gMapSetList.get(index);
		
		//the chromosome -- if any - this event pertains to (i.e. where on the canvas on y are we)
		GChromoMap selectedMap = Utils.getSelectedMap(MapViewer.winMain.dataContainer.gMapSetList, (int)(gMapSet.xPosition), y);
		
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
			
			MapViewer.logger.fine("distanceDragged = " + distanceDragged);
			MapViewer.logger.fine("lastMouseDragYPos when dragged = " + lastMouseDragYPos);
			
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
				
				MapViewer.winMain.fatController.selectionMap = selectedMap;
				//let the MAP draw this rectangle -- we want to have this rect associated with the map and redrawn when the map is rendered
				selectedMap.drawSelectionRect = true;	
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
		//turn antialiasing off for faster scrolling
		winMain.mainCanvas.antiAlias = false;
		
		// figure out which genome we are moving
		int index = Utils.getSelectedSet(e);
		GMapSet selectedSet = MapViewer.winMain.dataContainer.gMapSetList.get(index);
		
		// work out   in which direction we have moved the mouse
		int notches = e.getWheelRotation();
		
		//this moves the genome center point up and down
		int newCenterPoint = -1;

		if (notches < 0)
		{
			newCenterPoint = (int) (selectedSet.centerPoint  - scrollGenomeIncrement);
		}
		else
		{
			newCenterPoint = (int) (selectedSet.centerPoint  + scrollGenomeIncrement);
		}

		winMain.mainCanvas.moveGenomeViewPort(selectedSet, newCenterPoint);
		
		//repaint with antialiasing if required
		if(MapViewer.winMain.mainCanvas.antiAlias)
		{
			AntiAliasRepaintThread antiAliasRepaintThread = new AntiAliasRepaintThread();
			antiAliasRepaintThread.start();
		}
		
	}
	
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
