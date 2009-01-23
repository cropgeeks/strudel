package sbrn.mapviewer.gui.components;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.animators.*;
import sbrn.mapviewer.gui.entities.*;


public class ChromoContextPopupMenu extends JPopupMenu implements ActionListener
{
	String invertChromoStr = "Invert chromosome";
	String addAllFeaturesStr = "Add features in range to results";
	
	public JMenuItem invertChromoMenuItem;
	public JMenuItem addAllFeaturesItem;
	
	
	public ChromoContextPopupMenu()
	{
		invertChromoMenuItem = new JMenuItem(invertChromoStr);
		invertChromoMenuItem.addActionListener(this);
		add(invertChromoMenuItem);
		
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
			int fps = 20;
			// the length of time we want the animation to last in milliseconds
			int millis = 1000;
			ChromoZAxisInversionAnimator chromoInversionAnimator = new ChromoZAxisInversionAnimator(MapViewer.winMain.fatController.invertMap, fps, millis);
			chromoInversionAnimator.start();
		}
		else if(source.equals(addAllFeaturesItem))
		{
			//first find out what chromosome this relates to
			GChromoMap selectedMap = MapViewer.winMain.fatController.selectionMap;
			
			int selectionRectTopY = (int)selectedMap.selectionRect.getY();
			int selectionRectBottomY = (int)(selectedMap.selectionRect.getY() + selectedMap.selectionRect.getHeight());

			//add features from the selected region into the results table
			if(selectedMap != null)
			{
				MapViewer.winMain.ffResultsPanel.resultsTable.addFeaturesFromSelectedMap(selectedMap, selectionRectTopY, selectionRectBottomY);
				
				//store the current selection rectangle as part of the chromosome now
				//this is so it can move with the chromosome when the user scrolls or zooms
				selectedMap.selectionRectTopY = selectionRectTopY;
				selectedMap.selectionRectBottomY = selectionRectBottomY;		
				//this time we get the chromosome to paint the selection rectangle, not the canvas
				selectedMap.drawSelectionRect = true;		
			}
			
			//turn antialiasing on and repaint			
			MapViewer.winMain.mainCanvas.antiAlias = true;
			MapViewer.winMain.mainCanvas.updateCanvas(true);			
		}
		
	}
	
}
