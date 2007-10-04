package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ControlPanel extends JPanel
{

	public ControlPanel(JTabbedPane tabbedPane)
	{
		
		ViewChooserPanel viewChooserPanel = new ViewChooserPanel(tabbedPane);
		this.add(viewChooserPanel);
	}

}
