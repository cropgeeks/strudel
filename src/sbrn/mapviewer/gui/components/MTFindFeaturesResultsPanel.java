/*
 * MTFindFeaturesResultsPanel.java
 *
 * Created on __DATE__, __TIME__
 */

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

/**
 *
 * @author  __USER__
 */
public class MTFindFeaturesResultsPanel extends javax.swing.JPanel implements ListSelectionListener
{
	
	GChromoMap previousMap = null;
	
	HomologResultsTable resultsTable = null;
	
	/** Creates new form MTFindFeaturesResultsPanel */
	public MTFindFeaturesResultsPanel()
	{
		initComponents();
		
		resultsTable = new HomologResultsTable();
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		resultsTable.setFillsViewportHeight(true);
		//		scrollPane.setPreferredSize(this.getPreferredSize());
		this.add(scrollPane, BorderLayout.CENTER);
		//		scrollPane.setVisible(true);
		
		//settings for results table
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(this);
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		resultsLabel = new javax.swing.JLabel();
		
		resultsLabel.setText("<html>Click on row to highlight feature and homologs. Click on homolog name link to open web browser with feature info.</html>");
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										resultsLabel,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										731, Short.MAX_VALUE).add(51, 51, 51)));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(resultsLabel).addContainerGap(
										231, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel resultsLabel;
	
	// End of variables declaration//GEN-END:variables
	
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
			//this is the maxWidth for entire column, header included
			int maxWidth = 0;
			
			//get the font metrics for this table
			FontMetrics fm = resultsTable.getFontMetrics(resultsTable.getFont());
			
			//get the string width for the data header for this column
			int headerWidth = fm.stringWidth(resultsTable.getColumnName(i));
			if (headerWidth > maxWidth)
				maxWidth = headerWidth;
			
			//get the  data in this column and check their width
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
		
		int selectedRow = resultsTable.getSelectionModel().getLeadSelectionIndex();
		int selectedCol = resultsTable.getColumnModel().getSelectionModel().getLeadSelectionIndex();
		System.out.println("selected cell (row, col): " + selectedRow + "," + selectedCol);
		
		//user has clicked on homolog name -- fire up web browser with annotation info
		if (selectedCol == 3)
		{
			//extract the value of the cell clicked on
			String homologName = (String) resultsTable.getModel().getValueAt(selectedRow, selectedCol);
			System.out.println("homologName selected = " + homologName);
			
			String url = Prefs.refGenome2BaseURL + homologName;
			Desktop desktop = Desktop.getDesktop();
			try
			{
				if (desktop != null)
					desktop.browse(new URI(url));
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (URISyntaxException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try
		{
			if (resultsTable.getModel().getColumnCount() > 0)
			{
				//get the feature name
				String featureName = (String) resultsTable.getModel().getValueAt(
								resultsTable.getSelectedRow(), 0);
				//retrieve the Feature that corresponds to this name
				Feature f = Utils.getFeatureByName(featureName);
				//highlight it on the canvas
				MapViewer.winMain.fatController.highlightRequestedFeature(f);
				
				//which map and mapset are we dealing with here
				GMapSet owningSet = f.getOwningMap().getGChromoMap().owningSet;
				GChromoMap gChromoMap = f.getOwningMap().getGChromoMap();
				
				//we have changed map
				if (previousMap != null && !previousMap.equals(gChromoMap))
				{
					//zoom out first
					//					owningSet.zoomFactor = 1;
					//					owningSet.paintAllMarkers = false;
					//					MapViewer.winMain.mainCanvas.updateCanvas(true);
				}
				
				//zoom into that chromosome so it fills the screen
				if (owningSet.zoomFactor < owningSet.singleChromoViewZoomFactor || !gChromoMap.isShowingOnCanvas)
				{
					//zoom into the map
					MapViewer.winMain.mainCanvas.zoomHandler.processClickZoomRequest(gChromoMap,
									1000);
				}
				
				//remember this map
				previousMap = gChromoMap;
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
