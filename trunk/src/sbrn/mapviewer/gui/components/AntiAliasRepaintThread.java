package sbrn.mapviewer.gui.components;

import sbrn.mapviewer.*;

public class AntiAliasRepaintThread extends Thread
{
	int delayMillis = 500;
	
	//sleeps for a minimal amount of time to create a short delay for repainting, then repaints with antialias on
	//used for prettier redraws after aliased drawing (in some situations)
	public void run()
	{
		try{Thread.sleep(delayMillis);}catch(InterruptedException x){}

		MapViewer.logger.fine("AntiAliasRepaintThread redraw");
		
		MapViewer.winMain.mainCanvas.antiAlias = true;
		MapViewer.winMain.mainCanvas.updateCanvas(true);
	}
}
