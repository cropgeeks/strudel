package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;

public class FatController
{
	
	// ===============================================vars===================================
	
	private WinMain winMain = Strudel.winMain;
	
	//a map we are inverting
	public static GChromoMap invertMap = null;
	
	//a map we are drawing a selection rectangle over for the purpose of including additional features in a range search
	public static GChromoMap selectedMap = null;
	
	//true if all the components required for showing data have been assembled
	//we need this flag because initially we have to show the GUI in an incomplete state -- the full set
	//of components requires knowledge of the data first (i.e. how many genomes do we have)
	public boolean guiFullyAssembled = false;
	
	//this boolean indicates whether we load our own data or the example data provided by the application
	public boolean loadOwnData = false;
	
	//results table entries selected for strong emphasis highlighting
	public LinkedList<ResultsTableEntry> highlightedTableEntries;
	
	public boolean dataLoadCancelled = false;
	
	//true if we are using drag and drop to specify the input file
	public boolean dragAndDropDataLoad = false;
	
	public Vector<GChromoMap> selectedMaps = new Vector<GChromoMap>();
	
	//true if the user is adding maps to their selection by Ctrl clicking
	public boolean isCtrlClickSelection = false;
	
	//true if the user selects a whole genome at a time
	//	public boolean isWholeGenomeSelection = false;
	
	//true if we are loading a file from the recent docs list
	public boolean recentFileLoad = false;
	
	
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
		Strudel.winMain.zoomControlPanel.updateSlider();
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
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//this includes clearing any results of feature searches
	public void resetMainCanvasView()
	{
		clearResultsTable();
		
		//disable the button that allows export of this data to file
		Actions.saveResults.setEnabled(false);
		
		//set the value of the score spinner to the worst value for the current dataset so no link filtering happens
		if(Strudel.winMain.dataSet.dataFormat == Constants.FILEFORMAT_STRUDEL)
			Strudel.winMain.toolbar.initScoreSpinnerForEValues();
		else if(Strudel.winMain.dataSet.dataFormat == Constants.FILEFORMAT_MAF)
			Strudel.winMain.toolbar.initScoreSpinnerForIntegerScores();
		
		//deselect the button on the zoom control panel
		Strudel.winMain.zoomControlPanel.overrideMarkersAutoDisplayButton.setSelected(false);
		
		resetViewOnly();
	}

	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//hides the results table at the bottom of the main canvas and clears its table model etc
	public void clearResultsTable()
	{
		//clear the found features
		clearHighlightFeatures();
		if(FeatureSearchHandler.featuresInRange != null)
			FeatureSearchHandler.featuresInRange.clear();
		winMain.mainCanvas.drawFoundFeaturesInRange = false;
		
		winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setSelectedIndex(0);
		//clear the table model for the found features
		winMain.ffResultsPanel.getFFResultsTable().setModel(new DefaultTableModel());
		
		//hide the found features part of the split pane
		winMain.hideBottomPanel(false);
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//without clearing any results
	public void clearMouseOverLabels()
	{
		for(GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{
				//don't draw mouseover feature labels
				gMap.drawMouseOverFeatures = false;
			}
		}
		
		//repaint
		winMain.mainCanvas.updateCanvas(false);
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//without clearing any results
	public void resetViewOnly()
	{
		
		for(GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			//reset zoom on all mapsets
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
			
			//reset selected maps
			selectedMaps.clear();
			
			//marker and label display overrides
			gMapSet.overrideMarkersAutoDisplay = false;
			winMain.chromoContextPopupMenu.showAllLabelsItem.setText(winMain.chromoContextPopupMenu.showAllLabelsStr);
			
			//max zoom factor
			gMapSet.maxZoomFactor = Constants.MAX_ZOOM_FACTOR;
			
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{
				//clear the outline
				gMap.highlight = false;
				
				//any inverted maps have to be flagged as non-inverted
				gMap.isPartlyInverted = false;
				gMap.isFullyInverted = false;
				
				//clear any highlighted regions
				gMap.highlightChromomapRegion = false;
				
				//don't draw selection rectangle
				gMap.drawFeatureSelectionRectangle = false;
				
				//don't draw mouseover feature labels
				gMap.drawMouseOverFeatures = false;
				//or labels
				gMap.alwaysShowAllLabels = false;
			}
			
		}
		
		//clear any highlighting of genome labels
		winMain.genomeLabelPanel.resetSelectedMapset();
		winMain.zoomControlPanel.selectedSet = null;
		//and reset the zoom spinner
		//need to flag up the fact that this is done from within code to suppress warnings to the user
		winMain.zoomControlPanel.programmaticZoomSpinnerChange = true;
		winMain.zoomControlPanel.maxZoomSpinner.setValue(Constants.MAX_ZOOM_FACTOR);
		winMain.zoomControlPanel.programmaticZoomSpinnerChange = false;		
		
		//clear the feature selection rectangle
		clearSelectionRectangle();
		
		//clear the zoom selection rectangle
		winMain.mainCanvas.drawZoomSelectionRectangle = false;
		
		//repaint
		winMain.mainCanvas.updateCanvas(true);
		
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// assemble the rest of the GUI as required
	public void assembleRemainingGUIComps()
	{
		winMain.setupRemainingComponents();
		guiFullyAssembled = true;
	}
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//clears the green feature selection rectangle drawn around a map region with mouse drag, and any feature links associated
	//repaint needs to be called separately 
	public void clearSelectionRectangle()
	{
		if(Strudel.winMain.fatController.selectedMap != null)
		{
			Strudel.winMain.fatController.selectedMap.drawFeatureSelectionRectangle = false;
			Strudel.winMain.fatController.selectedMap = null;
			//reset any features selected in a range
			winMain.mainCanvas.drawLinksOriginatingInRange = false;
			winMain.mainCanvas.linkDisplayManager.featuresSelectedByRange = null;
		}
	}
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void addSelectedMap(GChromoMap chromoMap)
	{
		selectedMaps.add(chromoMap);
		chromoMap.highlight = true;
	}
	
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	//without clearing any results
	public void clearMapHighlighting()
	{
		for(GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			gMapSet.wholeMapsetIsSelected = false;
			
			//for all maps within mapset
			for(GChromoMap gMap: gMapSet.gMaps)
			{
				//clear the outline
				gMap.highlight = false;
			}
		}
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//clears the highlighted feature, its homolog, and associated gMap objects
	public void clearHighlightFeatures()
	{
		highlightedTableEntries = null;
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class






