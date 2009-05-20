package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	// =================================vars=======================================
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	public MTDataLoadingDialog dataLoadingDialog;
	
	File targetData, refGenome1FeatData, refGenome1HomData, refGenome2FeatData, refGenome2HomData;
	
	// =================================c'tor=======================================
	
	public OpenFileDialog()
	{
		super(MapViewer.winMain, "Load data", true);
		
		add(openFilesPanel);
		add(createButtons(), BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bOpen);
		SwingUtils.addCloseHandler(this, bCancel);
		
		pack();
		setResizable(true);
	}
	
	// ======================================methods=================================
	
	private JPanel createButtons()
	{
		bOpen = SwingUtils.getButton("Load data");
		bOpen.addActionListener(this);
		bOpen.setMnemonic(KeyEvent.VK_L);
		
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		bCancel.setMnemonic(KeyEvent.VK_C);
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bOpen);
		p1.add(bCancel);
		
		return p1;
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOpen)
		{
			// hide the open file dialog
			setVisible(false);
					
			//load the data in a separate thread
			loadDataInThread();
		}
		
		else if (e.getSource() == bCancel)
		{
			// hide the dialog
			setVisible(false);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void loadDataInThread()
	{
		dataLoadingDialog = new MTDataLoadingDialog(MapViewer.winMain, false);
		
		//first check that we have at least one pointer at a file with target feature data -- the bare minimum to run this application
		//missing target data file	

		//if the user wants to load their own data we need to check they have provided the correct file combination
		if(MapViewer.winMain.fatController.loadOwnData)
			checkUserInput();
		//else we just use the example data provided further down the call stack
		
		MapViewer.logger.fine("targetData in loadDataInThread = " + targetData);
		
		//then load the data in a separate thread
		DataLoadThread dataLoadThread = new DataLoadThread(targetData, refGenome1FeatData, refGenome1HomData, refGenome2FeatData, refGenome2HomData);
		dataLoadThread.start();
		
		//show a dialog with a progress bar
		dataLoadingDialog.setLocationRelativeTo(MapViewer.winMain);
		dataLoadingDialog.setVisible(true);

	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	private void checkUserInput()
	{
		//for each file, check whether we have a file chosen by the user -- if not, the respective
		//text field should be empty
		if(!openFilesPanel.getTargetfeatFileTF().getText().equals(""))
			targetData = new File(openFilesPanel.getTargetfeatFileTF().getText());				
		if(!openFilesPanel.getRefGen1FeatFileTF().getText().equals(""))
			refGenome1FeatData = new File(openFilesPanel.getRefGen1FeatFileTF().getText());				
		if(!openFilesPanel.getRefGen1HomFileTF().getText().equals(""))
			refGenome1HomData = new File(openFilesPanel.getRefGen1HomFileTF().getText());				
		if(!openFilesPanel.getRefGen2FeatFileTF().getText().equals(""))
			refGenome2FeatData = new File(openFilesPanel.getRefGen2FeatFileTF().getText());				
		if(!openFilesPanel.getRefGen2HomFileTF().getText().equals(""))
			refGenome2HomData = new File(openFilesPanel.getRefGen2HomFileTF().getText());		
		
		MapViewer.logger.fine("targetData in checkUserInput = " + targetData);
		
		//check whether user has specified files correctly				
		//missing target data file
		if(targetData == null)
		{
			String errorMessage = "The target data file has not been specified. Please try again.";
			TaskDialog.error(errorMessage, "Close");
			setVisible(true);
			return;
		}			
		//if reference datasets are to be used, we need to have both the feature file and the homology file
		//for each of them
		if(refGenome1FeatData != null && refGenome1HomData == null ||
						refGenome1FeatData == null && refGenome1HomData != null ||
						refGenome2FeatData != null && refGenome2HomData == null ||
						refGenome2FeatData == null && refGenome2HomData != null)
		{
			String errorMessage = "One of the files required for a reference genome has not been specified. Please specify both the feature file and the homology file.";
			TaskDialog.error(errorMessage, "Close");
			setVisible(true);
			return;
		}
	}
	
}// end class
