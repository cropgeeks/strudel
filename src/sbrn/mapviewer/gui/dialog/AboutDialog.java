/*
 * AboutDialog.java
 *
 * Created on 05 February 2009, 09:59
 */

package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

/**
 * 
 * @author imilne
 */
public class AboutDialog extends javax.swing.JDialog
{
	ExampleDataInfoPanel exampleDataInfoPanel = new ExampleDataInfoPanel();
	AboutPanel aboutPanel = new AboutPanel();
	JButton closeButton = new JButton("Close");
	
	
	/** Creates new form AboutDialog */
	public AboutDialog(java.awt.Frame parent, boolean modal)
	{
		super(parent, modal);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("About Strudel", aboutPanel);
		tabbedPane.add("Example Data Sets", exampleDataInfoPanel);
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		add(BorderLayout.CENTER, tabbedPane);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(closeButton);
		add(BorderLayout.SOUTH, buttonPanel);

		
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		
		pack();
		setTitle("About Strudel");
		setResizable(true);
		setLocationRelativeTo(parent);
	}

	
	/**
	 * @param args
	 *                the command lie arguments
	 */
	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				AboutDialog dialog = new AboutDialog(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter()
				{
					public void windowClosing(java.awt.event.WindowEvent e)
					{
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	
}
