package sbrn.mapviewer.gui;

import java.io.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.handlers.*;

public class DataLoadThread extends Thread
{

	File inputFile;
	
	public DataLoadThread(String inputFileName)
	{
		inputFile = new File (inputFileName);
	}

	public void run()
	{
		try
		{
			// data is loaded now -- check whether the user has cancelled
			if (!MapViewer.winMain.fatController.dataLoadCancelled)
			{
				// load the data
				// we do this by simply creating a new data container instance -- the actual data loading is done through this
				MapViewer.winMain.dataContainer = new DataContainer(inputFile);
				//check it all loaded ok
				if(!MapViewer.dataLoaded)
					return;
				
				// if users load datasets in succession we need to make sure we don't run out of memory
				// so we want any old data containers to be thrown away -- just run the garbage collector explicitly to do this
				System.gc();

				
				// build the rest of the GUI as required
				if (!MapViewer.winMain.fatController.guiFullyAssembled)
					MapViewer.winMain.fatController.assembleRemainingGUIComps();
				else
					MapViewer.winMain.reinitialiseDependentComponents();

				// check if we need to enable some functionality -- depends on the number of genomes loaded
				// cannot do comparative stuff if user one loaded one (target) genome
				if (MapViewer.winMain.dataContainer.gMapSets.size() == 1)
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
					//enable the comparative mode controls in the results table's control panel
					MapViewer.winMain.foundFeaturesTableControlPanel.getFilterLabel().setEnabled(true);
					MapViewer.winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setEnabled(true);
					MapViewer.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().setEnabled(true);
					
					
					// also need a new link display manager because it holds the precomputed links
					MapViewer.winMain.mainCanvas.linkDisplayManager = new LinkDisplayManager(MapViewer.winMain.mainCanvas);
				}
				
				// hide the data loading progress dialog
				if (MapViewer.winMain.dataLoadingDialog != null)
					MapViewer.winMain.dataLoadingDialog.setVisible(false);
				
				// hide the start panel if it is still showing
				MapViewer.winMain.showStartPanel(false);
				
				// revalidate the GUI
				MapViewer.winMain.validate();
				
				//repaint the main canvas
				MapViewer.winMain.mainCanvas.updateCanvas(true);
				
				// bring the focus back on the main window -- need this in case we had an overview dialog open (which then gets focus)
				MapViewer.winMain.requestFocus();
			}
			// user has cancelled
			else
			{

			}
			
			// reset the cancel flag as the user might now want to try again
			MapViewer.winMain.fatController.dataLoadCancelled = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
