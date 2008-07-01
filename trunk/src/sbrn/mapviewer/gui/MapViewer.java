package sbrn.mapviewer.gui;

import javax.swing.*;

public class MapViewer
{
	
	public static WinMain winMain;
	
	public static void main(String[] args)
	{
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			winMain = new WinMain();
			winMain.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}