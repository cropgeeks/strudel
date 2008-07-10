package sbrn.mapviewer.gui;

import java.awt.event.*;

import javax.swing.event.MouseInputListener;

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
	
	//used for selecting chromosomes for display of links and for zooming
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 1 && e.isControlDown())
		{
			winMain.mainCanvas.processClickZoomRequest(e.getX(), e.getY());
			return;
		}
		
		if (e.getClickCount() == 1)
		{
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY());
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
		System.out.println("mouse pressed");
		winMain.mainCanvas.antiAliasOn = false;
//		winMain.mainCanvas.repaint();		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseReleased(MouseEvent e)
	{
		System.out.println("mouse released");
		winMain.mainCanvas.antiAliasOn = true;
		winMain.mainCanvas.repaint();
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//used for zooming for now
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
		mouseOverHandler.detectMouseOver(e.getX(), e.getY());		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// mouse scrolling of canvas
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// figure out whether the user is zooming the left or right genome
		// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		int index = getSelectedSet(e);
		GMapSet selectedSet = winMain.mainCanvas.gMapSetList.get(index);
		
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
		
//		winMain.mainCanvas.antiAliasOn = false;
		winMain.mainCanvas.moveGenomeViewPort(selectedSet, selectedSet.centerPoint + differential);	
//		winMain.mainCanvas.antiAliasOn = true;
//		winMain.mainCanvas.repaint();
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//finds out which of the two genomes the current selection relates to
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
