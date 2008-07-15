package sbrn.mapviewer.gui;

import java.awt.event.*;

import javax.swing.event.MouseInputListener;

import sbrn.mapviewer.gui.entities.GChromoMap;
import sbrn.mapviewer.gui.entities.GMapSet;

public class MouseHandler implements MouseInputListener, MouseWheelListener
{
	// =================================================vars========================================
	
	WinMain winMain;
	int mouseDragPosY = 0;
	MouseOverHandler mouseOverHandler;
	
	// ===============================================c'tors===========================================
	
	public MouseHandler(WinMain winMain)
	{
		this.winMain = winMain;
		mouseOverHandler = new MouseOverHandler(winMain);
	}
	
	// =================================================methods=======================================
	
	// used for selecting chromosomes for display of links and for zooming
	public void mouseClicked(MouseEvent e)
	{
		if (e.isAltDown())
		{
			System.out.println("mouse clicked once with ALT down");
			winMain.mainCanvas.processClickZoomRequest(e.getX(), e.getY());
			return;
		}
		
		else if (!e.isControlDown())
		{
			System.out.println("mouse clicked once");
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY(), false);
		}
		
		else if (e.isControlDown())
		{
			System.out.println("mouse clicked once with CTRL down");
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY(), true);
		}
		

	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// used for zooming for now
	public void mouseDragged(MouseEvent e)
	{
		// figure out whether the user is zooming the left or right genome
		// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);
		
		// mouse is getting dragged down -- zoom in
		if (e.getY() > mouseDragPosY)
		{
			float newZoomFactor = selectedSet.zoomFactor * 1.1f;
			// don't let the zoom factor fall below 1
			if (newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.processSliderZoomRequest(newZoomFactor, index);
		}
		// mouse is getting dragged up -- zoom out
		if (e.getY() < mouseDragPosY)
		{
			float newZoomFactor = selectedSet.zoomFactor * 0.9f;
			// don't let the zoom factor fall below 1
			if (newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.processSliderZoomRequest(newZoomFactor, index);
		}
		mouseDragPosY = e.getY();
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseMoved(MouseEvent e)
	{
//		if (winMain.mainCanvas.overviewMode)
//		{
//			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY(), false);
//		}
//		else
//		{
//			mouseOverHandler.detectMouseOver(e.getX(), e.getY());
//		}
		
		 mouseOverHandler.detectMouseOver(e.getX(), e.getY());
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// mouse scrolling of canvas
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		System.out.println("mousewheelmove starting");
		
		// figure out whether the user is zooming the left or right genome
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);
		
		//work out by how much we have moved the mouse and in which direction
		int notches = e.getWheelRotation();
		int differential = 0;
		if (notches < 0)
		{
			differential = -1;
		}
		else
		{
			differential = 1;
		}
		
		winMain.mainCanvas.moveGenomeViewPort(selectedSet, selectedSet.centerPoint + differential);
		System.out.println("mousewheelmove complete");
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
	
	private GChromoMap getSelectedMap(MouseEvent e)
	{
		GChromoMap selectedMap = null;
		
		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in each genome
		for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.contains(e.getX(), e.getY()))
				{
					selectedMap = gChromoMap;
					break;
				}
			}
		}
		
		// the click has hit a chromosome
		if (selectedMap != null)
		{
			return selectedMap;
		}
		// no hit detected
		else
		{
			return null;
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
