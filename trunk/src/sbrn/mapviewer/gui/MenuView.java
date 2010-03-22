package sbrn.mapviewer.gui;

import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.components.WinMain;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * View menu is interacted with.
 */
public class MenuView
{
	private WinMain winMain;

	public MenuView(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void showOverview()
	{
		// Toggle the state
		Prefs.guiOverviewVisible = !Prefs.guiOverviewVisible;

		// Then set the toolbar button and dialog to match
		winMain.toolbar.bOverview.setSelected(Prefs.guiOverviewVisible);
		winMain.overviewDialog.setVisible(Prefs.guiOverviewVisible);
	}

	public void customiseColours()
	{
		winMain.colorChooserDialog.setLocationRelativeTo(Strudel.winMain);
		winMain.colorChooserDialog.setVisible(true);
	}

	public void showHint()
	{
		Prefs.showHintPanel = !Prefs.showHintPanel;
		if (Prefs.showHintPanel)
			winMain.hintPanel.setVisible(true);
		else
			winMain.hintPanel.setVisible(false);
		winMain.mainCanvas.updateCanvas(true);

		winMain.menuBar.getMShowHint().setSelected(Prefs.showHintPanel);
		Strudel.winMain.configureViewSettingsDialog.viewSettingsPanel.getHintPanelCheckBox().setSelected(Prefs.showHintPanel);
	}

	public void antialiasedDraw()
	{
		Prefs.userPrefAntialias = !Prefs.userPrefAntialias;
		winMain.menuBar.getMAntialiasedDraw().setSelected(Prefs.userPrefAntialias);
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void filterLinks()
	{
		Prefs.drawOnlyLinksToVisibleFeatures = !Prefs.drawOnlyLinksToVisibleFeatures;
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void showDistanceMarkers()
	{
		Prefs.showDistanceMarkers = !Prefs.showDistanceMarkers;
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void linkShape(int shape)
	{
	    Prefs.linkShape = shape;
	    Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void configureDatasets()
	{
		Strudel.winMain.genomeLayoutDialog.setLocationRelativeTo(Strudel.winMain);
		Strudel.winMain.genomeLayoutDialog.setVisible(true);
	}

	public void reset()
	{
		winMain.fatController.resetMainCanvasView();
	}
}
