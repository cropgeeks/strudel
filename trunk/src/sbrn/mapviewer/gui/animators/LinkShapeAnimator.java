package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.*;

public class LinkShapeAnimator extends Thread
{
	

	int fps;
	int millis;
	boolean straighten;
	
	public LinkShapeAnimator(int fps, int millis, boolean straighten)
	{
		this.fps = fps;
		this.millis = millis;
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
		
		MapViewer.logger.fine("=================================");
		MapViewer.logger.fine("coefficientIncrement = " + coefficientIncrement);
		MapViewer.logger.fine("straighten = " + straighten);
		
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
				MapViewer.logger.fine("decrementing");
				MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff -= coefficientIncrement;
			}
			else
			{
				MapViewer.logger.fine("incrementing");
				MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff += coefficientIncrement;
			}
			
			//do the repaint
			MapViewer.logger.fine("linkCurvatureCoeff after adjustment = " + MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff);
			MapViewer.logger.fine("repainting");
			MapViewer.winMain.mainCanvas.updateCanvas(true);		
		}
		
		//do a final repaint with the correct end values to make sure everything is at the right value
		//adjust the curvature coefficient by its increment
		if(straighten)
		{
			MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff = 0;
		}
		else
		{
			MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff = Constants.MAX_CURVATURE_COEFF;
		}
		
		//do the repaint
		MapViewer.logger.fine("linkCurvatureCoeff after final adjustment = " + MapViewer.winMain.mainCanvas.linkDisplayManager.linkCurvatureCoeff);
		MapViewer.logger.fine("final repaint");
		MapViewer.winMain.mainCanvas.antiAlias = true;
		MapViewer.winMain.mainCanvas.updateCanvas(true);		
	}
	
}
