package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class ChromoZAxisInversionAnimator extends Thread
{
	GChromoMap invertMap;
	int fps;
	int millis;
	
	public ChromoZAxisInversionAnimator(GChromoMap invertMap, int fps, int millis)
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
			//turn antialiasing off
			MapViewer.winMain.mainCanvas.antiAlias = false;		
			
			//the total number of frames we need to render
			int totalFrames = Math.round(fps * (millis / 1000.0f));
			
			//angle we want to draw at
			float currentAngle = 90;
			float endAngle = -90;
			float interval = (endAngle - currentAngle) / totalFrames;
			
			invertMap.inversionInProgress = true;
			
			// now loop for the number of total frames, zooming in by a bit each time
			for (int i = 0; i < totalFrames; i++)
			{
				//increment angle
				currentAngle = currentAngle + interval;
				//set the angle for drawing the map on the map object itself
				invertMap.angleFromVertical = currentAngle;

				if(invertMap.isFullyInverted)
				{
					if(currentAngle > 0)
						invertMap.isPartlyInverted = true;
					else
						invertMap.isPartlyInverted = false;
				}
				else
				{
					if(currentAngle < 0)
						invertMap.isPartlyInverted = true;
					else
						invertMap.isPartlyInverted = false;
				}
				
				//repaint
				MapViewer.winMain.mainCanvas.updateCanvas(true);
				
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
			if(invertMap.isFullyInverted)
				invertMap.isFullyInverted = false;
			else
				invertMap.isFullyInverted = true;
			
			invertMap.inversionInProgress = false;		
			
			//update the position lookup arrays for mouseover
			MapViewer.winMain.fatController.initialisePositionArrays();			
			
			//turn antialiasing back on
			MapViewer.winMain.mainCanvas.antiAlias = true;	
			//repaint
			MapViewer.winMain.mainCanvas.updateCanvas(true);			
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
}
