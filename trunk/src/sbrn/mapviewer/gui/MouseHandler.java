package sbrn.mapviewer.gui;

import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
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
	int mouseDraggedX = -1;
	int mouseDraggedY = -1;
	MouseOverHandler mouseOverHandler;

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

	// used for selecting chromosomes for display of links and for zooming
	public void mouseClicked(MouseEvent e)
	{


		if (e.isAltDown())
		{
			// System.out.println("mouse clicked with ALT down");
			winMain.mainCanvas.zoomHandler.processClickZoomRequest(e.getX(), e.getY());
			return;
		}

		else if (!isMetaClick(e))
		{
			// System.out.println("mouse clicked once");
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY(), false);
		}

		else if (isMetaClick(e))
		{
			// System.out.println("mouse clicked with CTRL down");
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY(), true);
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
		winMain.mainCanvas.mousePressedX = e.getX();
		winMain.mainCanvas.mousePressedY = e.getY();
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//currently the only use for this is when we do pan-and-zoom and we release the mouse at the end of the panning
	//in that case we want to trigger a zoom event which zooms into the selected region
	public void mouseReleased(MouseEvent e)
	{
		if (e.isControlDown())
		{
			//first repaint without the rectangle showing
			winMain.mainCanvas.drawSelectionRect = false;
			winMain.mainCanvas.repaint();
			
			//then request zooming for the selected map with the given set of coordinates
			//get the selected set first
			int gMapSetIndex = getSelectedSet(e);
			GChromoMap selectedMap = Utils.getSelectedMap(winMain, gMapSetIndex, mousePressedY);
			winMain.mainCanvas.zoomHandler.processPanZoomRequest(selectedMap, mousePressedY, e.getY());
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// used for various ways of zooming for now
	public void mouseDragged(MouseEvent e)
	{
		// figure out whether the user is zooming the left or right genome
		// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);

		// mouse is getting dragged down -- zoom in
		if (e.getY() > mouseDragPosY && !e.isControlDown())
		{
			float newZoomFactor = selectedSet.zoomFactor * 1.1f;
			// don't let the zoom factor fall below 1
			if (newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(newZoomFactor, index);
		}

		// mouse is getting dragged up -- zoom out
		if (e.getY() < mouseDragPosY && !e.isControlDown())
		{
			float newZoomFactor = selectedSet.zoomFactor * 0.9f;
			// don't let the zoom factor fall below 1
			if (newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(newZoomFactor, index);
		}

		// mouse is getting dragged horizontally with CTRL down -- draw a rectangle for zoom selection
		if (e.getX() > mouseDragPosX && e.isControlDown())
		{
			winMain.mainCanvas.mouseDraggedX = e.getX();
			winMain.mainCanvas.mouseDraggedY = e.getY();
			winMain.mainCanvas.drawSelectionRect = true;
			winMain.mainCanvas.repaint();
		}

		//update the current drag positions
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
		// figure out whether the user is zooming the left or right genome
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);

		// work out by how much we have moved the mouse and in which direction
		int notches = e.getWheelRotation();
		float differential = 0;
		if (notches < 0)
		{
//			differential = -(1/selectedSet.zoomFactor);
			differential = -1;
		}
		else
		{
//			differential = 1/selectedSet.zoomFactor;
			differential = 1;
		}

		System.out.println("differential = " + differential);
		System.out.println("moving to centerpoint = " + (int)(selectedSet.centerPoint + differential));
		
		winMain.mainCanvas.moveGenomeViewPort(selectedSet, (int)(selectedSet.centerPoint + differential));
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// finds out which of the two genomes the current selection relates to
	private int getSelectedSet(MouseEvent e)
	{
		// figure out whether the user is zooming the left or right genome
		// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		int index = -1;
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
		return index;
	}


	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
