package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class FindFeaturesDialog extends JDialog implements ActionListener
{
	
	private JButton bFind, bCancel;
	public MTFindFeaturesPanel ffPanel = new MTFindFeaturesPanel();
	
	public FindFeaturesDialog()
	{
		super(MapViewer.winMain, "Find features by name", true);
		
		add(ffPanel);
		add(createButtons(), BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);
		
		pack();
		setResizable(true);
	}
	
	
	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bCancel = SwingUtils.getButton("Close");
		bCancel.addActionListener(this);
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bFind);
		p1.add(bCancel);
		
		return p1;
	}
	
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFind)
		{
			showResultsPanel();
		}
		
		else if (e.getSource() == bCancel)
		{
			//hide the find dialog
			setVisible(false);
			//clear the found features
			MapViewer.winMain.fatController.foundFeatures.clear();
			MapViewer.winMain.fatController.foundFeatureHomologs.clear();
		}
	}
	
	
	private void showResultsPanel()
	{
		try
		{					
			//this array holds all the names of the features we need to display
			String [] allNames = new String[0];

			//parse input and find features
			String input =  ffPanel.getFFTextArea().getText();		
			allNames = input.split("\n");
			
			//now insert the results into the JTable held by the results panel
			FoundFeatureTableModel foundFeatureTableModel = MapViewer.winMain.fatController.makeFoundFeaturesDataModel(allNames);
			MapViewer.winMain.ffResultsPanel.getFFResultsTable().setModel(foundFeatureTableModel);
			//size the columns and the dialog containing the table appropriately
			MapViewer.winMain.ffResultsPanel.initColumnSizes();
			//hide the control panel for the results table as it is not needed with this kind of results
			MapViewer.winMain.foundFeaturesTableControlPanel.setVisible(false);
			
			//set the results panel to be visible and hide the find dialog
			this.setVisible(false);
			MapViewer.winMain.splitPane.setDividerSize(5);
			int newDividerLocation = (int) (MapViewer.winMain.mainCanvas.getHeight() - MapViewer.winMain.foundFeaturesTableControlPanel.getPreferredSize().getHeight());
			MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);

		}
		catch (RuntimeException e1)
		{
			e1.printStackTrace();
		}
	}
	
	
}
