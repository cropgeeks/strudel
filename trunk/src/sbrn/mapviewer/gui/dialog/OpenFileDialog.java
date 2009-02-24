package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	// =================================vars=======================================
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	public MTDataLoadingDialog dataLoadingDialog;
	
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
				// load the data			
				//we do this by simply creating a new data container instance -- the actual data loading is done through this
				DataContainer dataContainer = new DataContainer();
				
				//data is loaded now -- check whether the user has cancelled
				if(!MapViewer.winMain.fatController.dataLoadCancelled)
				{
					MapViewer.logger.fine("data load successful -- proceeding");
					
					//point the reference in winMain at the new data container
					MapViewer.winMain.dataContainer  = dataContainer;
					//if users load datasets in succession we need to make sure we don't run out of memory
					//we want any old data containers to be thrown away
					//run the garbage collector explicitly to do this
					System.gc();
					
					//build the rest of the GUI as required
					if(!MapViewer.winMain.fatController.guiFullyAssembled)
						MapViewer.winMain.fatController.assembleRemainingGUIComps();
					else
						MapViewer.winMain.reinitialiseDependentComponents();
					
					//also need a new link display manager because it holds the precomputed links
					MapViewer.winMain.mainCanvas.linkDisplayManager = new LinkDisplayManager(MapViewer.winMain.mainCanvas);	
					
					//check if we need to enable some functionality -- depends on the number of genomes loaded
					//cannot do comparative stuff if user one loaded one (target) genome
					if(MapViewer.winMain.dataContainer.gMapSetList.size() == 1)
					{
						MapViewer.winMain.toolbar.bFindFeatures.setEnabled(false);
						MapViewer.winMain.toolbar.bFindFeaturesinRange.setEnabled(false);
					}
					else
					{
						MapViewer.winMain.toolbar.bFindFeatures.setEnabled(true);
						MapViewer.winMain.toolbar.bFindFeaturesinRange.setEnabled(true);
					}
					
					//hide the data loading progress dialog
					if(dataLoadingDialog != null)
						dataLoadingDialog.setVisible(false);
					
					//enable the rest of the controls
					MapViewer.winMain.toolbar.enableAllControls();
					
					//hide the start panel if it is still showing
					MapViewer.winMain.showStartPanel(false);

					// revalidate the GUI
					MapViewer.winMain.validate();
				}
				//user has cancelled
				else
				{
					MapViewer.logger.fine("data load cancelled -- nulling data container");			
				}
				
				//check the memory situation
				MapViewer.logger.fine("memory max (mb) = " + Runtime.getRuntime().maxMemory()/1024/1024);
				MapViewer.logger.fine("memory available = (mb) " + Runtime.getRuntime().freeMemory()/1024/1024);
				
				//reset the cancel flag as the user might now want to try again
				MapViewer.winMain.fatController.dataLoadCancelled = false;
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
