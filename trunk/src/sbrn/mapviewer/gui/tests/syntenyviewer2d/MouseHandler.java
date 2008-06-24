package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.event.*;

import javax.swing.event.*;

import sbrn.mapviewer.gui.tests.mainGui.*;

public class MouseHandler implements MouseInputListener
{

	// ========================================vars=============================================

	MapViewerFrame frame;
	
	//variables for recording zoom requests
	int mousePressedX = -1;
	int mousePressedY = -1;
	int mouseReleasedX = -1;
	int mouseReleasedY = -1;

	// ========================================c'tor=============================================

	public MouseHandler(MapViewerFrame frame)
	{
		this.frame = frame;
	}

	// ========================================methods=============================================

	public void mouseClicked(MouseEvent arg0)
	{

		Canvas2D canvas = frame.canvas2D;

		int x = arg0.getX();
		int y = arg0.getY();
		System.out.println("mouse clicked at " + x + "," + y);

		// check if mouse is now in area of one of the target chromosomes
		for (int i = 0; i < canvas.genomes[0].chromosomes.length; i++)
		{
			// work out the trigger area for each chromosome
			// add a little space either side of chromo to make selecting it easier
			int triggerXMin = canvas.genomes[0].xPosition - canvas.genomes[0].chromoWidth * 5;
			int triggerXMax = canvas.genomes[0].xPosition + canvas.genomes[0].chromoWidth * 5;
			int triggerYMin = canvas.genomes[0].chromosomes[i].yPosition;
			int triggerYMax = canvas.genomes[0].chromosomes[i].yPosition + canvas.chromoHeight;

			// we have a hit
			if (x > triggerXMin && x < triggerXMax && y > triggerYMin && y < triggerYMax)
			{
				// a single click means we want to repaint the 2D view with the links from the selected chromosome displayed
				if (arg0.getClickCount() == 1 || arg0.getClickCount() == 2)
				{
					// set the selected chromo index and update the 2d view
					canvas.selectedChromoIndex = i;
					canvas.inTriggerArea = true;
					canvas.repaint();
					System.out.println("canvas.selectedChromoIndex = " + canvas.selectedChromoIndex);
				}

				break;
			}
			else
			// click was outside a trigger area
			{
				frame.setSelectedChromoIndex(-1);
				// repaint the 2D canvas with no links displayed
				canvas.inTriggerArea = false;
				canvas.selectedChromoIndex = -1;
				canvas.repaint();
				//System.out.println("nothing selected");
			}
		}
		
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// don't need these just now
	public void mouseEntered(MouseEvent arg0)
	{
	}

	public void mouseExited(MouseEvent arg0)
	{
	}

	public void mousePressed(MouseEvent arg0)
	{
		mousePressedX = arg0.getX();
		mousePressedY = arg0.getY();
//		System.out.println("mouse pressed at " + mousePressedX + "," + mousePressedY);
	}

	public void mouseReleased(MouseEvent arg0)
	{	
		mouseReleasedX = arg0.getX();
		mouseReleasedY = arg0.getY();
//		System.out.println("mouse released at " + mouseReleasedX + "," + mouseReleasedY);
		
		//take this as a cue to zoom
		frame.canvas2D.zoomTo(mousePressedX,mousePressedY,mouseReleasedX,mouseReleasedY);
	}

	public void mouseDragged(MouseEvent arg0)
	{
	}

	public void mouseMoved(MouseEvent arg0)
	{
	}

	// --------------------------------------------------------------------------------------------------------------------------------
}
