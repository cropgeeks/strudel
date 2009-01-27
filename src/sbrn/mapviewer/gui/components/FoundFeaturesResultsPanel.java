package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class FoundFeaturesResultsPanel extends JPanel implements ListSelectionListener
{
	//===========================================vars===========================================	
	
	GChromoMap previousMap = null;
	
	HomologResultsTable resultsTable = null;
	JLabel resultsLabel = null;
	
	public boolean isFilterEvent = false;
	
	//===========================================c'tor===========================================		
	
	/** Creates new form MTFindFeaturesResultsPanel */
	public FoundFeaturesResultsPanel()
	{
		super(new BorderLayout());
		
		String title = "Click on a row to highlight a homolog. Click on a homolog name to show annotation in a web browser: ";
		setBorder(BorderFactory.createTitledBorder(title));
		
		resultsTable = new HomologResultsTable();
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		this.add(scrollPane, BorderLayout.CENTER);
		
		//set the table up for sorting
		resultsTable.setAutoCreateRowSorter(true);
		
		// settings for results table
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(this);
	}
	
	//===========================================methods===========================================		
	
	public JTable getFFResultsTable()
	{
		return resultsTable;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public int getTotalTableWidth()
	{
		int width = 0;
		TableColumn column = null;
		for (int i = 0; i < resultsTable.getColumnModel().getColumnCount(); i++)
		{
			column = resultsTable.getColumnModel().getColumn(i);
			width += column.getPreferredWidth();
		}
		return width;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void initColumnSizes()
	{
		TableColumn column = null;
		for (int i = 0; i < resultsTable.getColumnModel().getColumnCount(); i++)
		{
			// this is the maxWidth for entire column, header included
			int maxWidth = 0;
			
			// get the font metrics for this table
			FontMetrics fm = resultsTable.getFontMetrics(resultsTable.getFont());
			
			// get the string width for the data header for this column
			int headerWidth = fm.stringWidth(resultsTable.getColumnName(i));
			if (headerWidth > maxWidth)
				maxWidth = headerWidth;
			
			// get the data in this column and check their width
			for (int j = 0; j < resultsTable.getModel().getRowCount(); j++)
			{
				String cellContent = resultsTable.getModel().getValueAt(j, i).toString();
				int cellWidth = fm.stringWidth(cellContent);
				if (cellWidth > maxWidth)
					maxWidth = cellWidth;
			}
			column = resultsTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(maxWidth);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
		{
			return;
		}
		
		if (!isFilterEvent)
		{
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) resultsTable.getModel();
			//get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (resultsTable.getSelectedRow() >= 0)
			{
				modelRow = resultsTable.convertRowIndexToModel(resultsTable.getSelectedRow());
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
				String featureName = (String) foundFeatureTableModel.getValueAt(modelRow, feature1NameColumnIndex);
				//if this fails we may have a feature in the table where there is no target value but only a homolog
				//this happens when we have inserted additional loci from a reference genome into the tabel that have no known equivalent in the target genome
				//in that case try the other feature's name
				if(featureName == null)
					featureName = (String) foundFeatureTableModel.getValueAt(modelRow, feature2NameColumnIndex);
				
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
				
				// remember this map
				previousMap = gChromoMap;
				
				if (resultsTable.getSelectedColumn() == foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologColumnLabel))
				{
					// user has clicked on homolog name -- fire up web browser with annotation info	
					launchBrowser(resultsTable.getSelectedRow(), resultsTable.getSelectedColumn());
				}
			}
		}
		isFilterEvent = false;		
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public JLabel getResultsLabel()
	{
		return resultsLabel;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//applies a regular epxression based filter to the results table
	@SuppressWarnings({"unchecked"})
	public void newFilter(String filterExpression, int index)
	{
		isFilterEvent = true;
		
		RowFilter<TableModel, Object> rf = null;
		String expr = "^" + filterExpression;
		
		try
		{
			rf = RowFilter.regexFilter(expr, index);
		}
		catch (java.util.regex.PatternSyntaxException e)
		{
			return;
		}
		
		if(filterExpression.equals("<none>"))
			((DefaultRowSorter<TableModel, Integer>) resultsTable.getRowSorter()).setRowFilter(null);
		else
			((DefaultRowSorter<TableModel, Integer>) resultsTable.getRowSorter()).setRowFilter(rf);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//fire up web browser with annotation info	
	private void launchBrowser(int selectedRow, int selectedCol)
	{
		if (!MapViewer.winMain.ffResultsPanel.isFilterEvent)
		{
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) resultsTable.getModel();
			
			//get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (resultsTable.getSelectedRow() >= 0)
			{
				modelRow = resultsTable.convertRowIndexToModel(resultsTable.getSelectedRow());
			}
			else
			{
				return;
			}
			
			// extract the value of the cell clicked on
			String homologName = (String) foundFeatureTableModel.getValueAt(modelRow, selectedCol);
			String mapSetName = (String) foundFeatureTableModel.getValueAt(modelRow, foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologGenomeColumnLabel));
			//figure out the URL we need to prefix this with
			String url = "";
			//find out the index of the mapset
			int mapSetIndex = MapViewer.winMain.dataContainer.referenceGMapSets.indexOf(Utils.getGMapSetByName(mapSetName));
			//for the canned example data that ship with the application we use this
			if (!MapViewer.winMain.fatController.loadOwnData)
			{
				if (mapSetIndex == 0)
					url = Constants.exampleRefGenome1BaseURL + homologName;
				else if (mapSetIndex == 1)
					url = Constants.exampleRefGenome2BaseURL + homologName;
			}
			//for the users own data we use these URLs
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
		
		MapViewer.winMain.ffResultsPanel.isFilterEvent = false;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}//end class
