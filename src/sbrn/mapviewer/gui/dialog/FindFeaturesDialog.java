package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.data.Feature;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class FindFeaturesDialog extends JFrame implements ActionListener
{

	private JButton bFind, bCancel;
	private boolean isOK = false;

	public MTFindFeaturesPanel ffPanel = new MTFindFeaturesPanel();
	public MTFindFeaturesResultsPanel ffResultsPanel = new MTFindFeaturesResultsPanel();
	public JTabbedPane tabbedPane = new JTabbedPane();

	public FindFeaturesDialog()
	{
		super("Find Features");
		
		tabbedPane.addTab("Find",ffPanel);
		tabbedPane.addTab("Results",ffResultsPanel);
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		add(tabbedPane);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(MapViewer.winMain);
		setResizable(true);
	}


	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bCancel = SwingUtils.getButton("Close");
		bCancel.addActionListener(this);

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
			try
			{			
				//switch to the find tab
				tabbedPane.setSelectedIndex(0);
				
				//clear the found features, if any
				if(MapViewer.winMain.fatController.foundFeatures != null)
					MapViewer.winMain.fatController.foundFeatures.clear();
				if(MapViewer.winMain.fatController.foundFeatureHomologs != null)
					MapViewer.winMain.fatController.foundFeatureHomologs.clear();
				
				isOK = true;
				String allNames =  ffPanel.getFFTextArea().getText();		
				ffResultsPanel.getFFResultsList().setListData((allNames.split("\n")));

				//switch to the results tab
				tabbedPane.setSelectedIndex(1);
			}
			catch (RuntimeException e1)
			{
				e1.printStackTrace();
			}
		}

		else if (e.getSource() == bCancel)
		{
			setVisible(false);
			//clear the found features
			MapViewer.winMain.fatController.foundFeatures.clear();
			MapViewer.winMain.fatController.foundFeatureHomologs.clear();
		}
	}


}
