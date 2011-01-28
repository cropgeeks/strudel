package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

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
			highlightSelectedFeaturesFromTable();
			//update the canvas
			Strudel.winMain.mainCanvas.updateCanvas(true);
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
		if (col == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) || col == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel))
		{
			//highlight the feature in the row we clicked on
			highlightSelectedFeaturesFromTable();
			//update the canvas
			Strudel.winMain.mainCanvas.updateCanvas(true);
			//launch the web browser
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
		
		// get the index of the selected row but check for changes due to filtering
		int modelRow = resultsTable.convertRowIndexToModel(row);

		// if we are, change the cursor to a hand
		HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
		boolean isURLColumn = col == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) ||
		col == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel);

		if (isURLColumn && resultsTable.cellHasURLSet(modelRow, col))
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
			if(feature == null)
				return;
			MapSet 	mapSet = feature.getOwningMapSet();

			if(mapSet.getURL() != null)
				Utils.visitURL(mapSet.getURL() + feature.getName());
		}

		resultsTable.isFilterEvent = false;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void clearTableSelection()
	{
		// clear the highlight features
		Strudel.winMain.fatController.clearHighlightFeatures();
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//this checks which rows are currently selected in our results table and then earmarks them for
	//being drawn as highlighted features with links and labels
	private void highlightSelectedFeaturesFromTable()
	{
		HomologResultsTableModel homologResultsTableModel = null;
		try
		{
			homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
		}
		catch (ClassCastException e){}
		
		//here we store the selected features
		LinkedList<ResultsTableEntry> selectedTableEntries = new LinkedList<ResultsTableEntry>();
		
		//get the table indices of the selected rows
		int [] selectedRows = resultsTable.getSelectedRows();
		for (int i = 0; i < selectedRows.length; i++)
		{
			// get the index of the selected row but check for changes due to filtering
			int modelRow = resultsTable.convertRowIndexToModel(selectedRows[i]);
			
			//retrieve the target feature
			ResultsTableEntry entry = homologResultsTableModel.tableEntries.get(modelRow);
			//add it to our vector
			selectedTableEntries.add(entry);
		}
		
		//now highlight the features we have selected
		Strudel.winMain.fatController.highlightedTableEntries = selectedTableEntries;	
		
//		System.out.println("currently selected features:");
//		for (ResultsTableEntry resultsTableEntry : selectedTableEntries)
//			System.out.println(resultsTableEntry.getTargetFeature().getName());
			
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
