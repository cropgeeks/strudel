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
	public String showAnnotationStr = "Show annotation for features in range";
	public String selectAllChromosStr = "Select all chromosomes in genome";

	public JMenuItem invertChromoMenuItem;
	public JMenuItem fitChromoMenuItem;
	public JMenuItem showAnnotationItem;
	public JCheckBoxMenuItem showAllLabelsItem;
	public JMenuItem selectAllChromosItem;


	public ChromoContextPopupMenu()
	{
		invertChromoMenuItem = new JMenuItem(invertChromoStr);
		invertChromoMenuItem.addActionListener(this);
		add(invertChromoMenuItem);

		fitChromoMenuItem = new JMenuItem(fitChromoStr);
		fitChromoMenuItem.addActionListener(this);
		add(fitChromoMenuItem);

		showAnnotationItem = new JMenuItem(showAnnotationStr);
		showAnnotationItem.addActionListener(this);
		add(showAnnotationItem);

		showAllLabelsItem = new JCheckBoxMenuItem(showAllLabelsStr);
		showAllLabelsItem.addActionListener(this);
		add(showAllLabelsItem);

		selectAllChromosItem = new JMenuItem(selectAllChromosStr);
		selectAllChromosItem.addActionListener(this);
		add(selectAllChromosItem);
	}


	public void actionPerformed(ActionEvent e)
	{
		JMenuItem source = (JMenuItem)(e.getSource());

		//first find out what chromosome this relates to
		GChromoMap selectedMap = Strudel.winMain.fatController.selectedMap;

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
		
		else if(source.equals(showAnnotationItem))
		{
			//display annotation for the range selected
			if(selectedMap != null)
			{
				FeatureSearchHandler.findFeaturesInRangeFromCanvasSelection();				
				//update the label that says how many features are contained in the results table
				Strudel.winMain.foundFeaturesTableControlPanel.getNumberFeaturesLabel().setText(new Integer(Strudel.winMain.ffResultsPanel.resultsTable.getVisibleEntries().size()).toString());
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
		
		else if (source.equals(selectAllChromosItem))
		{
			//set the appropriate flags to indicate what we are doing
			selectedMap.owningSet.wholeMapsetIsSelected = true;
			Strudel.winMain.fatController.isCtrlClickSelection = false;

			Strudel.winMain.mainCanvas.linkDisplayManager.processLinkDisplayRequest(selectedMap);
			//repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
	}
}
