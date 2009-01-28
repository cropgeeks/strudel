package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.event.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class HomologResultsTableListener implements ListSelectionListener, MouseMotionListener, MouseListener
{

	HomologResultsTable homologResultsTable;
	Point point = new Point();	
	
	public HomologResultsTableListener(HomologResultsTable homologResultsTable)
	{
		super();
		this.homologResultsTable = homologResultsTable;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
		{
			return;
		}
		
		if (!homologResultsTable.isFilterEvent)
		{
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) homologResultsTable.getModel();
			// get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (homologResultsTable.getSelectedRow() >= 0)
			{
				modelRow = homologResultsTable.convertRowIndexToModel(homologResultsTable.getSelectedRow());
			}
			else
			{
				return;
			}
			if (foundFeatureTableModel.getColumnCount() > 0)
			{
				// get the feature name
				int feature1NameColumnIndex = foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.targetNameColumnLabel);
				int feature2NameColumnIndex = foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologColumnLabel);
				String featureName = (String) foundFeatureTableModel.getValueAt(modelRow,
								feature1NameColumnIndex);
				// if this fails we may have a feature in the table where there is no target value but only a homolog
				// this happens when we have inserted additional loci from a reference genome into the tabel that have no known equivalent in the target genome
				// in that case try the other feature's name
				if (featureName == null)
					featureName = (String) foundFeatureTableModel.getValueAt(modelRow,
									feature2NameColumnIndex);
				
				// retrieve the Feature that corresponds to this name
				Feature f = Utils.getFeatureByName(featureName);
				// highlight it on the canvas
				MapViewer.winMain.fatController.highlightRequestedFeature(f);
				
				// which map and mapset are we dealing with here
				GMapSet owningSet = f.getOwningMap().getGChromoMap().owningSet;
				GChromoMap gChromoMap = f.getOwningMap().getGChromoMap();
				
				// we have changed map
				// zoom into that chromosome so it fills the screen
				if (owningSet.zoomFactor < owningSet.singleChromoViewZoomFactor || !gChromoMap.isShowingOnCanvas)
				{
					// zoom into the map
					MapViewer.winMain.mainCanvas.zoomHandler.processClickZoomRequest(gChromoMap);
				}
			}
		}
		homologResultsTable.isFilterEvent = false;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		// check whether we are in any of the cells containing hyperlinks
		point.setLocation(e.getX(), e.getY());
		int col = homologResultsTable.columnAtPoint(point);
		int row = homologResultsTable.rowAtPoint(point);
		
		// if we are, fire up a web browser with a query for the value in the cell we clicked on
		FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) homologResultsTable.getModel();
		if (col == foundFeatureTableModel.findColumn(foundFeatureTableModel.homologColumnLabel))
		{
			launchBrowser(row, col);
		}
		
	}
	
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
	
	public void mouseMoved(MouseEvent e)
	{
		// check whether we are in any of the cells containing hyperlinks
		point.setLocation(e.getX(), e.getY());
		int col = homologResultsTable.columnAtPoint(point);
		
		// if we are, change the cursor to a hand
		FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) homologResultsTable.getModel();
		if (col == foundFeatureTableModel.findColumn(foundFeatureTableModel.homologColumnLabel))
		{
			homologResultsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else
		{
			homologResultsTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	// fire up web browser with annotation info
	private void launchBrowser(int selectedRow, int selectedCol)
	{
		if (!homologResultsTable.isFilterEvent)
		{
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) homologResultsTable.getModel();
			
			// get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (homologResultsTable.getSelectedRow() >= 0)
			{
				modelRow = homologResultsTable.convertRowIndexToModel(homologResultsTable.getSelectedRow());
			}
			else
			{
				return;
			}
			
			// extract the value of the cell clicked on
			String homologName = (String) foundFeatureTableModel.getValueAt(modelRow, selectedCol);
			String mapSetName = (String) foundFeatureTableModel.getValueAt(
							modelRow,
							foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologGenomeColumnLabel));
			// figure out the URL we need to prefix this with
			String url = "";
			// find out the index of the mapset
			int mapSetIndex = MapViewer.winMain.dataContainer.referenceGMapSets.indexOf(Utils.getGMapSetByName(mapSetName));
			// for the canned example data that ship with the application we use this
			if (!MapViewer.winMain.fatController.loadOwnData)
			{
				if (mapSetIndex == 0)
					url = Constants.exampleRefGenome1BaseURL + homologName;
				else if (mapSetIndex == 1)
					url = Constants.exampleRefGenome2BaseURL + homologName;
			}
			// for the users own data we use these URLs
			else
			{
				if (mapSetIndex == 0)
					url = MapViewer.winMain.openFileDialog.openFilesPanel.getRefGenome1UrlTf().getText() + homologName;
				else if (mapSetIndex == 1)
					url = MapViewer.winMain.openFileDialog.openFilesPanel.getRefGenome2UrlTf().getText() + homologName;
			}
			Desktop desktop = null;
			if (Desktop.isDesktopSupported())
				desktop = Desktop.getDesktop();
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			{
				try
				{
					desktop.browse(new URI(url));
				}
				catch (java.net.URISyntaxException e1)
				{
					TaskDialog.error("Error: URL not specified or specified incorrectly", "Close");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		homologResultsTable.isFilterEvent = false;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
}
