package sbrn.mapviewer.gui;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import sbrn.mapviewer.gui.entities.GMapSet;

public class MouseHandler implements MouseInputListener
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
		//figure out whether the user is zooming the left or right genome
		//simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
		GMapSet selectedSet = null;
		int index = -1;
		if(e.getX() < winMain.mainCanvas.getWidth()/2)
		{
			//left hand side hit
			selectedSet = winMain.mainCanvas.targetGMapSet;
			index = 0;
		}
		else
		{
			//right hand side hit
			selectedSet = winMain.mainCanvas.referenceGMapSet;
			index = 1;
		}
		
		
		//mouse is getting dragged down -- zoom in
		if (e.getY() > mouseDragPosY)
		{
			float newZoomFactor = selectedSet.zoomFactor *1.1f;
			//don't let the zoom factor fall below 1
			if(newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.processSliderZoomRequest(newZoomFactor, index);
		}
		//mouse is getting dragged up -- zoom out
		if(e.getY() < mouseDragPosY)
		{
			float newZoomFactor = selectedSet.zoomFactor *0.9f;
			//don't let the zoom factor fall below 1
			if(newZoomFactor < 1)
				newZoomFactor = 1;
			winMain.mainCanvas.processSliderZoomRequest(newZoomFactor, index);
		}
		mouseDragPosY = e.getY();		
	}
	
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
}
