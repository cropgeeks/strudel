package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	// =================================vars=======================================
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	
	
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
			openFiles();
		}
		
		else if (e.getSource() == bCancel)
		{
			// hide the dialog
			setVisible(false);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	private void openFiles()
	{
		try
		{
			MapViewer.winMain.fatController.initialiseNewProject();
			
			//hide the startpanel and show the main canvas instead
			MapViewer.winMain.showStartPanel(false);
			MapViewer.winMain.mainCanvas.setVisible(true);
			MapViewer.winMain.mainCanvas.updateCanvas(true);
			MapViewer.winMain.repaint();
			
			// hide the dialog
			setVisible(false);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
