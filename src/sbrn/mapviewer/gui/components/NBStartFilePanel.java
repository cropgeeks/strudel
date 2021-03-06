// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.event.*;
import java.io.File;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.io.*;
import scri.commons.gui.*;
import scri.commons.gui.matisse.HyperLinkLabel;
import scri.commons.gui.Icons;

/**
 *
 * @author gsteph
 */
public class NBStartFilePanel extends javax.swing.JPanel implements ActionListener
{
	int numRecentFiles = 8;

	private final HyperLinkLabel[] labels = new HyperLinkLabel[numRecentFiles];

	private final String[] files = new String[numRecentFiles];
	private final String[] filenames = new String[numRecentFiles];
	private final String[] tooltips = new String[numRecentFiles];

	public NBStartFilePanel()
	{
		initComponents();
		setOpaque(false);

		tutorialLabel.setText("Take our 90 second quickstart tutorial (recommended)");
		tutorialLabel.setIcon(Icons.getIcon("FILEOPEN16"));

		//action listeners
		tutorialLabel.addActionListener(this);
		openOwnDataLabel.addActionListener(this);
		openExampleDataLabel.addActionListener(this);

		// Create the labels array
		labels[0] = project0;
		labels[1] = project1;
		labels[2] = project2;
		labels[3] = project3;
		labels[4] = project4;
		labels[5] = project5;
		labels[6] = project6;
		labels[7] = project7;

		int recentDocsCount = 0;
		// Parse the list of recent documents
		for (final String path : Prefs.guiRecentDocs)
		{
			// Ignore any that haven't been set yet
			if (path == null || path.equals(" "))
				continue;

			File tempFile = new File(path);

			// Button text will be "name" (or "name1" | "name2")
			String text = tempFile.getName();
			String filePath = tempFile.getPath();
			String tooltip = tempFile.getPath();

			if (recentDocsCount < filenames.length)
			{
				filenames[recentDocsCount] = text;
				files[recentDocsCount] = filePath;
				tooltips[recentDocsCount] = tooltip;
			}
			else
				break;

			recentDocsCount++;
		}

		for (int i = 0; i < labels.length; i++)
		{
			if (filenames[i] != null)
			{
				labels[i].addActionListener(this);
				labels[i].setText(filenames[i]);
				labels[i].setToolTipText("<html>" + tooltips[i] + "</html>");
				labels[i].setIcon(Icons.getIcon("DOCSINGLE"));
			}
			else
				labels[i].setVisible(false);
		}

	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == tutorialLabel)
		{
			Utils.visitURL(Constants.strudelQuickStartPage);
		}
		else if (e.getSource() == openOwnDataLabel)
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
		else if (e.getSource() == openExampleDataLabel)
		{
			Strudel.winMain.fatController.loadOwnData = false;
			DataLoadUtils.loadDataInThread(null, false);
		}
		else
		//source is one of the recent file labels
		{
			for (int i = 0; i < labels.length; i++)
			{
				if (e.getSource() == labels[i])
				{
					Strudel.winMain.fatController.recentFileLoad = true;
					DataLoadUtils.loadDataInThread(files[i], false);
				}
			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{

		tutorialLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		otherUsersLabel = new javax.swing.JLabel();
		openExampleDataLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		openOwnDataLabel = new scri.commons.gui.matisse.HyperLinkLabel();
		project0 = new scri.commons.gui.matisse.HyperLinkLabel();
		project1 = new scri.commons.gui.matisse.HyperLinkLabel();
		project2 = new scri.commons.gui.matisse.HyperLinkLabel();
		project3 = new scri.commons.gui.matisse.HyperLinkLabel();
		project4 = new scri.commons.gui.matisse.HyperLinkLabel();
		project5 = new scri.commons.gui.matisse.HyperLinkLabel();
		project6 = new scri.commons.gui.matisse.HyperLinkLabel();
		project7 = new scri.commons.gui.matisse.HyperLinkLabel();
		otherUsersLabel1 = new javax.swing.JLabel();

		tutorialLabel.setForeground(new java.awt.Color(68, 106, 156));
		tutorialLabel.setText("Take our 90 second quickstart tutorial (recommended)");

		otherUsersLabel.setText("Other Tasks:");

		openExampleDataLabel.setForeground(new java.awt.Color(68, 106, 156));
		openExampleDataLabel.setText("Open example dataset");

		openOwnDataLabel.setForeground(new java.awt.Color(68, 106, 156));
		openOwnDataLabel.setText("Open own dataset...");

		project0.setForeground(new java.awt.Color(68, 106, 156));
		project0.setText("file0");

		project1.setForeground(new java.awt.Color(68, 106, 156));
		project1.setText("file1");

		project2.setForeground(new java.awt.Color(68, 106, 156));
		project2.setText("file2");

		project3.setForeground(new java.awt.Color(68, 106, 156));
		project3.setText("file3");

		project4.setForeground(new java.awt.Color(68, 106, 156));
		project4.setText("file4");

		project5.setForeground(new java.awt.Color(68, 106, 156));
		project5.setText("file5");

		project6.setForeground(new java.awt.Color(68, 106, 156));
		project6.setText("file6");

		project7.setForeground(new java.awt.Color(68, 106, 156));
		project7.setText("file7");

		otherUsersLabel1.setText("Open recent file:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(tutorialLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(otherUsersLabel).addComponent(openOwnDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(openExampleDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(otherUsersLabel1)).addComponent(project0, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project6, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE).addComponent(project7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(tutorialLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(otherUsersLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(openOwnDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(openExampleDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(otherUsersLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(project7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(116, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private scri.commons.gui.matisse.HyperLinkLabel openExampleDataLabel;
	private scri.commons.gui.matisse.HyperLinkLabel openOwnDataLabel;
	private javax.swing.JLabel otherUsersLabel;
	private javax.swing.JLabel otherUsersLabel1;
	private scri.commons.gui.matisse.HyperLinkLabel project0;
	private scri.commons.gui.matisse.HyperLinkLabel project1;
	private scri.commons.gui.matisse.HyperLinkLabel project2;
	private scri.commons.gui.matisse.HyperLinkLabel project3;
	private scri.commons.gui.matisse.HyperLinkLabel project4;
	private scri.commons.gui.matisse.HyperLinkLabel project5;
	private scri.commons.gui.matisse.HyperLinkLabel project6;
	private scri.commons.gui.matisse.HyperLinkLabel project7;
	private scri.commons.gui.matisse.HyperLinkLabel tutorialLabel;
	// End of variables declaration//GEN-END:variables

}
