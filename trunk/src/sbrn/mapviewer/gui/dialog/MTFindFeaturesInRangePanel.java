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
public class MTFindFeaturesInRangePanel extends javax.swing.JPanel implements ActionListener
{
	
	/** Creates new form MTFindFeaturesPanel */
	public MTFindFeaturesInRangePanel()
	{
		initComponents();

	}
	
	public void initRemainingComponents()
	{	
		//set up the combo boxes with their data models
		Vector<String> genomes = new Vector<String>();
		for (GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSetList)
		{
			genomes.add(gMapSet.name);
		}
		genomeCombo.setModel(new DefaultComboBoxModel(genomes));
		setUpInitialCombos();
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jPanel1 = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		genomeCombo = new javax.swing.JComboBox();
		jLabel5 = new javax.swing.JLabel();
		chromoCombo = new javax.swing.JComboBox();
		jLabel6 = new javax.swing.JLabel();
		intervalStartTextField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		intervalEndTextField = new javax.swing.JTextField();
		displayHomologsCheckBox = new javax.swing.JCheckBox();
		displayLabelsCheckbox = new javax.swing.JCheckBox();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a range for listing features in:"));
		
		jLabel4.setText("Genome:");
		
		genomeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		genomeCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTFindFeaturesInRangePanel.this.actionPerformed(evt);
			}
		});
		
		jLabel5.setText("Chromosome:");
		
		chromoCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		chromoCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTFindFeaturesInRangePanel.this.actionPerformed(evt);
			}
		});
		
		jLabel6.setText("Range start:");
		
		jLabel7.setText("Range end:");
		
		displayHomologsCheckBox.setText("Display homologies for this range");
		displayHomologsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		
		displayLabelsCheckbox.setText("Display labels for all features");
		displayLabelsCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).add(18, 18, 18).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(genomeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 203, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(org.jdesktop.layout.GroupLayout.LEADING, chromoCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, intervalEndTextField).add(org.jdesktop.layout.GroupLayout.LEADING, intervalStartTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))).add(displayHomologsCheckBox).add(displayLabelsCheckbox)).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel4).add(genomeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel5).add(chromoCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel6).add(intervalStartTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel7).add(intervalEndTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(displayHomologsCheckBox).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(displayLabelsCheckbox).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents
	
	private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt)
	{
		// TODO add your handling code here:
	}
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JComboBox chromoCombo;
	private javax.swing.JCheckBox displayHomologsCheckBox;
	private javax.swing.JCheckBox displayLabelsCheckbox;
	private javax.swing.JComboBox genomeCombo;
	private javax.swing.JTextField intervalEndTextField;
	private javax.swing.JTextField intervalStartTextField;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JPanel jPanel1;
	
	// End of variables declaration//GEN-END:variables
	
	public javax.swing.JComboBox getChromoCombo()
	{
		return chromoCombo;
	}
	
	public javax.swing.JComboBox getGenomeCombo()
	{
		return genomeCombo;
	}
	
	public javax.swing.JTextField getIntervalEndTextField()
	{
		return intervalEndTextField;
	}
	
	public javax.swing.JTextField getIntervalStartTextField()
	{
		return intervalStartTextField;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == genomeCombo)
		{
			JComboBox cb = (JComboBox) e.getSource();
			String genomeName = (String) cb.getSelectedItem();
			setUpChromoCombo(genomeName);
		}
	}
	
	private void setUpInitialCombos()
	{
		//populate the chromoCombo with the chromos for the currently selected genome
		setUpChromoCombo((String) genomeCombo.getSelectedItem());
	}
	
	private void setUpChromoCombo(String genomeName)
	{
		//find the genome object and set the other combo to list its chromosomes
		GMapSet gMapSet = Utils.getGMapSetByName(genomeName);
		Vector<String> chromoNames = new Vector<String>();
		for (GChromoMap gChromoMap : gMapSet.gMaps)
		{
			chromoNames.add(gChromoMap.name);
		}
		chromoCombo.setModel(new DefaultComboBoxModel(chromoNames));
	}
	
	public javax.swing.JCheckBox getDisplayHomologsCheckBox()
	{
		return displayHomologsCheckBox;
	}
	
	public javax.swing.JCheckBox getDisplayLabelsCheckbox()
	{
		return displayLabelsCheckbox;
	}
	
}
