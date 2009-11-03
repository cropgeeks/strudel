package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.dialog.*;



public class FindFeaturesInRangeAction extends AbstractAction
{

	public void actionPerformed(ActionEvent e)
	{
	
		//clear the dialog
		FindFeaturesInRangeDialog featuresInRangeDialog = MapViewer.winMain.ffInRangeDialog; 
		
		featuresInRangeDialog.ffInRangePanel.getRangeStartSpinner().setValue(0);
		featuresInRangeDialog.ffInRangePanel.getRangeEndSpinner().setValue(0);
		featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(0);
		featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(0);
		
		//reset the main canvas
		MapViewer.winMain.fatController.resetMainCanvasView();	
		
		//show the dialog
		featuresInRangeDialog.setLocationRelativeTo(MapViewer.winMain);
		featuresInRangeDialog.setVisible(true);

	}
	
}
