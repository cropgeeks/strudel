package sbrn.mapviewer.gui;

import sbrn.mapviewer.gui.entities.*;

public class ChromoInversionAnimator extends Thread
{
	GChromoMap invertMap;
	int fps;
	int millis;
	
	public ChromoInversionAnimator(GChromoMap invertMap, int fps, int millis)
	{
		super();
		this.invertMap = invertMap;
		this.fps = fps;
		this.millis = millis;
	}
	
	public void run()
	{
		try
		{
			System.out.println("inverting chromo " + invertMap.name);
			
			//turn antialiasing off
			MapViewer.winMain.mainCanvas.antiAlias = false;		
			//turn link drawing off
			MapViewer.winMain.mainCanvas.drawLinks = false;		
			
			//the total number of frames we need to render
			float totalFrames = fps * (millis / 1000);
			
			//angle we want to draw at
			float currentAngle = 0;
			float endAngle = 180;
			float interval = (endAngle - currentAngle) / totalFrames;
			
			invertMap.inversionInProgress = true;
			
			// now loop for the number of total frames, zooming in by a bit each time
			for (int i = 0; i < totalFrames; i++)
			{
				//increment angle
				currentAngle = currentAngle + interval;
				
				//set the angle fro drawing the map on the map object itself
				invertMap.angleFromVertical = currentAngle;
				
				//repaint
				MapViewer.winMain.mainCanvas.repaint();
				
				// sleep for the amount of animation time divided by the totalFrames value
				try
				{
					Thread.sleep((long) (millis / totalFrames));
				}
				catch (InterruptedException e)
				{
				}
			}
			
			//flag up the fact that this chromoMap is now inverted but check first whether it is already inverted
			//in that case it will now be the right way up again
			if(invertMap.isInverted)
				invertMap.isInverted = false;
			else
				invertMap.isInverted = true;
			
			invertMap.inversionInProgress = false;
			
			//turn antialiasing back on
			MapViewer.winMain.mainCanvas.antiAlias = true;		
			//turn link drawing back on
			MapViewer.winMain.mainCanvas.drawLinks = true;	
			
			//update the position lookup arrays for mouseover
			MapViewer.winMain.fatController.initialisePositionArrays();
			
			MapViewer.winMain.mainCanvas.repaint();
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
}
