package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
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
		bFind.setMnemonic(KeyEvent.VK_F);
		
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		bCancel.setMnemonic(KeyEvent.VK_C);
		
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
			MapViewer.winMain.fatController.findFeaturesRequested = true;
			showResultsPanel();
		}
		
		else if (e.getSource() == bCancel)
		{
			//hide the find dialog
			setVisible(false);
			//clear the found features
			MapViewer.winMain.fatController.highlightFeature = null;
			MapViewer.winMain.fatController.highlightFeatureHomolog = null;
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
			
			JTable homologResultsTable = MapViewer.winMain.ffResultsPanel.getFFResultsTable();
			
			//now insert the results into the JTable held by the results panel
			LinkedList<Link> featuresFound = MapViewer.winMain.fatController.matchFeaturesToNames(allNames);
			FoundFeatureTableModel foundFeatureTableModel = new FoundFeatureTableModel(featuresFound);
			homologResultsTable.setModel(foundFeatureTableModel);
			//size the columns and the dialog containing the table appropriately
			MapViewer.winMain.ffResultsPanel.initColumnSizes();
			//hide the control panel for the results table as it is not needed with this kind of results
			MapViewer.winMain.foundFeaturesTableControlPanel.setVisible(false);

			if (featuresFound.size() > 0)
			{
				//set the results panel to be visible
				this.setVisible(false);
				MapViewer.winMain.splitPane.setDividerSize(Constants.SPLITPANE_DIVIDER_SIZE);
				int newDividerLocation = -1;
				//check how big the results table is
				int tableHeight = homologResultsTable.getRowCount() * homologResultsTable.getRowHeight();
				int spacer = 150;
				int totalTableHeight = tableHeight + spacer;
				//if it is more than a third of the main window we want to limit it to that size
				if (totalTableHeight > (MapViewer.winMain.getHeight() * 0.33f))
				{
					newDividerLocation = (int) (MapViewer.winMain.getHeight() * 0.66f);
				}
				else
				{
					newDividerLocation = (int) (MapViewer.winMain.getHeight() - totalTableHeight);
				}
				MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);
				
				// validate and repaint the canvas so it knows it has been resized
				MapViewer.winMain.validate();
				MapViewer.winMain.mainCanvas.updateCanvas(true);
			}
			else
			{
				TaskDialog.info("No matches found for the name(s) entered", "Close");
			}

		}
		catch (RuntimeException e1)
		{
			e1.printStackTrace();
		}
	}
	
	
}
