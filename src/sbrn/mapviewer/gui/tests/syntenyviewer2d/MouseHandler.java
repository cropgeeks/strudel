package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.event.*;

import javax.swing.event.*;

public class MouseHandler implements MouseInputListener
{

//========================================vars=============================================	
	
	Canvas canvas = null;
	
//========================================c'tor=============================================
	
	public MouseHandler(Canvas canvas)
	{
		this.canvas = canvas;
	}

//========================================methods=============================================	
	
	public void mouseMoved(MouseEvent arg0)
	{

		int x = arg0.getX();
		int y = arg0.getY();

		// check if mouse is now in area to the right of one of the reference chromosomes
		for (int i = 0; i < canvas.numChromos1; i++)
		{
			// work out the trigger area for each chromosome
			int triggerXMin = canvas.sideSpace;
			int triggerXMax = canvas.sideSpace + canvas.colSpacing;
			int triggerYMin = canvas.genome1Ypositions[i];
			int triggerYMax = canvas.genome1Ypositions[i] + canvas.barHeight;
			
			if (x > triggerXMin && x < triggerXMax && y > triggerYMin && y < triggerYMax)
			{
				canvas.triggeredChromo = i;
				canvas.inTriggerArea = true;
				break;
			}
			else
			{
				canvas.inTriggerArea = false;
				canvas.triggeredChromo = -1;
			}
		}
		
		// only repaint if we have just entered a new kind of
		// area
		// just entered trigger area
		if (canvas.inTriggerArea && !canvas.previousAreaWasTrigger)
		{
			canvas.repaint();
			canvas.previousAreaWasTrigger = true;
			canvas.previousAreaWasNonTrigger = false;
		}
		// just entered non-trigger area
		if (!canvas.inTriggerArea && canvas.previousAreaWasTrigger)
		{
			canvas.repaint();
			canvas.previousAreaWasTrigger = false;
			canvas.previousAreaWasNonTrigger = true;
		}
	}

// --------------------------------------------------------------------------------------------------------------------------------
	
	// don't need these just now
	public void mouseClicked(MouseEvent arg0){}	
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}	
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
	public void mouseDragged(MouseEvent arg0){}
	
//	 --------------------------------------------------------------------------------------------------------------------------------
}
