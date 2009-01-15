package sbrn.mapviewer.gui;

import java.io.*;
import sbrn.mapviewer.gui.dialog.*;

public class DataLoaderThread extends Thread
{
	public boolean done = false;
	MTDataLoadingDialog dataLoadingDialog;
	
	public DataLoaderThread(MTDataLoadingDialog dataLoadingDialog)
	{
		this.dataLoadingDialog = dataLoadingDialog;
	}
	
	public void run()
	{
		MapViewer.winMain.fatController.initialiseNewProject();
		
		//hide the startpanel and show the main canvas instead
//		MapViewer.winMain.showStartPanel(false);
		MapViewer.winMain.mainCanvas.setVisible(true);
		MapViewer.winMain.mainCanvas.updateCanvas(true);
		MapViewer.winMain.repaint();
		
		// hide the dialog
		MapViewer.winMain.openFileDialog.setVisible(false);
		
		//hide data load progress dialog
//		if(dataLoadingDialog != null)
//			dataLoadingDialog.setVisible(false);
	}	
}
