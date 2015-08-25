package sbrn.mapviewer.gui;

import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.dialog.FindFeaturesInRangeDialog;
import sbrn.mapviewer.gui.handlers.FeatureSearchHandler;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * Explore menu is interacted with.
 */
public class MenuExplore
{
	public void showTable()
	{

		//if we don't have features to show from a name or egion based search we do not want the checkboxes enabled
		if (!Strudel.winMain.mainCanvas.drawFoundFeaturesInRange)
		{
			Strudel.winMain.foundFeaturesTableControlPanel.getShowLabelsCheckbox().setEnabled(false);
			Strudel.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().setEnabled(false);
			Strudel.winMain.foundFeaturesTableControlPanel.getHighlightWhiteCheckbox().setEnabled(false);
		}
		
		//reset the main canvas
		Strudel.winMain.fatController.resetMainCanvasView();
		
		//display all the features
		FeatureSearchHandler.findAllFeatures();
	}

	public void exploreRange()
	{
		//clear the dialog
		FindFeaturesInRangeDialog featuresInRangeDialog = Strudel.winMain.ffInRangeDialog;

		featuresInRangeDialog.ffInRangePanel.getRangeStartSpinner().setValue(0);
		featuresInRangeDialog.ffInRangePanel.getRangeEndSpinner().setValue(0);
		featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(0);
		featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(0);

		//reset the main canvas
		Strudel.winMain.fatController.resetMainCanvasView();

		//show the dialog
		featuresInRangeDialog.setLocationRelativeTo(Strudel.winMain);
		featuresInRangeDialog.setVisible(true);
	}
}
