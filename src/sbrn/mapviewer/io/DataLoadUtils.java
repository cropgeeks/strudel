package sbrn.mapviewer.io;

import java.io.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import scri.commons.gui.*;

public class DataLoadUtils
{
	
	
	public static void loadDataInThread(String inputFileName)
	{
		MapViewer.winMain.dataLoadingDialog = new MTDataLoadingDialog(MapViewer.winMain, false);

		//first check that we have at least one pointer at a file with target feature data -- the bare minimum to run this application
		//missing target data file	
		
		//if the user wants to load their own data we need to check they have provided the correct file combination
		if(MapViewer.winMain.fatController.loadOwnData)
		{
			inputFileName = getUserInputFile();
		}
		//load example data if user has not specified own data or dragged and dropped a file
		else if(!MapViewer.winMain.fatController.loadOwnData && !MapViewer.winMain.fatController.dragAndDropDataLoad)
		{
			// load the example data that ships with the application
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			inputFileName = workingDir + fileSep + Constants.exampleDataAllInOne;
		}

		//then load the data in a separate thread
		DataLoadThread dataLoadThread = new DataLoadThread(inputFileName);
		dataLoadThread.start();
		
		//show a dialog with a progress bar
		MapViewer.winMain.dataLoadingDialog.setLocationRelativeTo(MapViewer.winMain);
		MapViewer.winMain.dataLoadingDialog.setVisible(true);
		
	}
	
	
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	public static String getUserInputFile()
	{
		MTOpenFilesPanel openFilesPanel = MapViewer.winMain.openFileDialog.openFilesPanel;
		
		String inputFileName = null;
		
		//for each file, check whether we have a file chosen by the user -- if not, the respective
		//text field should be empty
		if(!openFilesPanel.getInputFileTF().getText().equals(""))
			inputFileName = openFilesPanel.getInputFileTF().getText();				

		//check whether user has specified files correctly				
		//missing target data file
		if(inputFileName == null && MapViewer.winMain.fatController.loadOwnData)
		{
			String errorMessage = "The input data file has not been specified. Please try again.";
			TaskDialog.error(errorMessage, "Close");
		}			

		return inputFileName;
	}
}
