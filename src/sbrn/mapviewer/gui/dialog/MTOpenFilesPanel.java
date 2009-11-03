/*
 * MTOpenFilesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import java.io.*;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

/**
 *
 * @author  __USER__
 */
public class MTOpenFilesPanel extends javax.swing.JPanel
{
	
	JFileChooser fc;
	File dataDir = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "data");
	
	//lookup table for relating buttons to their text fields
	Hashtable<JButton, JTextField> buttonMap = new Hashtable<JButton, JTextField>();
	
	/** Creates new form MTOpenFilesPanel */
	public MTOpenFilesPanel()
	{
		initComponents();
		
		// file chooser
		fc = new JFileChooser(dataDir);
		
		//inits a lookup table for relating buttons to their text fields
		mapButtonsToTextFields();
	}
	
	//inits a lookup table for relating buttons to their text fields
	private void mapButtonsToTextFields()
	{
		buttonMap.put(browseButton, inputFileTF);
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		importModeButtonGroup = new javax.swing.ButtonGroup();
		dataImportModePanel = new javax.swing.JPanel();
		exampleDataRadioButton = new javax.swing.JRadioButton();
		ownDataRadioButton = new javax.swing.JRadioButton();
		targetGenomeLoaderPanel = new javax.swing.JPanel();
		inputFileLabel = new javax.swing.JLabel();
		inputFileTF = new javax.swing.JTextField();
		browseButton = new javax.swing.JButton();
		
		dataImportModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose data import mode:"));
		
		importModeButtonGroup.add(exampleDataRadioButton);
		exampleDataRadioButton.setSelected(true);
		exampleDataRadioButton.setText("Load example data provided with the application");
		
		importModeButtonGroup.add(ownDataRadioButton);
		ownDataRadioButton.setText("Load own data files");
		ownDataRadioButton.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ownDataRadioButtonStateChanged(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout dataImportModePanelLayout = new org.jdesktop.layout.GroupLayout(dataImportModePanel);
		dataImportModePanel.setLayout(dataImportModePanelLayout);
		dataImportModePanelLayout.setHorizontalGroup(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(dataImportModePanelLayout.createSequentialGroup().add(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(org.jdesktop.layout.GroupLayout.LEADING, ownDataRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, exampleDataRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap(192, Short.MAX_VALUE)));
		dataImportModePanelLayout.setVerticalGroup(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(dataImportModePanelLayout.createSequentialGroup().add(exampleDataRadioButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(ownDataRadioButton)));
		
		targetGenomeLoaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Strudel format combined input file:"));
		targetGenomeLoaderPanel.setEnabled(false);
		
		inputFileLabel.setEnabled(false);
		inputFileLabel.setText("Input data file:");
		
		inputFileTF.setEnabled(false);
		
		browseButton.setEnabled(false);
		browseButton.setText("Browse...");
		browseButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout targetGenomeLoaderPanelLayout = new org.jdesktop.layout.GroupLayout(targetGenomeLoaderPanel);
		targetGenomeLoaderPanel.setLayout(targetGenomeLoaderPanelLayout);
		targetGenomeLoaderPanelLayout.setHorizontalGroup(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, targetGenomeLoaderPanelLayout.createSequentialGroup().add(inputFileLabel).add(18, 18, 18).add(inputFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE).add(18, 18, 18).add(browseButton).addContainerGap()));
		targetGenomeLoaderPanelLayout.setVerticalGroup(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(inputFileLabel).add(inputFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(browseButton)));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(org.jdesktop.layout.GroupLayout.LEADING, dataImportModePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, targetGenomeLoaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(dataImportModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(targetGenomeLoaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//=======================================================================
	
	private void ownDataRadioButtonStateChanged(javax.swing.event.ChangeEvent evt)
	{
		if (ownDataRadioButton.isSelected())
		{
			//enable the components required for specifying own file locations				
			setOwnFileCompsEnabled(true);
			MapViewer.winMain.fatController.loadOwnData = true;
		}
		else
		{
			//disable the components required for specifying own file locations				
			setOwnFileCompsEnabled(false);
			MapViewer.winMain.fatController.loadOwnData = false;
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	
	private void setOwnFileCompsEnabled(boolean enable)
	{
		browseButton.setEnabled(enable);
		inputFileLabel.setEnabled(enable);
		inputFileTF.setEnabled(enable);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	
	private void actionPerformed(java.awt.event.ActionEvent evt)
	{
		//has this event been triggered by a JButton
		if (evt.getSource().getClass().isInstance(browseButton))
		{
			JButton sourceButton = (JButton) evt.getSource();
			//find out which textfield goes with this button
			JTextField textField = buttonMap.get(sourceButton);
			int returnVal = fc.showOpenDialog(MapViewer.winMain.openFileDialog);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				textField.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton browseButton;
	private javax.swing.JPanel dataImportModePanel;
	private javax.swing.JRadioButton exampleDataRadioButton;
	private javax.swing.ButtonGroup importModeButtonGroup;
	private javax.swing.JLabel inputFileLabel;
	private javax.swing.JTextField inputFileTF;
	private javax.swing.JRadioButton ownDataRadioButton;
	private javax.swing.JPanel targetGenomeLoaderPanel;
	
	// End of variables declaration//GEN-END:variables
	
	public javax.swing.JTextField getInputFileTF()
	{
		return inputFileTF;
	}
	
}
