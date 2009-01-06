/*
 * MTFindFeaturesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

/**
 *
 * @author  __USER__
 */
public class MTFindFeaturesPanel extends javax.swing.JPanel
{
	
	/** Creates new form MTFindFeaturesPanel */
	public MTFindFeaturesPanel()
	{
		initComponents();
		
		//set up the combo boxes with their data models
		Vector<String> genomes = new Vector<String>();
		for (GMapSet gMapSet : MapViewer.winMain.mainCanvas.gMapSetList)
		{
			genomes.add(gMapSet.name);
		}
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jLabel1 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		ffTextArea = new javax.swing.JTextArea();
		
		jLabel1.setText("<html>Enter one or more feature names (one per line) you would like to highlight:</html>");
		
		ffTextArea.setColumns(20);
		ffTextArea.setRows(1);
		ffTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
		jScrollPane1.setViewportView(ffTextArea);
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE).addContainerGap()).add(layout.createSequentialGroup().add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE).add(12, 12, 12)))));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jLabel1).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents

	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JTextArea ffTextArea;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	
	// End of variables declaration//GEN-END:variables
	
	public JTextArea getFFTextArea()
	{
		return ffTextArea;
	}

	
}
