package sbrn.mapviewer.io;

import java.io.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.WinMain;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.handlers.LinkDisplayManager;
import scri.commons.gui.*;

public class DataLoadUtils
{

	public static void loadDataInThread(String inputFileName, boolean commandLineLoad)
	{
		//make sure we set this flag back to false if we are loading data on top of an existing set
		Strudel.winMain.fatController.mapSetsInited = false;

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

		SingleFileImporter singleFileImporter = new SingleFileImporter();
		StrudelFile file = new StrudelFile(inputFileName);
		singleFileImporter.setInput(file);

		ProgressDialog dialog = new ProgressDialog(singleFileImporter, "Data loading", "Data loading - please wait...");
		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{

				TaskDialog.error("Data load failed: " + dialog.getException().toString() + "\nPlease correct your data and try again.", "Close");
			}

			return;
		}
		else if(dialog.getResult() == ProgressDialog.JOB_COMPLETED)
			checkLoadCompleted(inputFileName);
	}

	private static void checkLoadCompleted(String inputFileName)
	{
		File inputFile = new File(inputFileName);
		//store the input file in the recent files list
		Prefs.setRecentDocument(inputFile.getAbsolutePath());

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
		Strudel.winMain.setTitle(inputFile.getName() + " -- Strudel " + Install4j.VERSION);

		// check if we need to enable some functionality -- depends on the number of genomes loaded
		// cannot do comparative stuff if user one loaded one (target) genome
		if (Strudel.winMain.dataContainer.gMapSets.size() == 1)
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
			WinMain.mainCanvas.linkDisplayManager = new LinkDisplayManager(WinMain.mainCanvas);
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
