/*
 * FoundFeaturesTableControlPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.components;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

/**
 *
 * @author  __USER__
 */
public class FoundFeaturesTableControlPanel extends javax.swing.JPanel
{

	/** Creates new form FoundFeaturesTableControlPanel */
	public FoundFeaturesTableControlPanel()
	{
		initComponents();
		setupGenomeFilterCombo();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{

		genomeLabel = new javax.swing.JLabel();
		chromoLabel = new javax.swing.JLabel();
		regionStartLabel = new javax.swing.JLabel();
		regionEndLabel = new javax.swing.JLabel();
		numberFeaturesLabel = new javax.swing.JLabel();
		showLabelsCheckbox = new javax.swing.JCheckBox();
		showHomologsCheckbox = new javax.swing.JCheckBox();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		filterLabel = new javax.swing.JLabel();
		genomeFilterCombo = new javax.swing.JComboBox();

		setBorder(javax.swing.BorderFactory.createTitledBorder("Highlighted region: "));
		setMinimumSize(new java.awt.Dimension(10, 300));

		genomeLabel.setText("n/a");

		chromoLabel.setText("n/a");

		regionStartLabel.setText("n/a");

		regionEndLabel.setText("n/a");

		numberFeaturesLabel.setText("n/a");

		showLabelsCheckbox.setText("Show all labels");
		showLabelsCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				showLabelsCheckboxStateChanged(evt);
			}
		});

		showHomologsCheckbox.setText("Show all homologies");
		showHomologsCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				showHomologsCheckboxStateChanged(evt);
			}
		});

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
		jLabel1.setText("Genome:");

		jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
		jLabel2.setText("Chromosome:");

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
		jLabel3.setText("Region start:");

		jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
		jLabel4.setText("Region end:");

		jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
		jLabel5.setText("No. of features:");

		filterLabel.setText("Filter by reference genome:");

		genomeFilterCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		genomeFilterCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				genomeFilterComboActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)).add(filterLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(numberFeaturesLabel).add(regionEndLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE).add(regionStartLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE).add(chromoLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE).add(genomeLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE).add(genomeFilterCombo, 0, 101, Short.MAX_VALUE))).add(showLabelsCheckbox).add(showHomologsCheckbox)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel1).add(genomeLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel2).add(chromoLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel3).add(regionStartLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel4).add(regionEndLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel5).add(numberFeaturesLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(filterLabel).add(genomeFilterCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(showLabelsCheckbox).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(showHomologsCheckbox).addContainerGap(76, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	private void genomeFilterComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (!(Strudel.winMain.ffResultsPanel.resultsTable.getModel() instanceof DefaultTableModel))
		{
			JComboBox cb = (JComboBox) evt.getSource();
			String genomeName = (String) cb.getSelectedItem();
			HomologResultsTableModel homologResultsTableModel = (HomologResultsTableModel) Strudel.winMain.ffResultsPanel.resultsTable.getModel();
			int genomeColumnIndex = homologResultsTableModel.findColumn(homologResultsTableModel.homologGenomeColumnLabel);
			Strudel.winMain.ffResultsPanel.resultsTable.newFilter(genomeName, genomeColumnIndex);

			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
	}

	private void showHomologsCheckboxStateChanged(javax.swing.event.ChangeEvent evt)
	{
		//synchronise this checkbox with the corresponding one in the find features in range panel
		if (showHomologsCheckbox.isSelected())
			Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().setSelected(true);
		else
			Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().setSelected(false);

		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	private void showLabelsCheckboxStateChanged(javax.swing.event.ChangeEvent evt)
	{
		//synchronise this checkbox with the corresponding one in the find features in range panel
		if (showLabelsCheckbox.isSelected())
			Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().setSelected(true);
		else
			Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().setSelected(false);

		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	public void setupGenomeFilterCombo()
	{
		//set up the combo box with its data model
		Vector<String> genomes = new Vector<String>();
		genomes.add("<none>");
		for (MapSet mapSet : Strudel.winMain.dataContainer.allMapSets)
		{
			genomes.add(mapSet.getName());
		}
		genomeFilterCombo.setModel(new DefaultComboBoxModel(genomes));
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel chromoLabel;
	private javax.swing.JLabel filterLabel;
	private javax.swing.JComboBox genomeFilterCombo;
	private javax.swing.JLabel genomeLabel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel numberFeaturesLabel;
	private javax.swing.JLabel regionEndLabel;
	private javax.swing.JLabel regionStartLabel;
	private javax.swing.JCheckBox showHomologsCheckbox;
	private javax.swing.JCheckBox showLabelsCheckbox;

	// End of variables declaration//GEN-END:variables
	public javax.swing.JLabel getChromoLabel()
	{
		return chromoLabel;
	}

	public javax.swing.JLabel getGenomeLabel()
	{
		return genomeLabel;
	}

	public javax.swing.JLabel getNumberFeaturesLabel()
	{
		return numberFeaturesLabel;
	}

	public javax.swing.JLabel getRegionEndLabel()
	{
		return regionEndLabel;
	}

	public javax.swing.JLabel getRegionStartLabel()
	{
		return regionStartLabel;
	}

	public javax.swing.JCheckBox getShowHomologsCheckbox()
	{
		return showHomologsCheckbox;
	}

	public javax.swing.JCheckBox getShowLabelsCheckbox()
	{
		return showLabelsCheckbox;
	}

	public javax.swing.JLabel getFilterLabel()
	{
		return filterLabel;
	}

	public javax.swing.JComboBox getGenomeFilterCombo()
	{
		return genomeFilterCombo;
	}

}