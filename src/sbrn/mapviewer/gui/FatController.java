package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;

public class FatController
{
	
	// ===============================================vars===================================
	
	private WinMain winMain = MapViewer.winMain;
	
	//a map we are inverting
	public static GChromoMap invertMap = null;
	
	//a map we are drawing a selection rectangle over for the purpose of including additional features in a range search
	public static GChromoMap selectionMap = null;
	
	//true if all the components required for showing data have been assembled
	//we need this flag because initially we have to show the GUI in an incomplete state -- the full set
	//of components requires knowledge of the data first (i.e. how many genomes do we have)
	public boolean guiFullyAssembled = false;
	
	//this boolean indicates whether we load our own data or the example data provided by the application
	public boolean loadOwnData = false;
	
	//feature for highlighting and a single homolog for this
	public Feature highlightFeature, highlightFeatureHomolog;
	
	//true if we have requested to find features by name
	public boolean findFeaturesRequested = false;

	public boolean dataLoadCancelled = false;
	
	//true if we are using drag and drop to specify the input file
	public boolean dragAndDropDataLoad = false;
	
	// ===============================================curve'tors===================================
	
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
	public void updateAllZoomControls()
	{
		for (ZoomControlPanel zoomControlPanel : winMain.zoomControlPanels)
		{
			zoomControlPanel.updateSlider();
		}
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//changes the main canvas background colour
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
	
	//updates the positions of all features of all chromosomes
	//this is necessary because zooming changes the actual position values as the canvas grows
	public void initialisePositionArrays()
	{
		long startTime = System.nanoTime();
		// for all gmapsets
		for (GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				gChromoMap.initArrays();
			}
		}

		MapViewer.logger.finest(" done indexing position arrays");
		MapViewer.logger.finest("time taken (nanos) = " + (System.nanoTime() - startTime));
	}
	

	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//this includes clearing any results of feature searches
	public void resetMainCanvasView()
	{		
		//hide the found features part of the split pane
		winMain.hideSplitPaneBottomHalf();
		winMain.splitPane.setDividerLocation(1.0);
		
		resetViewOnly();

		//clear the found features
		MapViewer.winMain.fatController.highlightFeature = null;
		MapViewer.winMain.fatController.highlightFeatureHomolog = null;
		if(FeatureSearchHandler.featuresInRange != null)
			FeatureSearchHandler.featuresInRange.clear();		
		winMain.mainCanvas.drawHighlightFeatures = false;
		winMain.mainCanvas.drawFoundFeaturesInRange = false;
		findFeaturesRequested = false;
		winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setSelectedIndex(0);
		//clear the table model for the found features
		winMain.ffResultsPanel.getFFResultsTable().setModel(new DefaultTableModel());
		//disable the button that allows export of this data to file
		MapViewer.winMain.toolbar.bSave.setEnabled(false);
		
		//reset the BLAST cut-off
		LinkDisplayManager.setBlastThreshold(1);
		MapViewer.winMain.toolbar.eValueSpinner.setValue(0);
		
	}
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//without clearing any results
	public void clearMouseOverLabels()
	{		
		for(GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{			
				//don't draw mouseover feature labels
				gMap.drawMouseOverFeatures = false;
			}			
		}	

		
		//repaint
		winMain.mainCanvas.updateCanvas(true);
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//without clearing any results
	public void resetViewOnly()
	{		
		for(GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			//reset zoom on all mapsets
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
			
			//reset selected maps
			gMapSet.selectedMaps.clear();
			
			//marker and label display overrides
			gMapSet.overrideMarkersAutoDisplay = false;
			gMapSet.alwaysShowAllLabels = false;
			
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{			
				//clear the outline
				gMap.drawHighlightOutline = false;
				
				//any inverted maps have to be flagged as non-inverted
				gMap.isPartlyInverted = false;
				gMap.isFullyInverted = false;
				
				//clear any highlighted regions
				gMap.highlightChromomapRegion = false;
				
				//don't draw selection rectangle
				gMap.drawSelectionRect = false;
				
				//don't draw mouseover feature labels
				gMap.drawMouseOverFeatures = false;
			}			
		}	
		
		initialisePositionArrays();
		
		//repaint
		winMain.mainCanvas.updateCanvas(true);
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// assemble the rest of the GUI as required
	public void assembleRemainingGUIComps()
	{	
		winMain.setupRemainingComponents();
		winMain.ffInRangeDialog.ffInRangePanel.initRemainingComponents();
		guiFullyAssembled = true;
	}
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void hideSelectionRect()
	{
		if(MapViewer.winMain.fatController.selectionMap != null)
			MapViewer.winMain.fatController.selectionMap.drawSelectionRect = false;
		MapViewer.winMain.mainCanvas.updateCanvas(true);
	}
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class






