package sbrn.mapviewer.gui.handlers;

import java.util.*;
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

		GChromoMap gMap = Utils.getGMapByName(chromosome, genome);
		Strudel.winMain.fatController.selectedMap = gMap;
		findAndDisplayFeaturesInRange(gMap, intervalStart, intervalEnd, false);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public static void findFeaturesInRangeFromCanvasSelection()
	{
		//remember the selected map
		GChromoMap gMap = Strudel.winMain.fatController.selectedMap;
		
		//do not show the selection recatnlge or any links drawn during the preview
		Strudel.winMain.fatController.selectedMap.drawFeatureSelectionRectangle = false;
		Strudel.winMain.mainCanvas.drawLinksOriginatingInRange = false;
		
		//show the features in range
		findAndDisplayFeaturesInRange(gMap, gMap.relativeTopY, gMap.relativeBottomY, true);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private static void findAndDisplayFeaturesInRange(GChromoMap gChromoMap, float intervalStart, float intervalEnd, boolean isCanvasSelection)
	{
		try
		{
			ChromoMap chromoMap = gChromoMap.chromoMap;

			//we need to check that we have not exceeded the maximum value of the positions on the chromosome
			if(intervalEnd > chromoMap.getStop())
			{
				//if we have drawn beyond the end of the chromosome we just reset this value to the chromosome end value
				intervalEnd = chromoMap.getStop();
			}

			//also check the range start is less than the range end
			if(intervalEnd < intervalStart)
			{
				TaskDialog.error("The range start value is greater than the range end value.", "Close");
				return;
			}

			//get a list with names for all the features contained in this interval
			ArrayList<Feature> containedFeatures = new ArrayList<Feature>();
			for(Feature f : chromoMap.getFeatureList())
			{
//				boolean featureHasLinks = f.getLinks().size() > 0;
				//add the feature only if it is in the interval and has links or if the number of mapsets loaded is 1
				if((f.getStart() >= intervalStart) && (f.getStart() <= intervalEnd))
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

				//show the results table
				Strudel.winMain.showBottomPanel();

				// validate and repaint the canvas so it knows it has been resized
				Strudel.winMain.validate();
				Strudel.winMain.mainCanvas.updateCanvas(true);

				//now zoom into that range on the chromosome
				Strudel.winMain.mainCanvas.zoomHandler.zoomIntoRange(gChromoMap, intervalStart, intervalEnd, false);

				//we also need to set the labels on the control panel for the results to have the appropriate text
				FoundFeaturesTableControlPanel controlPanel = Strudel.winMain.foundFeaturesTableControlPanel;
				controlPanel.setVisible(true);
				controlPanel.getGenomeLabel().setText(gChromoMap.owningSet.name);
				controlPanel.getChromoLabel().setText(gChromoMap.name);
				controlPanel.getRegionStartLabel().setText(new Float(intervalStart).toString());
				controlPanel.getRegionEndLabel().setText(new Float(intervalEnd).toString());
				controlPanel.getNumberFeaturesLabel().setText(new Integer(containedFeatures.size()).toString());

				//sync the checkboxes states with those in the find dialog itself to make sure they show the same value
				controlPanel.getShowLabelsCheckbox().setSelected(Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().isSelected());
				controlPanel.getShowHomologsCheckbox().setSelected(Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().isSelected());

				//earmark the features for drawing on repaint
				Strudel.winMain.mainCanvas.drawFoundFeaturesInRange = true;
				//repaint the canvas so we can see the highlighted region which should then be coloured in differently
				Strudel.winMain.mainCanvas.updateCanvas(true);

				//now put the results into the JTable held by the results panel
				updateResultsTable(containedFeatures);

				//hide the dialog
				Strudel.winMain.ffInRangeDialog.setVisible(false);
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

	public static void findAllFeatures()
	{
		ArrayList<Feature> features = Utils.getAllFeatures();

		//show the results table
		Strudel.winMain.showBottomPanel();

		//now put the results into the JTable held by the results panel
		updateResultsTable(features);

		//earmark the features for drawing on repaint
		Strudel.winMain.mainCanvas.drawFeaturesFoundByName = true;

		// validate and repaint the canvas so it knows it has been resized
		Strudel.winMain.validate();
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//insert the results into the JTable held by the results panel
	private static void updateResultsTable(ArrayList<Feature> features)
	{
		ArrayList<ResultsTableEntry> tableEntries = TableEntriesGenerator.makeTableEntries(features);
		HomologResultsTableModel homologResultsTableModel = new HomologResultsTableModel(tableEntries);
		ResultsTable resultsTable = (ResultsTable)Strudel.winMain.ffResultsPanel.getFFResultsTable();
		resultsTable.setModel(homologResultsTableModel);

		//size the columns and the dialog containing the table appropriately
		((ResultsTable)Strudel.winMain.ffResultsPanel.getFFResultsTable()).initColumnSizes();

		//enable the button that allows export of this data to file
		Actions.saveResults.setEnabled(true);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}