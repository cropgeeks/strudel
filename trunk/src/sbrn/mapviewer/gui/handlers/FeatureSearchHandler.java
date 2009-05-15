package sbrn.mapviewer.gui.handlers;

import java.util.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class FeatureSearchHandler
{

//==========================================methods==========================================================	
	
	public static void findFeaturesInRangeFromDialog(FindFeaturesInRangeDialog findFeaturesInRangeDialog)
	{
		//gather the required inputs from the panel
		String genome = (String) findFeaturesInRangeDialog.ffInRangePanel.getGenomeCombo().getSelectedItem();
		String chromosome =  (String) findFeaturesInRangeDialog.ffInRangePanel.getChromoCombo().getSelectedItem();
		float intervalStart = ((Number)findFeaturesInRangeDialog.ffInRangePanel.getRangeStartSpinner().getValue()).floatValue();
		float intervalEnd = ((Number)findFeaturesInRangeDialog.ffInRangePanel.getRangeEndSpinner().getValue()).floatValue();

		findAndDisplayFeaturesInRange(genome, chromosome, intervalStart, intervalEnd);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static void findFeaturesInRangeFromCanvasSelection()
	{
		GChromoMap gMap = MapViewer.winMain.fatController.selectionMap;	
		findAndDisplayFeaturesInRange(gMap.owningSet.name, gMap.name, gMap.relativeTopY, gMap.relativeBottomY);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private  static void findAndDisplayFeaturesInRange(String genome, String chromosome,float intervalStart, float intervalEnd)
	{
		try
		{		
			//first reset the canvas to its default view
			MapViewer.winMain.fatController.resetMainCanvasView();		
			
			//this array holds all the names of the features we need to display
			String [] allNames = new String[0];
			GChromoMap gChromoMap = null;
			
			//get the chromo object
			gChromoMap = Utils.getGMapByName(chromosome,genome);
			ChromoMap chromoMap = gChromoMap.chromoMap;
			
			//we need to check that we have not exceeded the maximum value of the positions on the chromosome
			if(intervalEnd > chromoMap.getStop())
			{
				TaskDialog.error("The range end value exceeds the maximum position value on the chromosome.", "Close");
				return;
			}
			
			//also check the range start is less than the range end
			if(intervalEnd < intervalStart)
			{
				TaskDialog.error("The range start value is greater than the range end value.", "Close");
				return;
			}
	
			//get a list with names for all the features contained in this interval
			Vector<String> containedFeatureNames = new Vector<String>();
			MapViewer.logger.fine("checking for features in range");
			for(Feature f : chromoMap.getFeatureList())
			{
				MapViewer.logger.fine("feature = " + f.getName() + " " + f.getStart());
				boolean featureHasLinks = f.getLinks().size() > 0;
				//add the feature only if it is in the interval and has links or if the number of mapsets loaded is 1
				if((f.getStart() >= intervalStart) && (f.getStart() <= intervalEnd) && (featureHasLinks || MapViewer.winMain.dataContainer.gMapSetList.size() == 1))
				{	
					containedFeatureNames.add(f.getName());
					MapViewer.winMain.fatController.featuresInRange.add(f);
				}
			}
			allNames = containedFeatureNames.toArray(allNames);
			
			//if there are actually features contained in this range
			if (containedFeatureNames.size() > 0)
			{
				
				//tell it to highlight the region specified
				gChromoMap.highlightedRegionStart = intervalStart;
				gChromoMap.highlightedRegionEnd = intervalEnd;
				gChromoMap.highlightChromomapRegion = true;
										
				//resize the split pane so we can see the results table
				MapViewer.winMain.splitPane.setDividerSize(Constants.SPLITPANE_DIVIDER_SIZE);
				int newDividerLocation = (int) (MapViewer.winMain.getHeight() - MapViewer.winMain.foundFeaturesTableControlPanel.getMinimumSize().getHeight());
				MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);
						
				// validate and repaint the canvas so it knows it has been resized
				MapViewer.winMain.validate();
				MapViewer.winMain.mainCanvas.updateCanvas(true);

				//now zoom into that range on the chromosome
				MapViewer.winMain.mainCanvas.zoomHandler.zoomIntoRange(gChromoMap, intervalStart, intervalEnd, false);
	
				//we also need to set the labels on the control panel for the results to have the appropriate text
				FoundFeaturesTableControlPanel foundFeaturesTableControlPanel = MapViewer.winMain.foundFeaturesTableControlPanel;
				foundFeaturesTableControlPanel.setVisible(true);
				foundFeaturesTableControlPanel.getGenomeLabel().setText(genome);
				foundFeaturesTableControlPanel.getChromoLabel().setText(chromosome);
				foundFeaturesTableControlPanel.getRegionStartLabel().setText(new Float(intervalStart).toString());
				foundFeaturesTableControlPanel.getRegionEndLabel().setText(new Float(intervalEnd).toString());
				foundFeaturesTableControlPanel.getNumberFeaturesLabel().setText(new Integer(containedFeatureNames.size()).toString());
				
				//sync the checkboxes states with those in the find dialog itself to make sure they show the same value
				foundFeaturesTableControlPanel.getShowLabelsCheckbox().setSelected(MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().isSelected());
				foundFeaturesTableControlPanel.getShowHomologsCheckbox().setSelected(MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().isSelected());
				
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
				
				//hide the dialog
				MapViewer.winMain.ffInRangeDialog.setVisible(false);
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
