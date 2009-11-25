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
	
	//a vector of features we have looked up by position range
	public static Vector<Feature> featuresInRange = new Vector<Feature>();
	
	//==========================================methods==========================================================	
	
	public static void findFeaturesInRangeFromDialog(FindFeaturesInRangeDialog findFeaturesInRangeDialog)
	{
		//gather the required inputs from the panel
		String genome = (String) findFeaturesInRangeDialog.ffInRangePanel.getGenomeCombo().getSelectedItem();
		String chromosome =  (String) findFeaturesInRangeDialog.ffInRangePanel.getChromoCombo().getSelectedItem();
		float intervalStart = ((Number)findFeaturesInRangeDialog.ffInRangePanel.getRangeStartSpinner().getValue()).floatValue();
		float intervalEnd = ((Number)findFeaturesInRangeDialog.ffInRangePanel.getRangeEndSpinner().getValue()).floatValue();
		
		findAndDisplayFeaturesInRange(genome, chromosome, intervalStart, intervalEnd, false);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static void findFeaturesInRangeFromCanvasSelection()
	{
		MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().setSelected(true);
		
		GChromoMap gMap = MapViewer.winMain.fatController.selectionMap;	
		findAndDisplayFeaturesInRange(gMap.owningSet.name, gMap.name, gMap.relativeTopY, gMap.relativeBottomY, true);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private  static void findAndDisplayFeaturesInRange(String genome, String chromosome,float intervalStart, float intervalEnd, boolean isCanvasSelection)
	{
		try
		{		
			//get the chromo object
			GChromoMap gChromoMap = Utils.getGMapByName(chromosome,genome);
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
			Vector<Feature> containedFeatures = new Vector<Feature>();
			for(Feature f : chromoMap.getFeatureList())
			{
				boolean featureHasLinks = f.getLinks().size() > 0;
				//add the feature only if it is in the interval and has links or if the number of mapsets loaded is 1
				if((f.getStart() >= intervalStart) && (f.getStart() <= intervalEnd) && (featureHasLinks || MapViewer.winMain.dataContainer.gMapSets.size() == 1))
				{	
					containedFeatures.add(f);
					featuresInRange.add(f);
				}
			}
			
			//if there are actually features contained in this range
			if (containedFeatures.size() > 0)
			{				
				//highlight the region specified
				gChromoMap.highlightedRegionStart = intervalStart;
				gChromoMap.highlightedRegionEnd = intervalEnd;
				gChromoMap.highlightChromomapRegion = true;
				
				//turn off potential mouseover highlight feature label drawing
				gChromoMap.drawMouseOverFeatures = false;

				//don't draw selection rectangle
				gChromoMap.drawSelectionRect = false;
				
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
				FoundFeaturesTableControlPanel controlPanel = MapViewer.winMain.foundFeaturesTableControlPanel;
				controlPanel.setVisible(true);
				controlPanel.getGenomeLabel().setText(genome);
				controlPanel.getChromoLabel().setText(chromosome);
				controlPanel.getRegionStartLabel().setText(new Float(intervalStart).toString());
				controlPanel.getRegionEndLabel().setText(new Float(intervalEnd).toString());
				controlPanel.getNumberFeaturesLabel().setText(new Integer(containedFeatures.size()).toString());
				
				//sync the checkboxes states with those in the find dialog itself to make sure they show the same value
				controlPanel.getShowLabelsCheckbox().setSelected(MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().isSelected());
				controlPanel.getShowHomologsCheckbox().setSelected(MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().isSelected());
				
				//earmark the features for drawing on repaint
				MapViewer.winMain.mainCanvas.drawFoundFeaturesInRange = true;	
				//repaint the canvas so we can see the highlighted region which should then be coloured in differently
				MapViewer.winMain.mainCanvas.updateCanvas(true);
				
				//now put the results into the JTable held by the results panel
				updateResultsTable(containedFeatures);
				
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
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static  void findFeaturesByName(FindFeaturesDialog findFeaturesDialog)
	{
		try
		{		
			//first reset the canvas to its default view
			MapViewer.winMain.fatController.resetMainCanvasView();		
			
			//this array holds all the names of the features we need to display
			String [] allNames = new String[0];
			String input =  findFeaturesDialog.ffPanel.getFFTextArea().getText();	
			//parse inputFile 
			allNames = input.split("\n");	
			
			//get the corresponding feature objects
			Vector<Feature> features = new Vector<Feature>(allNames.length);
			for (int i = 0; i < allNames.length; i++)
			{
				features.add(Utils.getFeatureByName(allNames[i].trim()));
			}
			
			//we have found features
			if (features.size() > 0)
			{				
				//set the results panel to be visible
				findFeaturesDialog.setVisible(false);
				MapViewer.winMain.splitPane.setDividerSize(Constants.SPLITPANE_DIVIDER_SIZE);
				int newDividerLocation = (int) (MapViewer.winMain.getHeight() * 0.66f);
				MapViewer.winMain.splitPane.setDividerLocation(newDividerLocation);
				
				//now put the results into the JTable held by the results panel
				updateResultsTable(features);
				
				//hide the control panel for the results table as it is not needed with this kind of results
				MapViewer.winMain.foundFeaturesTableControlPanel.setVisible(false);
				
				// validate and repaint the canvas so it knows it has been resized
				MapViewer.winMain.validate();
				MapViewer.winMain.mainCanvas.updateCanvas(true);
			}
			//we have not found any features
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
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//insert the results into the JTable held by the results panel
	private static void updateResultsTable(Vector<Feature> features)
	{
		LinkedList<Link> linksFound = Utils.getLinksForFeatures(features);

		LinkedList<ResultsTableEntry> tableEntries = TableEntriesGenerator.makeTableEntries(features);
		HomologResultsTableModel homologResultsTableModel = new HomologResultsTableModel(tableEntries);
		ResultsTable resultsTable = (ResultsTable)MapViewer.winMain.ffResultsPanel.getFFResultsTable();
		resultsTable.setModel(homologResultsTableModel);
		
		//size the columns and the dialog containing the table appropriately
		((ResultsTable)MapViewer.winMain.ffResultsPanel.getFFResultsTable()).initColumnSizes();
		
		//enable the button that allows export of this data to file
		MapViewer.winMain.toolbar.bSave.setEnabled(true);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}