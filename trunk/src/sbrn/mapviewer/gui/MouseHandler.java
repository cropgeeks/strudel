package sbrn.mapviewer.gui;

import java.awt.event.*;
import java.util.*;

import javax.swing.event.*;

import sbrn.mapviewer.data.*;
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
	
	long timeOfLastDragRequest = 0;

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
		if (e.isAltDown())
		{
			GChromoMap selectedMap = Utils.getSelectedMap(winMain.mainCanvas.gMapSetList, e.getX(),
							e.getY());

			if (selectedMap != null)
				winMain.mainCanvas.zoomHandler.processClickZoomRequest(selectedMap);
			return;
		}

		else if (e.isShiftDown() && isMetaClick(e))
		{
			Vector<Feature> selectedFeatures = mouseOverHandler.detectMouseOver(e.getX(), e.getY());
			mouseOverHandler.updateAnnotationDisplay(selectedFeatures, e.getX(), e.getY());
		}

		else if (!isMetaClick(e))
		{
			winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY(), false);
			new AntiAliasRepaintThread().start();

		}

		else if (isMetaClick(e))
		{
			winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(e.getX(), e.getY(), true);
			new AntiAliasRepaintThread().start();
		}

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
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	public void mouseReleased(MouseEvent e)
	{
		//this is when we do pan-and-zoom and we release the mouse at the end of the panning
		// in that case we want to trigger a zoom event which zooms into the selected region
		if (e.isShiftDown() && !isMetaClick(e))
		{
			// first repaint without the rectangle showing
			winMain.mainCanvas.drawSelectionRect = false;
			winMain.mainCanvas.updateCanvas(true);

			// then request zooming for the selected map with the given set of coordinates
			// get the selected set first
			int gMapSetIndex = getSelectedSet(e);
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, mousePressedY);
			winMain.mainCanvas.zoomHandler.processPanZoomRequest(selectedMap, mousePressedY, e.getY());
			//turn antialiasing on and repaint
			winMain.mainCanvas.antiAlias = true;
			winMain.mainCanvas.updateCanvas(true);
		}
		
		//this is for bringing up a context menu when we the mouse is over a chromosome
		if(e.isPopupTrigger() && mouseOverHandler.selectedMap != null)
		{
			// get the selected set first
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, getSelectedSet(e), mousePressedY);
			winMain.fatController.invertMap = selectedMap;
			winMain.chromoContextPopupMenu.show(winMain.mainCanvas, e.getX(), e.getY());
			//turn antialiasing on and repaint
			winMain.mainCanvas.antiAlias = true;
			winMain.mainCanvas.updateCanvas(true);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// used for various ways of zooming for now
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		
		//mouse is getting dragged without shift held down
		if (!e.isShiftDown())
		{
			// figure out which genome the user is zooming 
			int index = getSelectedSet(e);
				
			//this number is the maximum amount of time that can have elapsed between successive mouse drags in order for them to still count as mouse drags 
			//this is to guard against single mouse clicks associated with a very small mouse drag being interpreted as mouse drag events when they
			//are in fact intended to be mouse clicks
			//if there are gaps between mouse drags longer than this cutoff, the first mousedrag is always ignored
			int timeCutOff = 1000;			
			long timeDifferential = System.currentTimeMillis() - timeOfLastDragRequest;
	
			if (timeDifferential < timeCutOff)
			{
				// mouse is getting dragged down -- zoom in
				if (y > mouseDragPosY)
				{
					// the multiplier is the amount by which we multiply the current zoom factor to increase it
					float multiplier = 1.2f;
					winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(-1, multiplier,
									index, false);
					
					// update the current drag positions
					mouseDragPosX = e.getX();
					mouseDragPosY = e.getY();
				}
				// mouse is getting dragged up -- zoom out
				if (y < mouseDragPosY)
				{
					// the multiplier is the amount by which we multiply the current zoom factor to decrease it
					float multiplier = 0.8f;
					winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(-1, multiplier,
									index, false);
					
					// update the current drag positions
					mouseDragPosX = e.getX();
					mouseDragPosY = e.getY();
				}
			}
			
			//update the timestamp
			timeOfLastDragRequest = System.currentTimeMillis();
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
			
			// update the current drag positions
			mouseDragPosX = e.getX();
			mouseDragPosY = e.getY();
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
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);

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

	// finds out which of the two genomes the current selection relates to
	private int getSelectedSet(MouseEvent e)
	{
		// figure out which genome the user is zooming

		int index = -1;

		// if we have two genomes only
		if (winMain.mainCanvas.gMapSetList.size() == 2)
		{
			// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
			if (e.getX() < winMain.mainCanvas.getWidth() / 2)
			{
				// left hand side hit
				index = 0;
			}
			else
			{
				// right hand side hit
				index = 1;
			}
		}
		else if (winMain.mainCanvas.gMapSetList.size() == 3)
		{
			int oneThirdCanvas = Math.round(winMain.mainCanvas.getWidth() / 3);

			// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
			if (e.getX() <= oneThirdCanvas)
			{
				// left hand side hit
				index = 0;
			}
			else if(e.getX() > oneThirdCanvas && e.getX() <= oneThirdCanvas*2)
			{
				// middle hit
				index = 1;
			}
			else if(e.getX() > oneThirdCanvas*2)
			{
				//right hand side hit
				index = 2;
			}
		}

		return index;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
