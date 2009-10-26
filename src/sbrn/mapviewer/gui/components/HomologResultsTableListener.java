package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
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
		MapViewer.logger.fine("=========valueChanged");
		
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
		int col = resultsTable.columnAtPoint(point);
		
		// if we are, change the cursor to a hand
		HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) resultsTable.getModel();
		if (col == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) ||
						col == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel))
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
			
			String url = "";
			// extract the value of the cell clicked on
			String featureName = (String) homologResultsTableModel.getValueAt(modelRow, selectedCol);
			
			if (selectedCol == homologResultsTableModel.findColumn(homologResultsTableModel.homologColumnLabel) || 
							selectedCol == homologResultsTableModel.findColumn(homologResultsTableModel.targetNameColumnLabel))
			{
				String mapSetName = (String) homologResultsTableModel.getValueAt(
								modelRow,
								homologResultsTableModel.columnNameList.indexOf(homologResultsTableModel.homologGenomeColumnLabel));
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
		MapViewer.logger.fine("highlightFeature()");
		
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
			// get the feature name
			int feature1NameColumnIndex = homologResultsTableModel.columnNameList.indexOf(homologResultsTableModel.targetNameColumnLabel);
			int feature2NameColumnIndex = homologResultsTableModel.columnNameList.indexOf(homologResultsTableModel.homologColumnLabel);
			String feature1Name = (String) homologResultsTableModel.getValueAt(modelRow,feature1NameColumnIndex);
			String feature2Name = (String) homologResultsTableModel.getValueAt(modelRow,feature2NameColumnIndex);
			// if this fails we may have a feature in the table where there is no target value but only a homolog
			// this happens when we have inserted additional loci from a reference genome into the tabel that have no known equivalent in the target genome
			// in that case try the other feature's name
			if (feature1Name == null)
				feature1Name = (String) homologResultsTableModel.getValueAt(modelRow,
								feature2NameColumnIndex);
			
			// retrieve the Feature that corresponds to this name
			Feature f1 = Utils.getFeatureByName(feature1Name);
			Feature f2 = Utils.getFeatureByName(feature2Name);
						
			// which map and mapset are we dealing with here
			GMapSet owningSet1 = f1.getOwningMap().getGChromoMap().owningSet;
			GMapSet owningSet2 = f2.getOwningMap().getGChromoMap().owningSet;
			
			//if we got here because we requested features through the find features by name dialog then we
			//want to zoom out fully so we can see them in the broadest possible context
			//if we got here through a range request we do nothing
			if(MapViewer.winMain.fatController.findFeaturesRequested)
			{
				MapViewer.winMain.mainCanvas.zoomHandler.processZoomResetRequest(owningSet1);
				MapViewer.winMain.mainCanvas.zoomHandler.processZoomResetRequest(owningSet2);
			}

			// highlight the feature on the canvas
			MapViewer.winMain.fatController.highlightFeature = f1;
			MapViewer.winMain.fatController.highlightFeatureHomolog = f2;	
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
