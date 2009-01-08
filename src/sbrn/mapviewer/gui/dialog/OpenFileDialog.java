package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	public boolean dataLoaded = false;
	
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
	
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOpen)
		{
			openFiles();
		}
		
		else if (e.getSource() == bCancel)
		{
			//hide the dialog
			setVisible(false);
		}
	}
	
	
	private void openFiles()
	{
		try
		{					
			dataLoaded = true;
			MapViewer.winMain.mainCanvas.updateCanvas(true);
			//hide the dialog
			setVisible(false);		
			//show components that were initially not required
			MapViewer.winMain.zoomControlContainerPanel.setVisible(true);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
	
	
}
