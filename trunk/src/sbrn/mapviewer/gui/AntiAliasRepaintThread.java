package sbrn.mapviewer.gui;

public class AntiAliasRepaintThread extends Thread
{
	//sleeps for a minimal amount of time to create a short delay for repainting, then repaints with antialias on
	//used for prettier redraws after aliased drawing (in some situations)
	public void run()
	{
		try{Thread.sleep(10);}catch(InterruptedException x){}
		MapViewer.winMain.mainCanvas.antiAlias = true;
		MapViewer.winMain.mainCanvas.updateCanvas(true);
	}
}
