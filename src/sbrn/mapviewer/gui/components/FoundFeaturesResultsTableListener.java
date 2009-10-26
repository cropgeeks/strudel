package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class FoundFeaturesResultsTableListener implements ListSelectionListener, MouseMotionListener, MouseListener
{
	
	ResultsTable resultsTable;
	Point point = new Point();	
	boolean isMouseClick = false;

	public FoundFeaturesResultsTableListener(ResultsTable resultsTable)
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
		
		if (!resultsTable.isFilterEvent && resultsTable.getSelectedRow() >= 0)
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
		LinklessFeatureTableModel linklessFeatureTableModel = (LinklessFeatureTableModel) resultsTable.getModel();
		if (col == linklessFeatureTableModel.findColumn(linklessFeatureTableModel.featureNameColumnLabel))
		{
			launchBrowser(row, col);
		}
	}
	
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseMoved(MouseEvent e)
	{
		// check whether we are in any of the cells containing hyperlinks
		point.setLocation(e.getX(), e.getY());
		int col = resultsTable.columnAtPoint(point);
		
		// if we are, change the cursor to a hand
		LinklessFeatureTableModel linklessFeatureTableModel = (LinklessFeatureTableModel) resultsTable.getModel();
		if (col == linklessFeatureTableModel.findColumn(linklessFeatureTableModel.featureNameColumnLabel))
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
			LinklessFeatureTableModel linklessFeatureTableModel = (LinklessFeatureTableModel) resultsTable.getModel();
			
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
			
			String url = "";
			// extract the value of the cell clicked on
			String featureName = (String) linklessFeatureTableModel.getValueAt(modelRow, selectedCol);
			
			if (selectedCol == linklessFeatureTableModel.findColumn(linklessFeatureTableModel.featureNameColumnLabel))
			{	
				//if we have an instance of this model this implies we only have a single genome loaded
				String mapSetName = MapViewer.winMain.dataContainer.allMapSets.get(0).getName();
				// figure out the URL we need to prefix this with				
				url = Utils.getMapSetByName(mapSetName).getURL();
			}

			Utils.visitURL(url);
		}
		
		resultsTable.isFilterEvent = false;
	}
	
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void highlightFeature()
	{	
		LinklessFeatureTableModel linklessFeatureTableModel = (LinklessFeatureTableModel) resultsTable.getModel();
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
		if (linklessFeatureTableModel.getColumnCount() > 0)
		{
			// get the feature name
			int featureNameColumnIndex = linklessFeatureTableModel.columnNameList.indexOf(linklessFeatureTableModel.featureNameColumnLabel);
			String featureName = (String) linklessFeatureTableModel.getValueAt(modelRow,featureNameColumnIndex);
			
			// retrieve the Feature that corresponds to this name
			Feature f = Utils.getFeatureByName(featureName);
			
			// which map and mapset are we dealing with here
			GMapSet owningSet = f.getOwningMap().getGChromoMap().owningSet;
			
			//if we got here because we requested features through the find features by name dialog then we
			//want to zoom out fully so we can see them in the broadest possible context
			//if we got here through a range request we do nothing
			if(MapViewer.winMain.fatController.findFeaturesRequested)
			{
				MapViewer.winMain.mainCanvas.zoomHandler.processZoomResetRequest(owningSet);
			}
			
			// highlight the feature on the canvas
			MapViewer.winMain.fatController.highlightFeature = f;
			MapViewer.winMain.mainCanvas.drawHighlightFeatures = true;
			
			//update the canvas
			MapViewer.winMain.mainCanvas.updateCanvas(true);	
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
