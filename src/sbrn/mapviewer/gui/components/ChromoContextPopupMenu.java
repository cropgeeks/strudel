package sbrn.mapviewer.gui.components;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.animators.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;


public class ChromoContextPopupMenu extends JPopupMenu implements ActionListener
{
	String invertChromoStr = "Invert chromosome";
	String fitChromoStr = "Fit chromosome on screen";
	public String addAllFeaturesStr = "Add features in range to results";
	public String webInfoStr = "Show annotation for features in range";
	
	public JMenuItem invertChromoMenuItem;
	public JMenuItem fitChromoMenuItem;
	public JMenuItem addAllFeaturesItem;
	
	
	
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
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		JMenuItem source = (JMenuItem)(e.getSource());
		
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
			GChromoMap selectedMap = Strudel.winMain.fatController.selectionMap;

			if (selectedMap != null)
				Strudel.winMain.mainCanvas.zoomHandler.processClickZoomRequest(selectedMap);
		}
		else if(source.equals(addAllFeaturesItem))
		{
			//first find out what chromosome this relates to
			GChromoMap selectedMap = Strudel.winMain.fatController.selectionMap;

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
			
			//turn antialiasing on and repaint			
			Strudel.winMain.mainCanvas.antiAlias = true;
			Strudel.winMain.mainCanvas.updateCanvas(true);			
		}
		
	}
	
}