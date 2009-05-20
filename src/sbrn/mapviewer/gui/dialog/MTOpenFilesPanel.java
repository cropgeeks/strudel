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
		buttonMap.put(targetFeatFileButton, targetfeatFileTF);
		buttonMap.put(ref1FeatFileButton, refGen1FeatFileTF);
		buttonMap.put(ref1HomFileButton, refGen1HomFileTF);
		buttonMap.put(ref2FeatFileButton, refGen2FeatFileTF);
		buttonMap.put(ref2HomFileButton, refGen2HomFileTF);
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
		targetFeatFileLabel = new javax.swing.JLabel();
		targetfeatFileTF = new javax.swing.JTextField();
		targetFeatFileButton = new javax.swing.JButton();
		referenceGenomeLoaderPanel = new javax.swing.JPanel();
		refGen1FeatFileLabel = new javax.swing.JLabel();
		refGen1FeatFileTF = new javax.swing.JTextField();
		ref1FeatFileButton = new javax.swing.JButton();
		refGen1HomFileLabel = new javax.swing.JLabel();
		refGen1HomFileTF = new javax.swing.JTextField();
		ref1HomFileButton = new javax.swing.JButton();
		refGen2HomFileLabel = new javax.swing.JLabel();
		refGen2HomFileTF = new javax.swing.JTextField();
		ref2FeatFileButton = new javax.swing.JButton();
		refGen2FeatFileLabel = new javax.swing.JLabel();
		ref2HomFileButton = new javax.swing.JButton();
		refGen2FeatFileTF = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		refGenome1UrlLabel = new javax.swing.JLabel();
		refGenome1UrlTf = new javax.swing.JTextField();
		refGenome2UrlLabel = new javax.swing.JLabel();
		refGenome2UrlTf = new javax.swing.JTextField();
		targetGenomeUrlLabel1 = new javax.swing.JLabel();
		targetGenomeUrlTf1 = new javax.swing.JTextField();
		
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
		dataImportModePanelLayout.setHorizontalGroup(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(dataImportModePanelLayout.createSequentialGroup().add(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(org.jdesktop.layout.GroupLayout.LEADING, ownDataRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, exampleDataRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap(317, Short.MAX_VALUE)));
		dataImportModePanelLayout.setVerticalGroup(dataImportModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(dataImportModePanelLayout.createSequentialGroup().add(exampleDataRadioButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(ownDataRadioButton)));
		
		targetGenomeLoaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select target genome feature file:"));
		targetGenomeLoaderPanel.setEnabled(false);
		
		targetFeatFileLabel.setEnabled(false);
		targetFeatFileLabel.setText("Target genome feature file:");
		
		targetfeatFileTF.setEnabled(false);
		
		targetFeatFileButton.setEnabled(false);
		targetFeatFileButton.setText("Browse...");
		targetFeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout targetGenomeLoaderPanelLayout = new org.jdesktop.layout.GroupLayout(targetGenomeLoaderPanel);
		targetGenomeLoaderPanel.setLayout(targetGenomeLoaderPanelLayout);
		targetGenomeLoaderPanelLayout.setHorizontalGroup(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, targetGenomeLoaderPanelLayout.createSequentialGroup().add(targetFeatFileLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(targetfeatFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE).add(18, 18, 18).add(targetFeatFileButton).addContainerGap()));
		targetGenomeLoaderPanelLayout.setVerticalGroup(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(targetGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(targetFeatFileButton).add(targetfeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(targetFeatFileLabel)));
		
		referenceGenomeLoaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Optionally select one or two reference genomes:"));
		referenceGenomeLoaderPanel.setEnabled(false);
		
		refGen1FeatFileLabel.setEnabled(false);
		refGen1FeatFileLabel.setText("Reference genome 1 feature file:");
		
		refGen1FeatFileTF.setEnabled(false);
		
		ref1FeatFileButton.setEnabled(false);
		ref1FeatFileButton.setText("Browse...");
		ref1FeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen1HomFileLabel.setEnabled(false);
		refGen1HomFileLabel.setText("Reference genome 1 homology file:");
		
		refGen1HomFileTF.setEnabled(false);
		
		ref1HomFileButton.setEnabled(false);
		ref1HomFileButton.setText("Browse...");
		ref1HomFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen2HomFileLabel.setEnabled(false);
		refGen2HomFileLabel.setText("Reference genome 2 homology file:");
		
		refGen2HomFileTF.setEnabled(false);
		
		ref2FeatFileButton.setEnabled(false);
		ref2FeatFileButton.setText("Browse...");
		ref2FeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen2FeatFileLabel.setEnabled(false);
		refGen2FeatFileLabel.setText("Reference genome 2 feature file:");
		
		ref2HomFileButton.setEnabled(false);
		ref2HomFileButton.setText("Browse...");
		ref2HomFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen2FeatFileTF.setEnabled(false);
		
		org.jdesktop.layout.GroupLayout referenceGenomeLoaderPanelLayout = new org.jdesktop.layout.GroupLayout(referenceGenomeLoaderPanel);
		referenceGenomeLoaderPanel.setLayout(referenceGenomeLoaderPanelLayout);
		referenceGenomeLoaderPanelLayout.setHorizontalGroup(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING, refGen2FeatFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, refGen1FeatFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, refGen1HomFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().add(5, 5, 5).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(refGen2FeatFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE).add(refGen2HomFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(refGen1FeatFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE).add(refGen1HomFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))))).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().add(refGen2HomFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))).add(18, 18, 18).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(ref1HomFileButton).add(ref1FeatFileButton).add(ref2FeatFileButton).add(ref2HomFileButton)).addContainerGap()));
		referenceGenomeLoaderPanelLayout.setVerticalGroup(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(referenceGenomeLoaderPanelLayout.createSequentialGroup().add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen1FeatFileLabel).add(refGen1FeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref1FeatFileButton)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen1HomFileLabel).add(refGen1HomFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref1HomFileButton)).add(8, 8, 8).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen2FeatFileLabel).add(refGen2FeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref2FeatFileButton)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(referenceGenomeLoaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen2HomFileLabel).add(refGen2HomFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref2HomFileButton))));
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotation URLs:"));
		
		refGenome1UrlLabel.setEnabled(false);
		refGenome1UrlLabel.setText("URL Reference genome 1:");
		
		refGenome1UrlTf.setEnabled(false);
		
		refGenome2UrlLabel.setEnabled(false);
		refGenome2UrlLabel.setText("URL Reference genome 2:");
		
		refGenome2UrlTf.setEnabled(false);
		
		targetGenomeUrlLabel1.setEnabled(false);
		targetGenomeUrlLabel1.setText("URL target genome:");
		
		targetGenomeUrlTf1.setEnabled(false);
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().add(refGenome2UrlLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(refGenome2UrlTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)).add(jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(refGenome1UrlLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(targetGenomeUrlLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(targetGenomeUrlTf1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE).add(refGenome1UrlTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)))).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(targetGenomeUrlLabel1).add(targetGenomeUrlTf1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGenome1UrlLabel).add(refGenome1UrlTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGenome2UrlLabel).add(refGenome2UrlTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(org.jdesktop.layout.GroupLayout.LEADING, targetGenomeLoaderPanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, referenceGenomeLoaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, dataImportModePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(13, 13, 13).add(dataImportModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(targetGenomeLoaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(referenceGenomeLoaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(18, 18, 18).add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
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
		targetFeatFileButton.setEnabled(enable);
		ref1FeatFileButton.setEnabled(enable);
		ref1HomFileButton.setEnabled(enable);
		ref2FeatFileButton.setEnabled(enable);
		ref2HomFileButton.setEnabled(enable);
		targetFeatFileLabel.setEnabled(enable);
		refGen1FeatFileLabel.setEnabled(enable);
		refGen2HomFileLabel.setEnabled(enable);
		refGen1HomFileLabel.setEnabled(enable);
		refGen2FeatFileLabel.setEnabled(enable);
		refGen1HomFileTF.setEnabled(enable);
		refGen2FeatFileTF.setEnabled(enable);
		refGen2HomFileTF.setEnabled(enable);
		refGen1FeatFileTF.setEnabled(enable);
		targetfeatFileTF.setEnabled(enable);
		targetGenomeUrlTf1.setEnabled(enable);
		refGenome1UrlTf.setEnabled(enable);
		refGenome2UrlTf.setEnabled(enable);
		refGenome1UrlLabel.setEnabled(enable);
		refGenome2UrlLabel.setEnabled(enable);
		targetGenomeUrlLabel1.setEnabled(enable);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------
	
	private void actionPerformed(java.awt.event.ActionEvent evt)
	{
		//has this event been triggered by a JButton
		if (evt.getSource().getClass().isInstance(targetFeatFileButton))
		{
			JButton sourceButton = (JButton) evt.getSource();
			//find out which textfield goes with this button
			JTextField textField = buttonMap.get(sourceButton);
			
			//point this file chooser at the current working directory
			fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
			
			//dirty hack to stop the file chooser remembering the last opened file and offering it as a default
			File emptyFile = new File("");
			fc.setSelectedFile(emptyFile);
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
	private javax.swing.JPanel dataImportModePanel;
	private javax.swing.JRadioButton exampleDataRadioButton;
	private javax.swing.ButtonGroup importModeButtonGroup;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JRadioButton ownDataRadioButton;
	private javax.swing.JButton ref1FeatFileButton;
	private javax.swing.JButton ref1HomFileButton;
	private javax.swing.JButton ref2FeatFileButton;
	private javax.swing.JButton ref2HomFileButton;
	private javax.swing.JLabel refGen1FeatFileLabel;
	private javax.swing.JTextField refGen1FeatFileTF;
	private javax.swing.JLabel refGen1HomFileLabel;
	private javax.swing.JTextField refGen1HomFileTF;
	private javax.swing.JLabel refGen2FeatFileLabel;
	private javax.swing.JTextField refGen2FeatFileTF;
	private javax.swing.JLabel refGen2HomFileLabel;
	private javax.swing.JTextField refGen2HomFileTF;
	private javax.swing.JLabel refGenome1UrlLabel;
	private javax.swing.JTextField refGenome1UrlTf;
	private javax.swing.JLabel refGenome2UrlLabel;
	private javax.swing.JTextField refGenome2UrlTf;
	private javax.swing.JPanel referenceGenomeLoaderPanel;
	private javax.swing.JButton targetFeatFileButton;
	private javax.swing.JLabel targetFeatFileLabel;
	private javax.swing.JPanel targetGenomeLoaderPanel;
	private javax.swing.JLabel targetGenomeUrlLabel1;
	private javax.swing.JTextField targetGenomeUrlTf1;
	private javax.swing.JTextField targetfeatFileTF;
	
	// End of variables declaration//GEN-END:variables
	
	public javax.swing.JTextField getRefGen1FeatFileTF()
	{
		return refGen1FeatFileTF;
	}
	
	public javax.swing.JTextField getRefGen1HomFileTF()
	{
		return refGen1HomFileTF;
	}
	
	public javax.swing.JTextField getRefGen2FeatFileTF()
	{
		return refGen2FeatFileTF;
	}
	
	public javax.swing.JTextField getRefGen2HomFileTF()
	{
		return refGen2HomFileTF;
	}
	
	public javax.swing.JTextField getTargetfeatFileTF()
	{
		return targetfeatFileTF;
	}
	
	public javax.swing.JTextField getRefGenome1UrlTf()
	{
		return refGenome1UrlTf;
	}
	
	public javax.swing.JTextField getRefGenome2UrlTf()
	{
		return refGenome2UrlTf;
	}
	
	public javax.swing.JLabel getRefGenome1UrlLabel()
	{
		return refGenome1UrlLabel;
	}
	
	public javax.swing.JLabel getRefGenome2UrlLabel()
	{
		return refGenome2UrlLabel;
	}
	
	public javax.swing.JTextField getTargetGenomeUrlTf1()
	{
		return targetGenomeUrlTf1;
	}
	
}
