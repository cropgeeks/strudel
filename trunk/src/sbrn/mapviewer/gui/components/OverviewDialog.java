package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class OverviewDialog extends JDialog
{
	private final WinMain winMain;

	public OverviewDialog(WinMain winMain)
	{
		super(winMain, "Overview", false);
		this.winMain = winMain;

		setSize(Prefs.guiOverviewWidth, Prefs.guiOverviewHeight);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Prefs.isFirstRun || Prefs.guiOverviewX > (scrnW-50) || Prefs.guiOverviewY > (scrnH-50))
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiOverviewX, Prefs.guiOverviewY);

		addListeners();
	}

	private void addListeners()
	{
		// Monitor the size and location of the dialog on screen
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				Prefs.guiOverviewWidth  = getSize().width;
				Prefs.guiOverviewHeight = getSize().height;
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}
		});

		// If the window is closed by hand (as opposed to by the toolbar button)
		// we still need to track the event
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				winMain.toolbar.toggleOverviewDialog();
			}
		});
	}

	void createLayout()
	{
		setLayout(new GridLayout(1, Strudel.winMain.dataContainer.gMapSets.size()));
	}

}