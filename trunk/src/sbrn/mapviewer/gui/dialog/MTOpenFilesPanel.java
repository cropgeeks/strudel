/*
 * MTOpenFilesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import java.io.*;
import java.util.*;
import javax.swing.*;
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
		
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		targetfeatFileTF = new javax.swing.JTextField();
		targetFeatFileButton = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
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
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select target genome feature file:"));
		
		jLabel2.setText("Target genome feature file:");
		
		targetFeatFileButton.setText("Browse...");
		targetFeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup().add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(targetfeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 315, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(18, 18, 18).add(targetFeatFileButton).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(targetFeatFileButton).add(targetfeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jLabel2)));
		
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Optionally select one or two reference genomes:"));
		
		refGen1FeatFileLabel.setText("Reference genome 1 feature file:");
		
		ref1FeatFileButton.setText("Browse...");
		ref1FeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen1HomFileLabel.setText("Reference genome 1 homology file:");
		
		ref1HomFileButton.setText("Browse...");
		ref1HomFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen2HomFileLabel.setText("Reference genome 2 homology file:");
		
		ref2FeatFileButton.setText("Browse...");
		ref2FeatFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		refGen2FeatFileLabel.setText("Reference genome 2 feature file:");
		
		ref2HomFileButton.setText("Browse...");
		ref2HomFileButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTOpenFilesPanel.this.actionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING, refGen2FeatFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, refGen1FeatFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, refGen1HomFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(5, 5, 5).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(refGen2FeatFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE).add(refGen2HomFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))).add(jPanel2Layout.createSequentialGroup().addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(refGen1FeatFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE).add(refGen1HomFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))))).add(jPanel2Layout.createSequentialGroup().add(refGen2HomFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))).add(18, 18, 18).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(ref1HomFileButton).add(ref1FeatFileButton).add(ref2FeatFileButton).add(ref2HomFileButton)).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen1FeatFileLabel).add(refGen1FeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref1FeatFileButton)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen1HomFileLabel).add(refGen1HomFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref1HomFileButton)).add(8, 8, 8).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen2FeatFileLabel).add(refGen2FeatFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref2FeatFileButton)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(refGen2HomFileLabel).add(refGen2HomFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ref2HomFileButton))));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(21, 21, 21)));
	}// </editor-fold>
	//GEN-END:initComponents
	
	
	private void actionPerformed(java.awt.event.ActionEvent evt)
	{
		JButton sourceButton = (JButton) evt.getSource();
				
		//find out which textfield goes with this button
		JTextField textField = buttonMap.get(sourceButton);
		
		//dirty hack to stop the file chooser remembering the last opened file and offering it as a default
		File emptyFile = new File("");
		fc.setSelectedFile(emptyFile);
		
		int returnVal = fc.showOpenDialog(MapViewer.winMain.toolbar.openFileDialog);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			textField.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
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
	private javax.swing.JButton targetFeatFileButton;
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
	
}
