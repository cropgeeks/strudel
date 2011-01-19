package sbrn.mapviewer.gui;

import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.components.*;
import scri.commons.gui.*;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * View menu is interacted with.
 */
public class MenuView
{
	private final WinMain winMain;

	public MenuView(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void showOverview()
	{
		//only execute this when the overview dialog is being opened, rather than closed
		if (!winMain.overviewDialog.isVisible())
		{
			//check whether any of the overview canvases contain so many chromos that they have to be rendered as one
			//if yes, display an error message
			boolean errorMessage = false;
			for (OverviewCanvas overviewCanvas : winMain.overviewCanvases)
			{
				if (overviewCanvas.renderAsOneChromo)
					errorMessage = true;
			}
			if (errorMessage && Prefs.showRenderAsOneMessage)
			{
				TaskDialog.info("In the overview, one or more genomes have too many chromosomes to have them rendered individually."+
								"\nThese will be rendered as a single chromosome instead.", "Continue", winMain.toolbar.renderAsOneChromoCheckBox);
			}
		}

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

		Strudel.winMain.configureViewSettingsDialog.viewSettingsPanel.getHintPanelCheckBox().setSelected(Prefs.showHintPanel);
	}

	public void antialiasedDraw()
	{
		Prefs.userPrefAntialias = !Prefs.userPrefAntialias;
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

	public void hideUnlinkedFeatures()
	{
		Prefs.hideUnlinkedFeatures = !Prefs.hideUnlinkedFeatures;
		winMain.fatController.initialisePositionArrays();
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void configureViewSettings()
	{
		Strudel.winMain.configureViewSettingsDialog.setLocationRelativeTo(Strudel.winMain);
		Strudel.winMain.configureViewSettingsDialog.setVisible(true);
	}

	public void scaleChromosomes()
	{
		Prefs.scaleChromosByRelativeSize = !Prefs.scaleChromosByRelativeSize;
		Strudel.winMain.fatController.resetMainCanvasView();
		Strudel.winMain.mainCanvas.initMapSets();
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
