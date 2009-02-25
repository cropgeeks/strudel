package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;

public class FatController
{
	
	// ===============================================vars===================================
	
	private WinMain winMain = MapViewer.winMain;
	
	//a vector of features we have looked up by position range
	public Vector<Feature> featuresInRange = new Vector<Feature>();

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
		MapViewer.logger.finest("indexing position arrays");
		long startTime = System.nanoTime();
		// for all gmapsets
		for (GMapSet gMapSet : winMain.dataContainer.gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				gChromoMap.initArrays();
			}
		}
		
		// update the display
		winMain.mainCanvas.updateCanvas(true);
		MapViewer.logger.finest(" done indexing position arrays");
		MapViewer.logger.finest("time taken (nanos) = " + (System.nanoTime() - startTime));
	}
	
	
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//sets up the data model for features we have searched for by name or range
	public LinkedList<Link> matchFeaturesToNames(String [] featureNames)
	{
		LinkedList<Link> homologies = new LinkedList<Link>();
		
		//parse the strings out into the table model and populate as appropriate
		for (int i = 0; i < featureNames.length; i++)
		{
			//retrieve the Feature that corresponds to this name
			Feature f = Utils.getFeatureByName(featureNames[i].trim());
			if (f != null)
			{
				//get all the links this feature is involved in
				//for each link
				for (Link link : f.getLinks())
				{
					//create a new entry in the homologies list
					homologies.add(link);
				}
			}
		}
		
		return homologies;
	}

	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//restores the original view to what it looked like after loading the current dataset
	public void resetMainCanvasView()
	{	
		//clear inputs to the find feature dialog
		MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().setText("");
		
		//hide the found features part of the split pane
		winMain.hideSplitPaneBottomHalf();
		winMain.splitPane.setDividerLocation(1.0);
		
		//clear the table model for the found features
		winMain.ffResultsPanel.getFFResultsTable().setModel(new FoundFeatureTableModel());
		
		//clear the found features
		MapViewer.winMain.fatController.highlightFeature = null;
		MapViewer.winMain.fatController.highlightFeatureHomolog = null;
		if(featuresInRange != null)
			featuresInRange.clear();		
		winMain.mainCanvas.drawHighlightFeatures = false;
		winMain.mainCanvas.drawFoundFeaturesInRange = false;
		findFeaturesRequested = false;
		
		for(GMapSet gMapSet : winMain.dataContainer.gMapSetList)
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
				
				//any inverted maps have to be flagged as non-inverted
				gMap.isPartlyInverted = false;
				
				//clear any highlighted regions
				gMap.highlightChromomapRegion = false;
				
				//don't draw selection rectangle
				gMap.drawSelectionRect = false;
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
	
	public void initialiseNewProject()
	{
		// load the data			
		//the easiest way of doing this is by simply creating a new data container instance
		winMain.dataContainer = new DataContainer();
		//if users load datasets in succession we need to make sure we don't run out of memory
		//we want any old data containers to be thrown away
		//run the garbage collector explicitly now
		System.gc();
		//check the memory situation
		MapViewer.logger.fine("memory max (mb) = " + Runtime.getRuntime().maxMemory()/1024/1024);
		MapViewer.logger.fine("memory available = (mb) " + Runtime.getRuntime().freeMemory()/1024/1024);
		
		//build the rest of the GUI as required
		if(!winMain.fatController.guiFullyAssembled)
			winMain.fatController.assembleRemainingGUIComps();
		else
			winMain.reinitialiseDependentComponents();
		
		//also need a new link display manager because it holds the precomputed links
		winMain.mainCanvas.linkDisplayManager = new LinkDisplayManager(winMain.mainCanvas);	
		
		//check if we need to enable some functionality -- depends on the number of genomes loaded
		//cannot do comparative stuff if user one loaded one (target) genome
		if(winMain.dataContainer.gMapSetList.size() == 1)
		{
			winMain.toolbar.bFindFeatures.setEnabled(false);
			winMain.toolbar.bFindFeaturesinRange.setEnabled(false);
		}
		else
		{
			winMain.toolbar.bFindFeatures.setEnabled(true);
			winMain.toolbar.bFindFeaturesinRange.setEnabled(true);
		}
	
	}
		
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class






