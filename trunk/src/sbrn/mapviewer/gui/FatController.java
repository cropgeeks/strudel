package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;

public class FatController
{

	// ===============================================vars===================================

	private WinMain winMain;

	// ===============================================c'tors===================================

	public FatController(WinMain winMain)
	{
		this.winMain = winMain;
	}

	// ===============================================methods===================================

	// repaint the overview canvases
	public void updateOverviewCanvases()
	{
		winMain.targetOverviewCanvas.repaint();
		winMain.referenceOverviewCanvas.repaint();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void changeBackgroundColour(String newColour)
	{
		Color colour = null;

		if (newColour.equals("black"))
		{
			colour = Color.BLACK;
		}
		else if (newColour.equals("light grey"))
		{
			colour = Color.LIGHT_GRAY;
		}
		else if (newColour.equals("dark grey"))
		{
			colour = Color.DARK_GRAY;
		}
		else if (newColour.equals("white"))
		{
			colour = Color.white;
		}

		// set all canvas backgrounds to the same colour
		winMain.mainCanvas.setBackground(colour);

		// update the display
		winMain.mainCanvas.repaint();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
