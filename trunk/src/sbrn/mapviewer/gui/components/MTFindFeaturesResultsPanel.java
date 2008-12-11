/*
 * MTFindFeaturesResultsPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.components;

import java.awt.*;

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
	
	/** Creates new form MTFindFeaturesResultsPanel */
	public MTFindFeaturesResultsPanel()
	{
		initComponents();
		
		//settings for results table
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getSelectionModel().addListSelectionListener(this);
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		resultsLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		resultsTable = new javax.swing.JTable();
		
		resultsLabel.setText("<html>Click on row to highlight feature and its homologs:</html>");
		
		jScrollPane1.setBorder(null);
		
		resultsTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]
		{
		{ null, null, null, null },
		{ null, null, null, null },
		{ null, null, null, null },
		{ null, null, null, null } }, new String[]
		{ "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(resultsTable);
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														layout.createSequentialGroup().add(
																		jScrollPane1,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		772,
																		Short.MAX_VALUE).addContainerGap()).add(
														layout.createSequentialGroup().add(
																		resultsLabel,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		731,
																		Short.MAX_VALUE).add(
																		51,
																		51,
																		51)))));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(resultsLabel).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										93, Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel resultsLabel;
	private javax.swing.JTable resultsTable;
	
	// End of variables declaration//GEN-END:variables
	
	public JTable getFFResultsTable()
	{
		return resultsTable;
	}
	
	public void setFFResultsTable(JTable table)
	{
		resultsTable = table;
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
		
		//get the feature name
		String featureName = (String) resultsTable.getModel().getValueAt(resultsTable.getSelectedRow(), 0);
		
		//retrieve the Feature that corresponds to this name
		Feature f = Utils.getFeatureByName(featureName);
		
		//highlight it on the canvas
		MapViewer.winMain.fatController.highlightRequestedFeature(f);
		
		//zoom into that chromosome so it fills the screen
		GMapSet owningSet = f.getOwningMap().getGChromoMap().owningSet;
		GChromoMap gChromoMap = f.getOwningMap().getGChromoMap();
		if (owningSet.zoomFactor < owningSet.singleChromoViewZoomFactor || !gChromoMap.isShowingOnCanvas)
		{
			//zoom into the map
			MapViewer.winMain.mainCanvas.zoomHandler.processClickZoomRequest(gChromoMap, 1000);
		}
		
		previousMap = gChromoMap;
	}
	
	public JLabel getResultsLabel()
	{
		return resultsLabel;
	}
	
}
