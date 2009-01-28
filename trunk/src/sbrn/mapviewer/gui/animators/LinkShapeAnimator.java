package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.components.*;

public class LinkShapeAnimator extends Thread
{
	
	//frame rate per seconds
	int fps = 20;
	int millis = 500;
	boolean straighten;
	
	public LinkShapeAnimator(boolean straighten)
	{
		this.straighten = straighten;
	}

	
	public void run()
	{
		//turn antialiasing off
		MapViewer.winMain.mainCanvas.antiAlias = false;

		//the total number of frames we need to render
		int totalFrames = Math.round(fps * (millis / 1000.0f));
		
		//work out the curvature coefficient increment over the range we need to cover
		float coefficientIncrement = Constants.MAX_CURVATURE_COEFF / totalFrames;

		// now loop for the number of total frames, zooming in by a bit each time
		for (int i = 0; i < totalFrames; i++)
		{
			// sleep for the amount of animation time divided by the totalFrames value
			try
			{
				Thread.sleep((long) (millis / totalFrames));
			}
			catch (InterruptedException e)
			{
			}
			
			//adjust the curvature coefficient by its increment
			if(straighten)
			{
				MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff -= coefficientIncrement;
			}
			else
			{
				MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff += coefficientIncrement;
			}
			
			//do the repaint
			MapViewer.winMain.mainCanvas.updateCanvas(true);		
		}
		
		AntiAliasRepaintThread antiAliasRepaintThread = new AntiAliasRepaintThread();
		antiAliasRepaintThread.start();
	}
	
}
