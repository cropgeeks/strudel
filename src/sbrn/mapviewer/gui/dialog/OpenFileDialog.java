package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	// =================================vars=======================================
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	MTDataLoadingDialog dataLoadingDialog;
	
	// =================================c'tor=======================================
	
	public OpenFileDialog()
	{
		super(MapViewer.winMain, "Open data files", true);
		
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
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		
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
		
		//start the load in a separate thread
		Runnable runnable = new Runnable()
		{
			public void run()
			{			

//				//it may seem idiotic to do this but the data loading can be so quick the user does not even get to read the message and
//				//may then assume they have missed something important
//				//for the sake of clarity let the thread sleep for long enough for them to at least read the message 
//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch (InterruptedException e)
//				{
//				}
				
				//init the new data set 
				MapViewer.winMain.fatController.initialiseNewProject();				

				//hide the data loading progress dialog
				if(dataLoadingDialog != null)
					dataLoadingDialog.setVisible(false);
				
				// revalidate the GUI
//				MapViewer.winMain.showStartPanel(false);
				MapViewer.winMain.validate();

			}
		};	
		Thread thread = new Thread(runnable);
		thread.start();
		
		//show a dialog with a progress bar
		dataLoadingDialog.setLocationRelativeTo(MapViewer.winMain);
		dataLoadingDialog.setVisible(true);

	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
