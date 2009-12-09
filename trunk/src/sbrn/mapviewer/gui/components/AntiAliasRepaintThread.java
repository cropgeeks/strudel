package sbrn.mapviewer.gui.components;

import sbrn.mapviewer.*;

public class AntiAliasRepaintThread extends Thread
{
	//sleeps for a minimal amount of time to create a short delay for repainting, then repaints with antialias on
	//used for prettier redraws after aliased drawing (in some situations)
	public void run()
	{
		Strudel.winMain.mainCanvas.antiAlias = true;
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}
}
