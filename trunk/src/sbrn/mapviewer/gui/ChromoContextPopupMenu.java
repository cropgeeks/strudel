package sbrn.mapviewer.gui;


import java.awt.event.*;

import javax.swing.*;


public class ChromoContextPopupMenu extends JPopupMenu implements ActionListener
{
	String invertChromoStr = "Invert chromosome";
	
	public ChromoContextPopupMenu()
	{
		JMenuItem menuItem = new JMenuItem(invertChromoStr);
		menuItem.addActionListener(this);
		add(menuItem);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		JMenuItem source = (JMenuItem)(e.getSource());
		
		if(source.getText().equals(invertChromoStr))
		{
			//invert the whole chromosome
			// frame rate
			int fps = 30;
			// the length of time we want the animation to last in milliseconds
			int millis = 1000;
			ChromoZAxisInversionAnimator chromoInversionAnimator = new ChromoZAxisInversionAnimator(MapViewer.winMain.fatController.invertMap, fps, millis);
			chromoInversionAnimator.start();
		}
	}
	
}
