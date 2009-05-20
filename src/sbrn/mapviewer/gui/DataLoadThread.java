package sbrn.mapviewer.gui;

import java.io.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.handlers.*;

public class DataLoadThread extends Thread
{

	File targetData;
	File refGenome1FeatData;
	File refGenome1HomData;
	File refGenome2FeatData;
	File refGenome2HomData;
	
	public DataLoadThread(File targetData, File refGenome1FeatData, File refGenome1HomData, File refGenome2FeatData, File refGenome2HomData)
	{
		this.targetData = targetData;
		this.refGenome1FeatData = refGenome1FeatData;
		this.refGenome1HomData = refGenome1HomData;
		this.refGenome2FeatData = refGenome2FeatData;
		this.refGenome2HomData = refGenome2HomData;
	}

	public void run()
	{
		try
		{
			// data is loaded now -- check whether the user has cancelled
			if (!MapViewer.winMain.fatController.dataLoadCancelled)
			{
				MapViewer.logger.fine("loading data in thread");

				// load the data
				// we do this by simply creating a new data container instance -- the actual data loading is done through this
				MapViewer.winMain.dataContainer = new DataContainer(targetData, refGenome1FeatData, refGenome1HomData, refGenome2FeatData, refGenome2HomData);
				//check it all loaded ok
				if(!MapViewer.winMain.dataContainer.dataLoaded)
					return;
				
				// if users load datasets in succession we need to make sure we don't run out of memory
				// so we want any old data containers to be thrown away -- just run the garbage collector explicitly to do this
				System.gc();

				
				// build the rest of the GUI as required
				if (!MapViewer.winMain.fatController.guiFullyAssembled)
					MapViewer.winMain.fatController.assembleRemainingGUIComps();
				else
					MapViewer.winMain.reinitialiseDependentComponents();
				
				// also need a new link display manager because it holds the precomputed links
				MapViewer.winMain.mainCanvas.linkDisplayManager = new LinkDisplayManager(MapViewer.winMain.mainCanvas);
				
				// check if we need to enable some functionality -- depends on the number of genomes loaded
				// cannot do comparative stuff if user one loaded one (target) genome
				if (MapViewer.winMain.dataContainer.gMapSetList.size() == 1)
				{		
					//enables toolbar controls selectively
					MapViewer.winMain.toolbar.enableControls(true);				
					//disable the comparative mode controls in the results table's control panel
					MapViewer.winMain.foundFeaturesTableControlPanel.getFilterLabel().setEnabled(false);
					MapViewer.winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setEnabled(false);
					MapViewer.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().setEnabled(false);
				}
				else
				{
					//enables toolbar controls selectively
					MapViewer.winMain.toolbar.enableControls(false);
				}
				
				// hide the data loading progress dialog
				if (MapViewer.winMain.openFileDialog.dataLoadingDialog != null)
					MapViewer.winMain.openFileDialog.dataLoadingDialog.setVisible(false);
				
				// hide the start panel if it is still showing
				MapViewer.winMain.showStartPanel(false);
				
				// revalidate the GUI
				MapViewer.winMain.validate();
				
				// bring the focus back on the main window -- need this in case we had an overview dialog open (which then gets focus)
				MapViewer.winMain.requestFocus();
			}
			// user has cancelled
			else
			{
				MapViewer.logger.fine("data load cancelled");
			}
			
			// check the memory situation
			MapViewer.logger.fine("memory max (mb) = " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
			MapViewer.logger.fine("memory available = (mb) " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
			
			// reset the cancel flag as the user might now want to try again
			MapViewer.winMain.fatController.dataLoadCancelled = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
