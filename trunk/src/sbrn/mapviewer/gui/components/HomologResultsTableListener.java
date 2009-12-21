package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

public class HomologResultsTableListener implements ListSelectionListener, MouseMotionListener, MouseListener
{

	ResultsTable resultsTable;
	Point point = new Point();
	boolean isMouseClick = false;


	public HomologResultsTableListener(ResultsTable resultsTable)
	{
		super();
		this.resultsTable = resultsTable;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
		{
			return;
		}

		if (!resultsTable.isFilterEvent)
		{
			highlightFeature();
		}
		resultsTable.isFilterEvent = false;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mouseClicked(MouseEvent e)
	{
		// check whether we are in any of the cells containing hyperlinks
		point.setLocation(e.getX(), e.getY());
		int col = resultsTable.columnAtPoint(point);
		int row = resultsTable.rowAtPoint(point);

		// if we are, fire up a web browser with a query for the value in the cell we clicked on
		HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
		if (col == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) ||
						col == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel))
		{
			launchBrowser(row, col);
		}
	}


	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void mouseMoved(MouseEvent e)
	{
		// check whether we are in any of the cells containing hyperlinks
		point.setLocation(e.getX(), e.getY());
		int row = resultsTable.rowAtPoint(point);
		int col = resultsTable.columnAtPoint(point);

		// if we are, change the cursor to a hand
		HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
		boolean isURLColumn = col == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) ||
		col == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel);

		if (isURLColumn && resultsTable.cellHasURLSet(row, col))
		{
			resultsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else
		{
			resultsTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}


	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// fire up web browser with annotation info
	private void launchBrowser(int selectedRow, int selectedCol)
	{

		if (!resultsTable.isFilterEvent)
		{
			HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();

			// get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (selectedRow >= 0)
			{
				modelRow = resultsTable.convertRowIndexToModel(resultsTable.getSelectedRow());
			}
			else
			{
				return;
			}

			// extract the value of the cell clicked on
			String featureName = (String) homologResultsTableModel.getValueAt(modelRow, selectedCol);
			Feature feature = Utils.getFeatureByName(featureName);
			MapSet 	mapSet = feature.getOwningMapSet();

			if(mapSet.getURL() != null)
				Utils.visitURL(mapSet.getURL() + feature.getName());
		}

		resultsTable.isFilterEvent = false;
	}



// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

private void highlightFeature()
{
	HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
	// get the index of the selected row but check for changes due to filtering
	int modelRow = -1;
	if (resultsTable.getSelectedRow() >= 0)
	{
		modelRow = resultsTable.convertRowIndexToModel(resultsTable.getSelectedRow());
	}
	else
	{
		return;
	}
	if (homologResultsTableModel.getColumnCount() > 0)
	{
		//retrieve the feature that was clicked on, and its gmapset
		Feature targetFeature = homologResultsTableModel.tableEntries.get(modelRow).getTargetFeature();
		//find out whether we have a homolog for this feature
		Feature homolog = homologResultsTableModel.tableEntries.get(modelRow).getHomologFeature();

		//if we got here because we requested features through the find features by name dialog then we
		//want to zoom out fully so we can see them in the broadest possible context
		//if we got here through a range request we do nothing
		if(Strudel.winMain.fatController.findFeaturesRequested)
		{
			Strudel.winMain.mainCanvas.zoomHandler.processZoomResetRequest(targetFeature.getOwningMap().getGChromoMap().owningSet);
			if(homolog != null)
				Strudel.winMain.mainCanvas.zoomHandler.processZoomResetRequest(homolog.getOwningMap().getGChromoMap().owningSet);
		}

		// highlight the feature on the canvas
		Strudel.winMain.fatController.highlightFeature = targetFeature;
		if(homolog != null)
			Strudel.winMain.fatController.highlightFeatureHomolog = homolog;
		else
			Strudel.winMain.fatController.highlightFeatureHomolog = null;
		Strudel.winMain.mainCanvas.drawHighlightFeatures = true;

		//update the canvas
		Strudel.winMain.mainCanvas.updateCanvas(false);
	}
}

// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


public void mouseEntered(MouseEvent e)
{
}

public void mouseExited(MouseEvent e)
{
}

public void mousePressed(MouseEvent e)
{
}

public void mouseReleased(MouseEvent e)
{
}

public void mouseDragged(MouseEvent e)
{
}

// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
