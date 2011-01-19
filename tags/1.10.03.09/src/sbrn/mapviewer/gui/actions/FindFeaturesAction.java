package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;

public class FindFeaturesAction extends AbstractAction
{

	public void actionPerformed(ActionEvent e)
	{
		//reset the main canvas
		Strudel.winMain.fatController.resetMainCanvasView();
		Strudel.winMain.fatController.findFeaturesRequested = true;

		//clear the find dialog
		Strudel.winMain.ffDialog.ffPanel.getFFTextArea().setText("");

		//show the find dialog
		Strudel.winMain.ffDialog.setLocationRelativeTo(Strudel.winMain);
		Strudel.winMain.ffDialog.setVisible(true);
		Strudel.winMain.ffDialog.ffPanel.getFFTextArea().requestFocusInWindow();
	}

}
