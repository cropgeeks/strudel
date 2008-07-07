package sbrn.mapviewer.gui;

import java.awt.event.*;

import javax.swing.event.MouseInputListener;

import sbrn.mapviewer.gui.entities.GMapSet;

public class MouseHandler implements MouseInputListener, MouseWheelListener
{
	WinMain winMain;
	
	int mouseDragPosY = 0;
	
	public MouseHandler(WinMain winMain)
	{
		this.winMain = winMain;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 1)
		{
			System.out.println("mouse clicked");
			winMain.mainCanvas.processLinkDisplayRequest(e.getX(), e.getY());
		}
		else
			if (e.getClickCount() == 2)
			{
				winMain.mainCanvas.processClickZoomRequest(e.getX(), e.getY());
			}
	}
	
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}
	
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
	
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	//mouse scrolling of canvas
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
			differential = 1;
		}
		else
		{
			differential = -1;
		}
		
		winMain.mainCanvas.moveGenomeViewPort(selectedSet, selectedSet.centerPoint + differential);
	}
	
	private int getSelectedSet(MouseEvent e)
	{
		// figure out whether the user is zooming the left or right genome
		// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		int index  = -1;
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
	
}
