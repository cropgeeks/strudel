package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;

public class FindFeaturesAction extends AbstractAction
{

	public void actionPerformed(ActionEvent e)
	{
		//reset the main canvas view to all its defaults 
		MapViewer.winMain.fatController.resetMainCanvasView();
		
		//clear the find dialog
		MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().setText("");
		
		//show the find dialog
		MapViewer.winMain.ffDialog.setLocationRelativeTo(MapViewer.winMain);
		
		MapViewer.winMain.ffDialog.setVisible(true);
		
	}
	
}
