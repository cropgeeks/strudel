package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class FoundFeaturesResultsPanel extends JPanel implements ListSelectionListener
{
	GChromoMap previousMap = null;
	
	HomologResultsTable resultsTable = null;
	JLabel resultsLabel = null;
	
	/** Creates new form MTFindFeaturesResultsPanel */
	public FoundFeaturesResultsPanel()
	{
		super(new BorderLayout());
		
		resultsLabel = new JLabel("<html><b>Click on row to highlight homolog. Click on homolog name to show annotation in web browser.</b></html>");
		resultsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(resultsLabel, BorderLayout.NORTH);
		
		resultsTable = new HomologResultsTable();
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		resultsTable.setFillsViewportHeight(true);
		this.add(scrollPane, BorderLayout.CENTER);
		
		// settings for results table
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(this);
	}
	
	public JTable getFFResultsTable()
	{
		return resultsTable;
	}
	
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
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
		{
			return;
		}
		
		try
		{
			int selectedRow = resultsTable.getSelectionModel().getLeadSelectionIndex();
			int selectedCol = resultsTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
			
			if (resultsTable.getModel().getColumnCount() > 0)
			{
				// get the feature name
				String featureName = (String) resultsTable.getModel().getValueAt(
								resultsTable.getSelectedRow(), 0);
				// retrieve the Feature that corresponds to this name
				Feature f = Utils.getFeatureByName(featureName);
				// highlight it on the canvas
				MapViewer.winMain.fatController.highlightRequestedFeature(f);
				
				// which map and mapset are we dealing with here
				GMapSet owningSet = f.getOwningMap().getGChromoMap().owningSet;
				GChromoMap gChromoMap = f.getOwningMap().getGChromoMap();
				
				// we have changed map
				if (previousMap != null && !previousMap.equals(gChromoMap))
				{
					// zoom out first
					// owningSet.zoomFactor = 1;
					// owningSet.paintAllMarkers = false;
					// MapViewer.winMain.mainCanvas.updateCanvas(true);
				}
				
				// zoom into that chromosome so it fills the screen
				if (owningSet.zoomFactor < owningSet.singleChromoViewZoomFactor || !gChromoMap.isShowingOnCanvas)
				{
					// zoom into the map
					MapViewer.winMain.mainCanvas.zoomHandler.processClickZoomRequest(gChromoMap);
				}
				
				// remember this map
				previousMap = gChromoMap;
				
				// user has clicked on homolog name -- fire up web browser with annotation info
				if (selectedCol == 3)
				{
					// extract the value of the cell clicked on
					String homologName = (String) resultsTable.getModel().getValueAt(selectedRow, selectedCol);
					
					String url = Prefs.refGenome2BaseURL + homologName;
					Desktop desktop = null;
					if (Desktop.isDesktopSupported()) 
						desktop = Desktop.getDesktop();
					
					if (desktop != null &&  desktop.isSupported(Desktop.Action.BROWSE))
						desktop.browse(new URI(url));	
				}
			}
			
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public JLabel getResultsLabel()
	{
		return resultsLabel;
	}
	
}
