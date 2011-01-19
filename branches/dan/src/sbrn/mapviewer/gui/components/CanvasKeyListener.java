package sbrn.mapviewer.gui.components;

import java.awt.event.*;
import sbrn.mapviewer.*;

public class CanvasKeyListener implements KeyListener
{
	public CanvasKeyListener()
	{

	}

	public void keyPressed(KeyEvent e)
	{
		//ESC has been pressed
		if(e.getKeyCode() == 27)
		{
			Strudel.winMain.fatController.hideSelectionRect();

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
