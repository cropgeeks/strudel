package sbrn.mapviewer.gui.components;

import java.awt.*;

import javax.swing.*;

import sbrn.mapviewer.gui.*;

public class StartPanel extends JPanel
{
	//a JLabel instructing the user to open a data file
	JLabel openFileLabel;	
	String openFileLabelMessage = "Open a data file using the data button on the toolbar.";
	
	public StartPanel()
	{
		setUpOpenFileLabel();
	}
	
	private void setUpOpenFileLabel()
	{
		//a JLabel instructing the user to open a data file
		setLayout(new BorderLayout());
		openFileLabel = new JLabel(openFileLabelMessage);	
		openFileLabel.setIcon(Icons.getIcon("FILEOPEN"));
		openFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		openFileLabel.setFont(new Font("Sans-serif", Font.PLAIN, 18));
		add(openFileLabel, BorderLayout.CENTER);
		openFileLabel.setVisible(true);
	}
}
