package sbrn.mapviewer.gui;

import java.awt.*;

import javax.swing.*;

import sbrn.mapviewer.gui.entities.*;

public class OverviewCanvas extends JPanel
{
	WinMain winMain;
	MainCanvas mainCanvas;
	GMapSet gMapSet;
	
	int chromoSpacing = 5;
	
	public OverviewCanvas(WinMain winMain, GMapSet gMapSet)
	{
		this.winMain = winMain;
		this.mainCanvas = winMain.mainCanvas;
		this.gMapSet = gMapSet;
		setBorder(BorderFactory.createLineBorder(new Color(180,180,180), 1));
		setBackground(new Color(240,240,240));
	}
	
	// ========================================methods=============================
	
	// paint the genome onto this canvas
	public void paintComponent(Graphics g)
	{
		// need to clear the canvas before we draw
		clear(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// get current size of frame
		int canvasHeight = getHeight();
		int canvasWidth = getWidth();
		
		// x position of genome
		int x = canvasWidth / 2;
		
		// work out the other coordinates needed		
		// chromoUnit is the combined height of a chromosome and the space below it extending to the next chromosome in the column
		int chromoUnit = canvasHeight / (gMapSet.numMaps + 1); // adding 1 gives us buffer space between chromosomes vertically
				
		// height of chromosomes
		int chromoHeight = chromoUnit - chromoSpacing;
		
		// now need to work out where we start painting
		// this can be a negative value
		// need to multiply the current chromosome height with the number of chromos
		// this plus the spaces between the chromosomes gives us the total number of pixels we draw
		// regardless of whether this is on the canvas or off
		gMapSet.totalY = gMapSet.numMaps * chromoUnit;
		
		// first set the distance from the top of the frame to the top of the first chromo
		int spacer = (canvasHeight - (gMapSet.numMaps * chromoUnit)) / 2;
		
		// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
		// this may be off the visible canvas in a northerly direction
		// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
		int currentY = spacer;
		
		// width of chromosomes -- set this to a fixed fraction of the screen width for now
		int chromoWidth = canvasWidth / 10;
		
		// now paint the chromosomes in this genome
		// for each chromosome in the genome
		for (GChromoMap gChromoMap : gMapSet.gMaps)
		{

			// the map draws itself from 0,0 always but we need move the origin of the graphics object to the actual
			// coordinates where we want things drawn
			g2.translate(x, currentY);
			
			// need to set the current height and width and coords on the chromomap before we draw it
			// this is purely so we have it stored somewhere
			gChromoMap.x = x;
			gChromoMap.y = currentY;
			gChromoMap.height = chromoHeight;
			gChromoMap.width = chromoWidth;
			
			// get the map to draw itself (from 0,0 always)
			gChromoMap.paintMap(g2,true);
			
			// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
			g2.translate(-x, -currentY);
			
			// increment the y position so we can draw the next one
			currentY += chromoUnit;
			
		}
		
		//now draw a line indicating where in the main canvas we are currently zoomed in to
		int centerPoint = gMapSet.centerPoint;
		int lineY = (int)(canvasHeight * ((float)centerPoint/100));
		g2.setColor(Color.red);
		g2.drawLine(0, lineY, canvasWidth, lineY);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// clears the canvas completely
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
}
