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
	
	@Override
	public void run()
	{
		try
		{
			//the total number of frames we need to render
			int totalFrames = Math.round(fps * (millis / 1000.0f));
			
			//angle we want to draw at
			float endAngle = -1;
			if(invertMap.angleOnZAxis == 90)
				endAngle = -90;
			else
				endAngle = 90;
			float interval = (endAngle -  invertMap.angleOnZAxis) / totalFrames;
			
			invertMap.inversionInProgress = true;
			
			// now loop for the number of total frames, zooming in by a bit each time
			for (int i = 0; i < totalFrames; i++)
			{
				//increment angle
				invertMap.angleOnZAxis += interval;
				
				if( invertMap.angleOnZAxis < 90 &&  invertMap.angleOnZAxis > -90)
				{
					invertMap.isPartlyInverted = true;
					invertMap.isFullyInverted = false;
				}
				else
				{
					invertMap.isPartlyInverted = false;
					
					if(invertMap.angleOnZAxis == -90)
						invertMap.isFullyInverted = true;
					else if (invertMap.angleOnZAxis == 90)
						invertMap.isFullyInverted = false;
				}
							
				//repaint
				Strudel.winMain.mainCanvas.updateCanvas(true);
				
				// sleep for the amount of animation time divided by the totalFrames value
				try
				{
					Thread.sleep((millis / totalFrames));
				}
				catch (InterruptedException e)
				{
				}
			}
			
			//due to rounding errors in the calculation we can end with slightly more or less than angles of 90 or -90
			//check and correct if necessary
			if(invertMap.angleOnZAxis < 0 && invertMap.angleOnZAxis != -90)
				 invertMap.angleOnZAxis = -90;
			else if(invertMap.angleOnZAxis > 0 && invertMap.angleOnZAxis != 90)
				 invertMap.angleOnZAxis = 90;			
			
			//check whether the map is now inverted
			if(invertMap.angleOnZAxis == -90)
				invertMap.isFullyInverted = true;
			else if (invertMap.angleOnZAxis == 90)
				invertMap.isFullyInverted = false;
			
			//reset the other flags
			invertMap.isPartlyInverted = false;		
			invertMap.inversionInProgress = false;
						
			//update the position lookup arrays for mouseover
			Strudel.winMain.fatController.initialisePositionArrays();
			
			//repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
		catch (RuntimeException e)
		{
		}
	}
}
