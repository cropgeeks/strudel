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
		for(OverviewCanvas overviewCanvas : winMain.overviewCanvases)
		{
			overviewCanvas.repaint();
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//update visible zoom info
	public void updateZoomControls()
	{
		for (ZoomControlPanel zoomControlPanel : winMain.zoomControlPanels)
		{
			zoomControlPanel.updateSliders();
		}
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
	
	public void initialisePositionArrays()
	{
		// for all gmapsets
		for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				gChromoMap.initArrays();
			}
		}
		
		// update the display
		winMain.mainCanvas.repaint();
	}
	
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
