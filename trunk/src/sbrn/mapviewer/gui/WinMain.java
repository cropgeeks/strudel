package sbrn.mapviewer.gui;

import javax.swing.JFrame;

public class WinMain extends JFrame
{
	MainCanvas mainCanvas;
	
	public WinMain()
	{
		setupComponents();
		
		// get the GUI set up
		setTitle("Map Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
	
	private void setupComponents()
	{
		add(new MainCanvas());
	}
}
