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
		//open the file dialog
		OpenFileDialog openFileDialog = Strudel.winMain.openFileDialog;
		openFileDialog.setLocationRelativeTo(Strudel.winMain);
		openFileDialog.setVisible(true);
		
		//clear the text fields, in case they had text showing previously
		openFileDialog.openFilesPanel.getInputFileTF().setText("");
	
	}
	
}
