package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.font.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class HomologResultsTable extends JTable
{
	
	//===============================================vars=========================================		
	
	HyperlinkCellRenderer hyperlinkCellRenderer = new HyperlinkCellRenderer();
	
	//===============================================c'tor=========================================	
	
	public HomologResultsTable()
	{
		//configure table for selections
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//add listeners
		getSelectionModel().addListSelectionListener(new RowListener());
		getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());		
	}
	
	//===============================================methods=========================================	
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if (column == 3)
		{
			return hyperlinkCellRenderer;
		}
		
		return super.getCellRenderer(row, column);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//fire up web browser with annotation info	
	private void launchBrowser(int selectedRow, int selectedCol)
	{
		if (!MapViewer.winMain.ffResultsPanel.isFilterEvent)
		{
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel) getModel();
			
			//get the index of the selected row but check for changes due to filtering
			int modelRow = -1;
			if (getSelectedRow() >= 0)
			{
				modelRow = convertRowIndexToModel(getSelectedRow());
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
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//===============================================inner classes=========================================
	
	class HyperlinkCellRenderer extends JLabel implements TableCellRenderer
	{
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			// get the index of the selected row but check for changes due to filtering
			int modelRow = table.convertRowIndexToModel(row);
			
			// set the font up so it's blue and underlined to make it look like a hyperlink
			Map<TextAttribute, Integer> attributes = new Hashtable<TextAttribute, Integer>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			Font font = new Font(attributes);
			setFont(font);
			setForeground(Color.blue);
			
			// this is what we want to print to the cell
			setText((String) table.getModel().getValueAt(modelRow, column));
			
			// selection colors etc
			if (isSelected)
			{
				setBackground(table.getSelectionBackground());
				setOpaque(true);
			}
			else
			{
				setOpaque(false);
			}
			
			
			return this;
		}
		
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private class RowListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent event)
		{
			if (event.getValueIsAdjusting())
			{
				return;
			}
			
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel)getModel();			
			if (getSelectedColumn() == foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologColumnLabel))
			{
				// user has clicked on homolog name -- fire up web browser with annotation info	
				launchBrowser(getSelectedRow(), getSelectedColumn());
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private class ColumnListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent event)
		{
			if (event.getValueIsAdjusting())
			{
				return;
			}
			
			FoundFeatureTableModel foundFeatureTableModel = (FoundFeatureTableModel)getModel();			
			if (getSelectedColumn() == foundFeatureTableModel.columnNameList.indexOf(foundFeatureTableModel.homologColumnLabel))
			{
				// user has clicked on homolog name -- fire up web browser with annotation info	
				launchBrowser(getSelectedRow(), getSelectedColumn());
			}
		}
	}
	

	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
}
