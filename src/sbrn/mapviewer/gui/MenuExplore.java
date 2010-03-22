package sbrn.mapviewer.gui;

import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.dialog.FindFeaturesInRangeDialog;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * Explore menu is interacted with.
 */
public class MenuExplore
{
	public void findFeature()
	{
		//reset the main canvas
		Strudel.winMain.fatController.resetMainCanvasView();
		Strudel.winMain.fatController.findFeaturesRequested = true;

		//clear the find dialog
		Strudel.winMain.ffDialog.ffPanel.getFFTextArea().setText("");

		//show the find dialog
		Strudel.winMain.ffDialog.setLocationRelativeTo(Strudel.winMain);
		Strudel.winMain.ffDialog.setVisible(true);
		Strudel.winMain.ffDialog.ffPanel.getFFTextArea().requestFocusInWindow();
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
