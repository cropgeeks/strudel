package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

class OverviewDialog extends JDialog
{
	private WinMain winMain;

	OverviewDialog(WinMain winMain)
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
			public void componentResized(ComponentEvent e)
			{
				Prefs.guiOverviewWidth  = getSize().width;
				Prefs.guiOverviewHeight = getSize().height;
				Prefs.guiOverviewX = getLocation().x;
				Prefs.guiOverviewY = getLocation().y;
			}

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
			public void windowClosing(WindowEvent e)
			{
				winMain.toolbar.toggleOverviewDialog();
			}
		});
	}

	void createLayout()
	{
		setLayout(new GridLayout(1, winMain.dataContainer.numRefGenomes+1));
	}

	void addCanvas(OverviewCanvas canvas)
	{
		add(canvas);
	}
}