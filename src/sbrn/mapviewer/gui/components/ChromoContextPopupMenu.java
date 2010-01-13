package sbrn.mapviewer.gui.components;

import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.animators.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;


public class ChromoContextPopupMenu extends JPopupMenu implements ActionListener
{
	public String invertChromoStr = "Invert chromosome";
	public String fitChromoStr = "Fit chromosome on screen";
	public String showAllLabelsStr = "Show all labels";
	//	public String hideAllLabelsStr = "Hide all labels";
	public String addAllFeaturesStr = "Add features in range to results";
	public String webInfoStr = "Show annotation for features in range";

	public JMenuItem invertChromoMenuItem;
	public JMenuItem fitChromoMenuItem;
	public JMenuItem addAllFeaturesItem;
	public JCheckBoxMenuItem showAllLabelsItem;


	public ChromoContextPopupMenu()
	{
		invertChromoMenuItem = new JMenuItem(invertChromoStr);
		invertChromoMenuItem.addActionListener(this);
		add(invertChromoMenuItem);

		fitChromoMenuItem = new JMenuItem(fitChromoStr);
		fitChromoMenuItem.addActionListener(this);
		add(fitChromoMenuItem);

		addAllFeaturesItem = new JMenuItem(addAllFeaturesStr);
		addAllFeaturesItem.addActionListener(this);
		add(addAllFeaturesItem);

		showAllLabelsItem = new JCheckBoxMenuItem(showAllLabelsStr);
		showAllLabelsItem.addActionListener(this);
		add(showAllLabelsItem);
	}


	public void actionPerformed(ActionEvent e)
	{
		JMenuItem source = (JMenuItem)(e.getSource());

		//first find out what chromosome this relates to
		GChromoMap selectedMap = Strudel.winMain.fatController.selectionMap;
		GMapSet gMapSet = selectedMap.owningSet;

		if(source.equals(invertChromoMenuItem))
		{
			//invert the whole chromosome
			// frame rate
			int fps = 25;
			// the length of time we want the animation to last in milliseconds
			int millis = 1000;
			ChromoZAxisInversionAnimator chromoInversionAnimator = new ChromoZAxisInversionAnimator(Strudel.winMain.fatController.invertMap, fps, millis);
			chromoInversionAnimator.start();
		}
		else if (source.equals(fitChromoMenuItem))
		{
			//fill the screen with the chromosome
			if (selectedMap != null)
				Strudel.winMain.mainCanvas.zoomHandler.processClickZoomRequest(selectedMap);
		}
		else if(source.equals(addAllFeaturesItem))
		{
			//add features from the selected region into the results table
			if(selectedMap != null)
			{
				//check whether we have an existing set of results
				boolean resultExists = Strudel.winMain.ffResultsPanel.resultsTable.getModel().getRowCount() > 0;
				//if yes, add the features from the current selection - otherwise make a new results table
				if(resultExists)
					Strudel.winMain.ffResultsPanel.resultsTable.addFeaturesFromSelectedMap(selectedMap);
				else
					FeatureSearchHandler.findFeaturesInRangeFromCanvasSelection();
			}

			//repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
		else if(source.equals(showAllLabelsItem))
		{
			//if the selected map is not currently showing all labels, set its boolean to true and show the item as selected
			if(selectedMap.alwaysShowAllLabels == false)
			{
				selectedMap.alwaysShowAllLabels = true;
				showAllLabelsItem.setSelected(true);
			}
			//otherwise set the boolean to false and deselect the item
			else
			{
				selectedMap.alwaysShowAllLabels = false;
				showAllLabelsItem.setSelected(false);
			}

			//repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
	}
}
