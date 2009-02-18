package sbrn.mapviewer.gui.components;

import java.awt.event.*;
import sbrn.mapviewer.*;

public class CanvasKeyListener implements KeyListener
{
	private MainCanvas mainCanvas;
	
	public CanvasKeyListener(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
	}
	
	public void keyPressed(KeyEvent e)
	{
		//ESC has been pressed
		if(e.getKeyCode() == 27)
		{
			if(MapViewer.winMain.fatController.selectionMap != null)
				MapViewer.winMain.fatController.selectionMap.drawSelectionRect = false;
			mainCanvas.updateCanvas(true);
		}
	}
	
	// not used for now
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
}
