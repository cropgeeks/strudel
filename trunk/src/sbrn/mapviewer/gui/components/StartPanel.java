/*
 * StartPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.components;

import java.awt.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.actions.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.io.*;
import scri.commons.gui.Icons;
import scri.commons.gui.matisse.HyperLinkLabel;

/**
 *
 * @author  __USER__
 */
public class StartPanel extends javax.swing.JPanel implements ActionListener
{

	/** Creates new form StartPanel */
	public StartPanel()
	{
		initComponents();
		setupLabels();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator2 = new javax.swing.JSeparator();
		jLabel4 = new javax.swing.JLabel();
		quickStartLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		exampleDataLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		ownDataLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		manualLabel = new scri.commons.gui.matisse.HyperLinkLabel();

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
		jLabel1.setText("Welcome to Strudel");

		jLabel2.setFont(new java.awt.Font("Tahoma", 1, 15));
		jLabel2.setText("New to Strudel?");

		jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16));
		jLabel4.setText("Existing users:");

		quickStartLabel.setFont(new java.awt.Font("Tahoma", 0, 15));
		quickStartLabel.setText("Take our 90 second tutorial (recommended)");

		exampleDataLabel.setFont(new java.awt.Font("Tahoma", 0, 15));
		exampleDataLabel.setText("Open example dataset");

		ownDataLabel.setFont(new java.awt.Font("Tahoma", 0, 15));
		ownDataLabel.setText("Open own dataset...");

		manualLabel.setFont(new java.awt.Font("Tahoma", 0, 15));
		manualLabel.setText("Visit online manual");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(exampleDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addComponent(jLabel1).addComponent(jLabel4).addComponent(ownDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addComponent(jLabel2).addComponent(quickStartLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE).addComponent(manualLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addGap(11, 11, 11).addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10, 10, 10).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(quickStartLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(exampleDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ownDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(manualLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(194, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	private void hyperLinkLabel1ActionPerformed(java.awt.event.ActionEvent evt)
	{
		// TODO add your handling code here:
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private scri.commons.gui.matisse.HyperLinkLabel exampleDataLabel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private scri.commons.gui.matisse.HyperLinkLabel manualLabel;
	private scri.commons.gui.matisse.HyperLinkLabel ownDataLabel;
	private scri.commons.gui.matisse.HyperLinkLabel quickStartLabel;

	// End of variables declaration//GEN-END:variables

	private void setupLabels()
	{
		quickStartLabel.addActionListener(this);
		ownDataLabel.addActionListener(this);
		exampleDataLabel.addActionListener(this);
		manualLabel.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == quickStartLabel)
		{
			Utils.visitURL(Constants.strudelQuickStartPage);
		}
		else if (e.getSource() == ownDataLabel)
		{
			//open the file dialog
			OpenFileDialog openFileDialog = Strudel.winMain.openFileDialog;
			openFileDialog.setLocationRelativeTo(Strudel.winMain);
			//clear the text fields, in case they had text showing previously
			openFileDialog.openFilesPanel.getInputFileTF().setText("");
			//select the appropriate radio button
			openFileDialog.openFilesPanel.getOwnDataRadioButton().setSelected(true);
			openFileDialog.setVisible(true);
		}
		else if (e.getSource() == exampleDataLabel)
		{
			Strudel.winMain.fatController.loadOwnData = false;
			DataLoadUtils.loadDataInThread(null, false);
		}
		else if (e.getSource() == manualLabel)
		{
			Utils.visitURL(Constants.strudelManualPage);
		}

	}

}
