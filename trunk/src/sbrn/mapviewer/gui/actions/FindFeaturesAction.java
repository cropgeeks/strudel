package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;

public class FindFeaturesAction extends AbstractAction
{

	public void actionPerformed(ActionEvent e)
	{
		//clear the find dialog
//		MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().setText("");
		
		//show the find dialog
		MapViewer.winMain.ffDialog.setLocationRelativeTo(MapViewer.winMain);		
		MapViewer.winMain.ffDialog.setVisible(true);
		MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().requestFocusInWindow();
		
		
	}
	
}
