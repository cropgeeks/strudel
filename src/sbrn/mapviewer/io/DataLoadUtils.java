package sbrn.mapviewer.io;

import java.io.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.LinkDisplayManager;

import scri.commons.gui.TaskDialog;

public class DataLoadUtils
{
	
	//the minimum chromosome height we will allow
	final static int minimumChromosomeHeight = 5;

	public static void loadDataInThread(String inputFileName, boolean commandLineLoad)
	{
		//first check that we have at least one pointer at a file with target feature data -- the bare minimum to run this application
		//missing target data file
		
		//if the user wants to load their own data we need to check they have provided the correct file combination
		if(Strudel.winMain.fatController.loadOwnData)
		{
			if(!commandLineLoad)
				inputFileName = getUserInputFile();
			else
				inputFileName = Strudel.initialFile;
		}
		//load example data if user has not specified own data or drag-and-dropped a file
		else if(!Strudel.winMain.fatController.loadOwnData && !Strudel.winMain.fatController.dragAndDropDataLoad &&
						!Strudel.winMain.fatController.recentFileLoad)
		{
			// load the example data that ships with the application
			String workingDir = System.getProperty("user.dir");
			String fileSep = System.getProperty("file.separator");
			inputFileName = workingDir + fileSep + Constants.exampleDataAllInOne;
		}
		
		//work out the file format and set up a parser for it
		TrackableReader fileImporter = null;
		StrudelFile strudelFile = new StrudelFile(inputFileName);
		int fileFormat = -1;
		try
		{
			fileFormat = FileFormatDetector.detectFileFormat(strudelFile.getFile());
		}
		catch (FileNotFoundException e)
		{
			TaskDialog.error("File not found", "Close");
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if(fileFormat == FileFormatDetector.FILEFORMAT_STRUDEL)
			fileImporter = new StrudelFormatParser();
		else if(fileFormat == FileFormatDetector.FILEFORMAT_MAF)
			fileImporter = new MAFParser();		
		fileImporter.setInput(strudelFile);

		//set up the progress dialog for the data load
		ProgressDialog dialog = new ProgressDialog(fileImporter, "Data loading", "Data loading - please wait...");
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{

				TaskDialog.error("Data load failed: " + dialog.getException().toString() + "\nPlease correct your data and try again.", "Close");
			}

			return;
		}
		else if(dialog.getResult() == ProgressDialog.JOB_COMPLETED)
		{
			//store the input file in the recent files list
			File inputFile = new File(inputFileName);
			Prefs.setRecentDocument(inputFile.getAbsolutePath());
		}
	}

	public static boolean setUpViewAfterDataLoading()
	{
		//this checks whether we have too many maps to display
		boolean mapNumbersOK = checkMapNumbers();
		if(!mapNumbersOK)
			return false;
		
		LinkDisplayManager.initLinkSetLookup();

		// build the rest of the GUI as required
		if (!Strudel.winMain.fatController.guiFullyAssembled)
			Strudel.winMain.fatController.assembleRemainingGUIComps();
		else
		{
			Strudel.winMain.reinitialiseDependentComponents();
			//reinitialize the combo boxes in the configure genomes dialog
			Strudel.winMain.genomeLayoutDialog.genomeLayoutPanel.setupComboBoxes();
		}

		//display the name of the current dataset in the window title bar
		Strudel.winMain.setTitle(Strudel.winMain.dataSet.fileName + " -- Strudel " + Install4j.VERSION);

		// check if we need to enable some functionality -- depends on the number of genomes loaded
		// cannot do comparative stuff if user one loaded one (target) genome
		if (Strudel.winMain.dataSet.gMapSets.size() == 1)
		{
			//enables toolbar controls selectively
			Strudel.winMain.toolbar.enableControls(true);
			//disable the comparative mode controls in the results table's control panel
			Strudel.winMain.foundFeaturesTableControlPanel.getFilterLabel().setEnabled(false);
			Strudel.winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setEnabled(false);
			Strudel.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().setEnabled(false);
		}
		else
		{
			//enables toolbar controls selectively
			Strudel.winMain.toolbar.enableControls(false);
			//enable the comparative mode controls in the results table's control panel
			Strudel.winMain.foundFeaturesTableControlPanel.getFilterLabel().setEnabled(true);
			Strudel.winMain.foundFeaturesTableControlPanel.getGenomeFilterCombo().setEnabled(true);
			Strudel.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().setEnabled(true);

			// also need a new link display manager because it holds the precomputed links
			WinMain.mainCanvas.linkDisplayManager = new LinkDisplayManager();
		}

		// hide the start panel if it is still showing
		Strudel.winMain.showStartPanel(false);

		//clear the results table, in case we already had data loaded
		Strudel.winMain.fatController.clearResultsTable();

		// revalidate the GUI
		Strudel.winMain.validate();

		//repaint the main canvas
		WinMain.mainCanvas.updateCanvas(true);

		// bring the focus back on the main window -- need this in case we had an overview dialog open (which then gets focus)
		Strudel.winMain.requestFocus();

		Strudel.winMain.fatController.recentFileLoad = false;
		
		return true;
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	public static int calculateNumberOfSupportedMaps()
	{
		// the total amount of space we have for drawing on vertically, in pixels
		int availableSpaceVertically = Strudel.winMain.getHeight() - (MainCanvas.chromoSpacing * 2);
		
		return (availableSpaceVertically / (minimumChromosomeHeight  + MainCanvas.chromoSpacing));
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	private static boolean checkMapNumbers()
	{
		boolean mapNumbersOK = true;

		// the total amount of space we have for drawing on vertically, in pixels
		int availableSpaceVertically = Strudel.winMain.getHeight() - (MainCanvas.chromoSpacing * 2);
		// the combined height of all the vertical spaces between chromosomes
		int allSpacers = MainCanvas.chromoSpacing * (Strudel.winMain.dataSet.maxChromos - 1);
		
		//for all mapsets
		for (GMapSet gMapSet : Strudel.winMain.dataSet.gMapSets)
		{
			// the height of a chromosome
			gMapSet.chromoHeight = (availableSpaceVertically - allSpacers) / Strudel.winMain.dataSet.maxChromos;
			
			//here we need to check whether this is smaller than a minimum chromosome height we defined
			//if it is, we do not support the data because it has too many chromsomes
			//need to display an error message to that effect
			if(gMapSet.chromoHeight < minimumChromosomeHeight)	
				mapNumbersOK = false;
		}
		
		return mapNumbersOK;
	}


	// ----------------------------------------------------------------------------------------------------------------------------------------------

	public static String getUserInputFile()
	{
		MTOpenFilesPanel openFilesPanel = Strudel.winMain.openFileDialog.openFilesPanel;

		String inputFileName = null;

		//for each file, check whether we have a file chosen by the user -- if not, the respective
		//text field should be empty
		if(!openFilesPanel.getInputFileTF().getText().equals(""))
			inputFileName = openFilesPanel.getInputFileTF().getText();

		//check whether user has specified files correctly
		//missing target data file
		if(inputFileName == null && Strudel.winMain.fatController.loadOwnData)
		{
			String errorMessage = "The input data file has not been specified. Please try again.";
			TaskDialog.error(errorMessage, "Close");
		}

		return inputFileName;
	}
}
