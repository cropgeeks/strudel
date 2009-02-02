package sbrn.mapviewer.gui.actions;

import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.components.*;

public class OpenFileDialogAction extends AbstractAction
{

	public OpenFileDialogAction()
	{
		super("openFileDialog");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		MapViewer.logger.fine("action event triggered -- opening file");
		
		//open the file dialog
		OpenFileDialog openFileDialog = MapViewer.winMain.openFileDialog;
		openFileDialog.setLocationRelativeTo(MapViewer.winMain);
		openFileDialog.setVisible(true);
		
		//clear the text fields, in case they had text showing previously
		openFileDialog.openFilesPanel.getTargetfeatFileTF().setText("");
		openFileDialog.openFilesPanel.getRefGen1FeatFileTF().setText("");
		openFileDialog.openFilesPanel.getRefGen1HomFileTF().setText("");
		openFileDialog.openFilesPanel.getRefGen2FeatFileTF().setText("");
		openFileDialog.openFilesPanel.getRefGen2HomFileTF().setText("");
		
	}
	
}
