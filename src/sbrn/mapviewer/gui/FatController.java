package sbrn.mapviewer.gui;

import java.awt.*;

public class FatController
{
	
	static WinMain winMain;
	
	
	
	public FatController(WinMain winMain)
	{
		this.winMain = winMain;
	}


	public static void changeBackgroundColour(String newColour)
	{
		if(newColour.equals("black"))
		{
			winMain.mainCanvas.setBackground(Color.black);
		}
		else if(newColour.equals("light grey"))
		{
			winMain.mainCanvas.setBackground(Color.LIGHT_GRAY);
		}
		else if(newColour.equals("dark grey"))
		{
			winMain.mainCanvas.setBackground(Color.DARK_GRAY);
		}
		else if(newColour.equals("white"))
		{
			winMain.mainCanvas.setBackground(Color.white);
		}
		winMain.mainCanvas.repaint();
	}
}
