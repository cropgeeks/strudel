package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

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
		winMain.mainCanvas.updateCanvas(true);
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
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void highlightFeaturesByNames(String namesStr)
	{
		//parse the String -- could be one or more names separated by EOL chars
		String [] names = namesStr.split("\n");
		
		boolean featuresFound = false;
		
		//for each feature in our list of search items
		for (int i = 0; i < names.length; i++)
		{
			//we need to search all chromomaps in all mapsets for this
			
			// for all gmapsets
			for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
			{
				// for all gchromomaps within each mapset
				for (GChromoMap gChromoMap : gMapSet.gMaps)
				{
					//get the ChromoMap object
					//look up the name in this
					Feature f = gChromoMap.chromoMap.getFeature(names[i].trim());
					//if it is there, add the corresponding Feature to the vector of found features of the gMap
					if(f != null)
					{
						featuresFound = true;
						winMain.mainCanvas.drawFoundFeatures = true;
						
						for(Link link : f.getLinks())
						{
							//get both features from the link and put them into their respective found features vectors
							
							//get the features of this link
							Feature f1 = link.getFeature1();
							Feature f2 = link.getFeature2();
							
							GChromoMap gMap1 = f1.getOwningMap().getGChromoMap();
							GChromoMap gMap2 = f2.getOwningMap().getGChromoMap();

							//check whether either of the features for this link are included in the highlightedfeatures list for its map
							if(!gMap1.foundFeatures.contains(f1))
							{
								gMap1.foundFeatures.add(f1);
							}
							if(!gMap2.foundFeatures.contains(f2))
							{
								gMap2.foundFeatures.add(f2);
							}
						}						
					}				
				}
			}
		}
		
		
		//now sort the vector with the found features so that they are in the orer in which they 
		//appear on the chromosome
		// for all gmapsets
		for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				Collections.sort(gChromoMap.foundFeatures);
			}
		}
		
		
		//nothing found by this name
		if(!featuresFound)
		{			
			TaskDialog.initialize(MapViewer.winMain, "MapViewer Error");
			TaskDialog.error("No matching features found", "Close");
			winMain.toolbar.ffDialog.setVisible(true);
		}
		
		// update the display
		winMain.mainCanvas.antiAlias = true;
		winMain.mainCanvas.updateCanvas(true);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	public void resetMainCanvasView()
	{	
		for(GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			//reset zoom on all mapsets
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
			
			//reset selected maps
			gMapSet.selectedMaps.clear();
			
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{			
				//clear the outline
				gMap.drawHighlightOutline = false;
				
				//clear the found features
				gMap.foundFeatures.clear();
			}			
		}	
		
		//repaint
		winMain.mainCanvas.updateCanvas(true);
	}
	
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class






