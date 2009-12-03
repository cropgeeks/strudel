package sbrn.mapviewer.gui.animators;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;

public class LinkShapeAnimator extends Thread
{
	
	//frame rate per seconds
	int fps = 25;
	int millis = 300;
	boolean straighten;
	int linkType;
	
	public LinkShapeAnimator(int linkType)
	{
		this.linkType = linkType;
	}

	
	public void run()
	{
		//first of all disable the button that triggered this thread so that the user cannot start multiple instances of this
		Strudel.winMain.toolbar.bCurves.setEnabled(false);
		
		//turn antialiasing off
		Strudel.winMain.mainCanvas.antiAlias = false;

		//the total number of frames we need to render
		int totalFrames = Math.round(fps * (millis / 1000.0f));
		
		float curvatureCoefficient;
		//draw straight links
		if(linkType == Constants.LINKTYPE_STRAIGHT)
		{
				curvatureCoefficient = 0;
		}
		//draw angled links
		else  if(linkType == Constants.LINKTYPE_ANGLED)
		{
			curvatureCoefficient = Constants.MAX_ANGLEDLINK_COEFF;
		}
		//draw curved links
		else
		{
			curvatureCoefficient = Constants.MAX_CURVEDLINK_COEFF;
		}

		//work out the curvature coefficient increment over the range we need to cover
		float differential;
		if(Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff > curvatureCoefficient)
			differential = Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff - curvatureCoefficient;
		else
			differential = curvatureCoefficient - Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff;
		float coefficientIncrement = differential / totalFrames;

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

			//straight links
			if(linkType == Constants.LINKTYPE_STRAIGHT)
			{
				Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff -= coefficientIncrement;
				
				//as a precaution, make sure that the last iteration of this loop will set the value of the link shape coefficient to the
				// minimum so that the lines are definitely straight
				//this is so we don't ever exceed the value in either direction
				if(i == totalFrames -1)
					Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff = 0;
			}
			//angled links
			else  if(linkType == Constants.LINKTYPE_ANGLED)
			{
				Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff += coefficientIncrement;
				
				//as a precaution, make sure that the last iteration of this loop will set the value of the link shape coefficient to the
				// maximum so that the lines are definitely angled
				//this is so we don't ever exceed the value in either direction
				if(i == totalFrames -1)
					Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff = curvatureCoefficient;
			}
			//curved links
			else
			{
				Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff += coefficientIncrement;
				
				//as a precaution, make sure that the last iteration of this loop will set the value of the link shape coefficient to the
				// maximum so that the lines are definitely curved
				//this is so we don't ever exceed the value in either direction
				if(i == totalFrames -1)
					Strudel.winMain.mainCanvas.linkDisplayManager.linkShapeCoeff = Constants.MAX_CURVEDLINK_COEFF;
			}

			//do the repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);		
		}
		
		//re-enable the button that triggered this thread
		Strudel.winMain.toolbar.bCurves.setEnabled(true);
		
		//repaint
		Utils.repaintAntiAliased();		
	}
	
}
