package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class FindFeaturesDialog extends JDialog implements ActionListener
{
	
	private JButton bFind, bCancel;
	public MTFindFeaturesPanel ffPanel = new MTFindFeaturesPanel();
	
	public FindFeaturesDialog()
	{
		super(Strudel.winMain, "Find features by name", true);
		
		add(ffPanel);
		add(createButtons(), BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);
		
		pack();
		setResizable(true);

	}
	
	
	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bFind.setMnemonic(KeyEvent.VK_F);
		
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		bCancel.setMnemonic(KeyEvent.VK_C);
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bFind);
		p1.add(bCancel);
		
		return p1;
	}
	
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFind)
		{
			Strudel.winMain.fatController.findFeaturesRequested = true;
			FeatureSearchHandler.findFeaturesByName(this);
		}
		
		else if (e.getSource() == bCancel)
		{
			//hide the find dialog
			setVisible(false);
			//clear the found features
			Strudel.winMain.fatController.highlightFeature = null;
			Strudel.winMain.fatController.highlightFeatureHomolog = null;
		}
	}
	
	

}
