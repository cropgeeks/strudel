package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class FindFeaturesInRangeDialog extends JDialog implements ActionListener
{
	
	private JButton bFind, bCancel;
	public MTFindFeaturesInRangePanel ffInRangePanel = new MTFindFeaturesInRangePanel();
	
	public FindFeaturesInRangeDialog()
	{
		super(MapViewer.winMain, "List features in range", true);
		
		add(ffInRangePanel);
		add(createButtons(), BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);
		
		setLocationRelativeTo(MapViewer.winMain);
		pack();
		setResizable(true);
	
	}
	
	
	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bCancel = SwingUtils.getButton("Cancel");
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
			GChromoMap gChromoMap = null;
			
			//gather the required inputs from the panel
			String genome = (String) ffInRangePanel.getGenomeCombo().getSelectedItem();
			String chromosome =  (String) ffInRangePanel.getChromoCombo().getSelectedItem();
			float intervalStart = (Float)ffInRangePanel.getRangeStartSpinner().getValue();
			float intervalEnd = (Float)ffInRangePanel.getRangeEndSpinner().getValue();
			
			//get the chromo object
			gChromoMap = Utils.getGMapByName(chromosome,genome);
			ChromoMap chromoMap = gChromoMap.chromoMap;
			
			//tell it to highlight the region specified
			gChromoMap.highlightedRegionStart = intervalStart;
			gChromoMap.highlightedRegionEnd = intervalEnd;
			gChromoMap.highlightChromomapRegion = true;
			
			//get a list with names for all the features contained in this interval
			Vector<String> containedFeatureNames = new Vector<String>();
			for(Feature f : chromoMap.getFeatureList())
			{
				if((f.getStart() >= intervalStart) && (f.getStart() <= intervalEnd))
				{
					containedFeatureNames.add(f.getName());
					MapViewer.winMain.fatController.featuresInRange.add(f);
				}
			}
			allNames = containedFeatureNames.toArray(allNames);
			
			//if there are actually features contained in this range
			if (containedFeatureNames.size() > 0)
			{
				//resize the split pane so we can see the results table
				MapViewer.winMain.splitPane.setDividerSize(Constants.SPLITPANE_DIVIDER_SIZE);
				int newDividerLocation = (int) (MapViewer.winMain.getHeight() - MapViewer.winMain.foundFeaturesTableControlPanel.getMinimumSize().getHeight());
				MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);
				
				//now zoom into that range on the chromosome
				MapViewer.winMain.mainCanvas.zoomHandler.zoomIntoRange(gChromoMap, intervalStart, intervalEnd);
				
				//we also need to set the labels on the control panel for the results to have the appropriate text
				FoundFeaturesTableControlPanel foundFeaturesTableControlPanel = MapViewer.winMain.foundFeaturesTableControlPanel;
				foundFeaturesTableControlPanel.setVisible(true);
				foundFeaturesTableControlPanel.getGenomeLabel().setText(genome);
				foundFeaturesTableControlPanel.getChromoLabel().setText(chromosome);
				foundFeaturesTableControlPanel.getRegionStartLabel().setText(
								new Float(intervalStart).toString());
				foundFeaturesTableControlPanel.getRegionEndLabel().setText(
								new Float(intervalEnd).toString());
				foundFeaturesTableControlPanel.getNumberFeaturesLabel().setText(
								new Integer(containedFeatureNames.size()).toString());
				//sync the checkboxes states with those in the find dialog itself to make sure they show the same value
				foundFeaturesTableControlPanel.getShowLabelsCheckbox().setSelected(
								ffInRangePanel.getDisplayLabelsCheckbox().isSelected());
				foundFeaturesTableControlPanel.getShowHomologsCheckbox().setSelected(
								ffInRangePanel.getDisplayHomologsCheckBox().isSelected());
				//earmark the features for drawing on repaint
				MapViewer.winMain.mainCanvas.drawFoundFeaturesInRange = true;
				//repaint the canvas so we can see the highlighted region which should then be coloured in differently
				MapViewer.winMain.mainCanvas.updateCanvas(true);
				//now insert the results into the JTable held by the results panel
				LinkedList<Link> featuresFound = MapViewer.winMain.fatController.matchFeaturesToNames(allNames);
				FoundFeatureTableModel foundFeatureTableModel = new FoundFeatureTableModel(featuresFound);
				MapViewer.winMain.ffResultsPanel.getFFResultsTable().setModel(foundFeatureTableModel);
				//set up sorting/filtering capability
				TableRowSorter<FoundFeatureTableModel> sorter = new TableRowSorter<FoundFeatureTableModel>(foundFeatureTableModel);
				MapViewer.winMain.ffResultsPanel.getFFResultsTable().setRowSorter(sorter);
				//size the columns and the dialog containing the table appropriately
				MapViewer.winMain.ffResultsPanel.initColumnSizes();
				//set the results panel to be visible 
				this.setVisible(false);
			}
			//no features in the range specified
			else
			{
				TaskDialog.info("No features found in this range", "Close");				
			}
			
		}
		catch (RuntimeException e1)
		{
			e1.printStackTrace();
		}
	}
	
	
}
