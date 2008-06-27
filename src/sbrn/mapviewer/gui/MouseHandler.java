package sbrn.mapviewer.gui;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class MouseHandler implements MouseInputListener
{
	WinMain winMain;
	
	public MouseHandler(WinMain winMain)
	{
		this.winMain = winMain;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		winMain.mainCanvas.processClickZoomRequest(e.getX(), e.getY());
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
		// TODO Auto-generated method stub
		
	}
	
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
}
