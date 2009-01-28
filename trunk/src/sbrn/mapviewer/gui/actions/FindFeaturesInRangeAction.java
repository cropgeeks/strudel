package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.dialog.*;



public class FindFeaturesInRangeAction extends AbstractAction
{

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//reset the main canvas view to all its defaults 
		MapViewer.winMain.fatController.resetMainCanvasView();
		
		//clear the dialog
		FindFeaturesInRangeDialog featuresInRangeDialog = MapViewer.winMain.ffInRangeDialog; 
		
		featuresInRangeDialog.ffInRangePanel.getIntervalStartTextField().setText("");
		featuresInRangeDialog.ffInRangePanel.getIntervalEndTextField().setText("");
		featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(0);
		featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(0);
		
		////////////////////////////////THIS BIT JUST FOR EASE OF TESTING////////////////////////////////
		//TODO: remove code used for testing only
		featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(1);
		featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(2);
		featuresInRangeDialog.ffInRangePanel.getIntervalStartTextField().setText("12");
		featuresInRangeDialog.ffInRangePanel.getIntervalEndTextField().setText("23");
		
		//show the dialog
		featuresInRangeDialog.setLocationRelativeTo(MapViewer.winMain);
		featuresInRangeDialog.setVisible(true);
	}
	
}
