package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.event.*;

import javax.swing.event.*;

import sbrn.mapviewer.gui.tests.mainGui.*;

public class MouseHandler implements MouseInputListener
{

//========================================vars=============================================	
	
		MapViewerFrame frame; 
	
//========================================c'tor=============================================
	
	public MouseHandler(MapViewerFrame frame)
	{
		this.frame = frame;
	}

//========================================methods=============================================	
	
	public void mouseMoved(MouseEvent arg0)
	{

		Canvas2D canvas = frame.getCanvas2D();
		
		int x = arg0.getX();
		int y = arg0.getY();

//		System.out.println("previousAreaWasTrigger = " + canvas.previousAreaWasTrigger);
//		System.out.println("previousAreaWasNonTrigger = " + canvas.previousAreaWasNonTrigger);
//		System.out.println("canvas.inTriggerArea = " + canvas.inTriggerArea);
//		System.out.println("canvas.selectedChromoIndex = " + canvas.selectedChromoIndex);

		// check if mouse is now in area to the right of one of the reference chromosomes
		for (int i = 0; i < canvas.genomes[0].chromosomes.length; i++)
		{
			// work out the trigger area for each chromosome
			int triggerXMin = canvas.genomes[0].xPosition;
			int triggerXMax = canvas.genomes[1].xPosition;
			int triggerYMin = canvas.genomes[0].chromosomes[i].yPosition;
			int triggerYMax = canvas.genomes[0].chromosomes[i].yPosition + canvas.chromoHeight;
			
			if (x > triggerXMin && x < triggerXMax && y > triggerYMin && y < triggerYMax)
			{
				canvas.selectedChromoIndex = i;
				canvas.inTriggerArea = true;
				break;
			}
			else
			{
				canvas.inTriggerArea = false;
				canvas.selectedChromoIndex = -1;
			}
		}
		
		// only repaint if we have just entered a new kind of area
		// just entered trigger area
		if (canvas.inTriggerArea && !canvas.previousAreaWasTrigger)
		{
			//System.out.println("repainting");
			canvas.repaint();
			canvas.previousAreaWasTrigger = true;
			canvas.previousAreaWasNonTrigger = false;
		}
		// just entered non-trigger area
		if (!canvas.inTriggerArea && canvas.previousAreaWasTrigger)
		{
			//System.out.println("repainting");
			canvas.repaint();
			canvas.previousAreaWasTrigger = false;
			canvas.previousAreaWasNonTrigger = true;
		}
	}
//	 --------------------------------------------------------------------------------------------------------------------------------	
	
	public void mouseClicked(MouseEvent arg0)
	{
		if(arg0.getClickCount()!=2)
			return;
		
		Canvas2D canvas = frame.getCanvas2D();
		
		int x = arg0.getX();
		int y = arg0.getY();	
		System.out.println("mouse clicked at " + x + "," + y);
		
		// check if mouse is now in area to the right of one of the reference chromosomes
		for (int i = 0; i < canvas.genomes[0].chromosomes.length; i++)
		{
			// work out the trigger area for each chromosome
			 //add a little space either side of chromo to make selecting it easier
			int triggerXMin = canvas.genomes[0].xPosition -  canvas.chromoWidth;
			int triggerXMax = canvas.genomes[0].xPosition + canvas.chromoWidth*2;
			int triggerYMin = canvas.genomes[0].chromosomes[i].yPosition;
			int triggerYMax = canvas.genomes[0].chromosomes[i].yPosition + canvas.chromoHeight;
			
			//we have a hit
			if (x > triggerXMin && x < triggerXMax && y > triggerYMin && y < triggerYMax)
			{
				//set the selected chromo index and update the 3d view
				frame.setSelectedChromoIndex(i);
				frame.getCanvas3D().setCentralChromoIndex(i);
				frame.getCanvas3D().updateView();
				
				//switch the view to the 3D view
				frame.getTabbedPane().setSelectedIndex(1);
				break;
			}
			else
			{
				frame.setSelectedChromoIndex(-1);
			}
		}		
		System.out.println("frame.selectedChromoIndex = " +  frame.getSelectedChromoIndex());
	}

// --------------------------------------------------------------------------------------------------------------------------------
	
	// don't need these just now
	
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}	
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
	public void mouseDragged(MouseEvent arg0){}
	
//	 --------------------------------------------------------------------------------------------------------------------------------
}
