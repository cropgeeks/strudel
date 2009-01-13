package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

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
			GChromoMap gChromoMap = null;
			
			//gather the required inputs from the panel
			String genome = (String) ffInRangePanel.getGenomeCombo().getSelectedItem();
			String chromosome =  (String) ffInRangePanel.getChromoCombo().getSelectedItem();
			float intervalStart = Float.parseFloat(ffInRangePanel.getIntervalStartTextField().getText());
			float intervalEnd = Float.parseFloat(ffInRangePanel.getIntervalEndTextField().getText());
			
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
			
			//zoom back out first if we are not fully zoomed out already
			if(gChromoMap.owningSet.zoomFactor > 1)
			{
				MapViewer.winMain.mainCanvas.zoomHandler.processZoomResetRequest(gChromoMap.owningSet);
			}
			
			//now zoom into that range on the chromosome
			//need to know where to zoom into first
			int relativeTopY = (int) Math.floor((gChromoMap.height / chromoMap.getStop()) * intervalStart);
			int relativeBottomY = (int) Math.ceil((gChromoMap.height / chromoMap.getStop()) * intervalEnd);
			//this buffer increases the size of the visible interval slightly so the bounds don't coincide with the canvas bounds
			int buffer = 2;
			int topY =  relativeTopY + gChromoMap.y - buffer;
			int bottomY = relativeBottomY  + gChromoMap.y + buffer;
			MapViewer.winMain.mainCanvas.zoomHandler.processPanZoomRequest(gChromoMap, topY, bottomY);
			
			//we also need to set the labels on the control panel for the results to have the appropriate text
			FoundFeaturesTableControlPanel foundFeaturesTableControlPanel = MapViewer.winMain.foundFeaturesTableControlPanel;
			foundFeaturesTableControlPanel.setVisible(true);
			foundFeaturesTableControlPanel.getGenomeLabel().setText("<html><b>Genome: </b>" + genome + "</html>");
			foundFeaturesTableControlPanel.getChromoLabel().setText("<html><b>Chromosome: </b>" + chromosome + "</html>");
			foundFeaturesTableControlPanel.getRegionStartLabel().setText("<html><b>Region start: </b>" + intervalStart + "</html>");
			foundFeaturesTableControlPanel.getRegionEndLabel().setText("<html><b>Region end: </b>" + intervalEnd + "</html>");
			foundFeaturesTableControlPanel.getNumberFeaturesLabel().setText("<html><b>No. of features: </b>" + containedFeatureNames.size() + "</html>");
			
			//sync the checkboxes states with those in the find dialog itself to make sure they show the same value
			foundFeaturesTableControlPanel.getShowLabelsCheckbox().setSelected(ffInRangePanel.getDisplayLabelsCheckbox().isSelected());
			foundFeaturesTableControlPanel.getShowHomologsCheckbox().setSelected(ffInRangePanel.getDisplayHomologsCheckBox().isSelected());				
			
			//earmark the features for drawing on repaint
			MapViewer.winMain.mainCanvas.drawFoundFeaturesInRange = true;
			
			//repaint the canvas so we can see the highlighted region which should then be coloured in differently
			MapViewer.winMain.mainCanvas.updateCanvas(true);

			//now insert the results into the JTable held by the results panel
			FoundFeatureTableModel foundFeatureTableModel = MapViewer.winMain.fatController.makeFoundFeaturesDataModel(allNames);
			MapViewer.winMain.ffResultsPanel.getFFResultsTable().setModel(foundFeatureTableModel);
			//size the columns and the dialog containing the table appropriately
			MapViewer.winMain.ffResultsPanel.initColumnSizes();
			
			//set the results panel to be visible and hide the find dialog
			this.setVisible(false);
			MapViewer.winMain.splitPane.setDividerSize(Constants.SPLITPANE_DIVIDER_SIZE);
			int newDividerLocation = (int) (MapViewer.winMain.mainCanvas.getHeight() - MapViewer.winMain.foundFeaturesTableControlPanel.getPreferredSize().getHeight());
			MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);
			
		}
		catch (RuntimeException e1)
		{
			e1.printStackTrace();
		}
	}
	
	
}
