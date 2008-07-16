package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import sbrn.mapviewer.gui.entities.*;

public class OverviewCanvas extends JPanel implements MouseMotionListener
{
	WinMain winMain;
	MainCanvas mainCanvas;
	GMapSet gMapSet;
	int lineY = 0;
	int mouseDragPosY = 0;
	int totalY = 0;
	// the space we want at the top and bottom
	int topBottomSpacer = 5;
	
	public OverviewCanvas(WinMain winMain, GMapSet gMapSet)
	{
		this.winMain = winMain;
		this.mainCanvas = winMain.mainCanvas;
		this.gMapSet = gMapSet;
		setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
//		setBackground(new Color(240, 240, 240));
		setBackground(Color.BLACK);
		this.addMouseMotionListener(this);
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
		
		// space between chromosomes, fixed
		int chromoSpacing = 4;
		
		// height of chromosomes
		int chromoUnit = (canvasHeight - (topBottomSpacer * 2)) / gMapSet.numMaps;
		
		// work out the other coordinates needed
		// chromoUnit is the combined height of a chromosome and the space
		// below it extending to the next chromosome in the column
		int chromoHeight = chromoUnit - chromoSpacing;
		
		// now need to work out where we start painting
		// this can be a negative value
		// need to multiply the current chromosome height with the number of chromos
		// this plus the spaces between the chromosomes gives us the total number of pixels we draw
		// regardless of whether this is on the canvas or off
		totalY = gMapSet.numMaps * chromoUnit;
		
		// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
		// this may be off the visible canvas in a northerly direction
		// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
		int currentY = topBottomSpacer;
		
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
			gChromoMap.paintOverViewMap(g);
			
			// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
			g2.translate(-x, -currentY);
			
			// increment the y position so we can draw the next one
			currentY += chromoUnit;
			
		}
		
		// now draw a line indicating where in the main canvas we are currently zoomed in to
		int centerPoint = gMapSet.centerPoint;

		if (gMapSet.zoomFactor == 1)
			lineY = canvasHeight / 2;
		else
			lineY = (int) (totalY * (centerPoint / 100.0f));

		g2.setColor(Color.red);
		g2.drawLine(0, lineY, canvasWidth, lineY);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// clears the canvas completely
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	private void processLineDragRequest(int newY)
	{
		//work out what percentage offset from the top of the topmost chromosome this y position is equal to
		int percentOffset = (int) (((newY - topBottomSpacer)/(float)totalY)*100);

		//move the genome viewport on the main canvas
		winMain.mainCanvas.moveGenomeViewPort(gMapSet, percentOffset);	
		
		repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		
	}

	public void mouseDragged(MouseEvent e)
	{
		processLineDragRequest(e.getY());
		
	}

	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
}
