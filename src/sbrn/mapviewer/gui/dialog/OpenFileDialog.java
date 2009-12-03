package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.io.*;
import scri.commons.gui.*;

public class OpenFileDialog extends JDialog implements ActionListener
{
	
	// =================================vars=======================================
	
	private JButton bOpen, bCancel;
	public MTOpenFilesPanel openFilesPanel = new MTOpenFilesPanel();
	File targetData, refGenome1FeatData, refGenome1HomData, refGenome2FeatData, refGenome2HomData;
	
	// =================================curve'tor=======================================
	
	public OpenFileDialog()
	{
		super(Strudel.winMain, "Load data", true);
		
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
			DataLoadUtils.loadDataInThread(DataLoadUtils.getUserInputFile(), false);
		}
		
		else if (e.getSource() == bCancel)
		{
			// hide the dialog
			setVisible(false);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	
}// end class
