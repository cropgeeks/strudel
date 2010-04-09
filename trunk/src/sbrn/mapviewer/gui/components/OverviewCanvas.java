package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class OverviewCanvas extends JPanel implements MouseMotionListener, MouseInputListener
{
//	===========================================vars================================

	//the main frame
	WinMain winMain;

	//the main canvas
	MainCanvas mainCanvas;

	//the map set this overview depicts
	public GMapSet gMapSet;

	//this is where we drag the mouse to
	int mouseDragPosY = 0;

	// space between chromosomes, fixed
	int chromoSpacing;

	//the rectangle we draw around the currently zoomed in area
	Rectangle regionRect;

	//the minimum chromosome height at which we will still render individual chromosomes on this overview
	int minChromoHeight = 2;

	//true if we have to render multiple chromosomes as one due to excessive numbers
	public boolean renderAsOneChromo = false;

//	========================================curve'tor=====================================

	public OverviewCanvas(WinMain winMain, GMapSet gMapSet)
	{
		this.winMain = winMain;
		this.mainCanvas = winMain.mainCanvas;
		this.gMapSet = gMapSet;

		regionRect = new Rectangle();

		setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
		setBackground(Colors.overviewCanvasBackgroundColour);

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		calcInitialParams();
	}

	// ========================================methods=============================

	//calculates chromosome height to predetermine whether this overview will have to do reduced
	//rendering, i.e. show single chromosome instead of multiples
	private void calcInitialParams()
	{
		int canvasHeight = getHeight();

		//space between the chromosomes vertically
		float chromoSpacing = Math.round((canvasHeight / gMapSet.numMaps) * 0.20f);

		// the total amount of space we have for drawing on vertically, in pixels
		float availableSpaceVertically = canvasHeight - (chromoSpacing * 2);
		// the combined height of all the vertical spaces between chromosomes
		float allSpacers = chromoSpacing * (gMapSet.numMaps - 1);
		// the height of a chromosome
		int chromoHeight = Math.round((availableSpaceVertically - allSpacers) / gMapSet.numMaps);

		if (chromoHeight < minChromoHeight && chromoSpacing < minChromoHeight)
		{
			renderAsOneChromo = true;
		}
	}

	// paint the genome onto this canvas
	@Override
	public void paintComponent(Graphics g)
	{
		// need to clear the canvas before we draw
		clear(g);

		Graphics2D g2 = (Graphics2D) g;

		// get current size of frame
		int canvasHeight = getHeight();
		int canvasWidth = getWidth();

		// width of chromosomes -- set this to a fixed fraction of the screen width for now
		int chromoWidth = canvasWidth / 10;

		// x position of genome
		int genomeX = (canvasWidth/2) - (chromoWidth/2);

		//space between the chromosomes vertically
		float chromoSpacing = Math.round((canvasHeight / gMapSet.numMaps) * 0.20f);

		// the total amount of space we have for drawing on vertically, in pixels
		float availableSpaceVertically = canvasHeight - (chromoSpacing * 2);
		// the combined height of all the vertical spaces between chromosomes
		float allSpacers = chromoSpacing * (gMapSet.numMaps - 1);
		// the height of a chromosome
		int chromoHeight = Math.round((availableSpaceVertically - allSpacers) / gMapSet.numMaps);

		// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
		// this may be off the visible canvas in a northerly direction
		// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
		int currentY = Math.round(chromoSpacing);

		//background gradient from top to bottom, dark to light, starts black
		Color b1 = Colors.backgroundGradientStartColour;
		Color b2 = Colors.backgroundGradientEndColour;
		g2.setPaint(new GradientPaint(canvasWidth/2, 0, b1, canvasWidth/2, canvasHeight, b2));
		g2.fillRect(0, 0, canvasWidth, canvasHeight);
		int fontSize = 9;

		//if we can fit all the chromosomes on the overview canvas
		if (chromoHeight > minChromoHeight && chromoSpacing > minChromoHeight)
		{
			// now paint the chromosomes in this genome
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// the map draws itself from 0,0 always but we need move the origin of the graphics object to the actual
				// coordinates where we want things drawn
				g2.translate(genomeX, currentY);

				// get the map to draw itself (from 0,0 always)
				gChromoMap.paintOverViewMap(g2, chromoWidth, chromoHeight, fontSize);

				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-genomeX, -currentY);

				// increment the y position so we can draw the next one
				currentY += chromoHeight + chromoSpacing;
			}
		}
		//if we cannot fit all chromos on together
		else
		{
			//draw a single chromo from top to bottom -- this represents all chromos together for this purpose
			Color colour = Colors.genomeColour;
			Color centreColour = colour.brighter().brighter().brighter().brighter();
			GradientPaint gradient = new GradientPaint(0, 0, colour, chromoWidth / 2, 0, centreColour, true);
			g2.setPaint(gradient);
			int chromoXPos = canvasWidth/2 - chromoWidth/2;
			int gap = 7;
			g2.fillRect(chromoXPos, gap, chromoWidth, canvasHeight - gap*2);

			//now draw a warning on the canvas to say that all chromos have to be rendered together here
			g2.setColor(Colors.chromosomeIndexColour);
			String warnStr = "all chromosomes";
			int strWidth = g2.getFontMetrics().stringWidth(warnStr);
			int labelXPos = canvasWidth/2 - strWidth/2;
			g2.drawString(warnStr, labelXPos, canvasHeight/2);
			g2.drawString("(rendered as one)", labelXPos, canvasHeight/2 + fontSize*2);
		}

		// now draw a rectangle indicating where in the main canvas we are currently zoomed in to
		//bounds of the rectangle:
		int rectX = 1;
		int rectY = 0;
		int rectWidth = getWidth()-3;
		int rectHeight = 0;
		//we only ever change the height and y coord of the rectangle
		//the rest stays the same as we always want it to cover the full width of the canvas
		if (gMapSet.zoomFactor == 1) //fully zoomed out
		{
			rectY = 1;
			rectHeight = getHeight()-3;
		}
		else //zoomed in
		{
			//work out the topmost y coord of the genome as visible on the main canvas
			int topY = Math.round(gMapSet.centerPoint - winMain.mainCanvas.getHeight()/2.0f);
			//scale this by the overall height of the genome on the main canvas
			double offsetProportionAtTop = topY/(float)gMapSet.totalY;
			//work out the equivalent point on this overview canvas in pixels
			rectY = (int)Math.round(getHeight()*offsetProportionAtTop);

			//work out the vertical extent of the visible area of the main canvas as a proportion of the total y
			double offsetProportionVisibleArea = winMain.mainCanvas.getHeight()/(float)gMapSet.totalY;
			//set the rect height accordingly
			rectHeight = (int)Math.round(getHeight() * offsetProportionVisibleArea);
		}

		//set this all up
		regionRect.setBounds(rectX, rectY, rectWidth, rectHeight);

		//draw the rectangle
		g2.setColor(Colors.overviewCanvasSelectionRectColour);
		g2.draw(regionRect);

		//fill it with a transparent paint
		g2.setColor(Colors.overviewCanvasTransparentPaint);
		g2.fill(regionRect);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// clears the canvas completely
	protected void clear(Graphics g)
	{
		super.paintComponent(g);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//used to move the genome viewport to a position in the genome equivalent to that the user just dragged
	//the mouse to in the overview canvas
	private void processLineDragRequest(int newY)
	{
		// work out what percentage offset from the top of the topmost chromosome this y position is equal to
		float offsetProportion = (newY - chromoSpacing) / (float)(getHeight()-chromoSpacing);

		//now convert this to an actual Y value which is what we need to pass to the next method
		int newYCoord = Math.round(offsetProportion*gMapSet.totalY);

		//check if we are not trying to move the region rectangle beyond the top or the bottom of the genome
		if(!(newYCoord < 0) && !(newYCoord > gMapSet.totalY))
		{
			// move the genome viewport on the main canvas
			winMain.mainCanvas.moveGenomeViewPort(gMapSet, newYCoord);
		}

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
		//turn antialiasing on and repaint
		//we do this so we can move the selection rectangle on the overview without antialias for better performance, and then
		//we want to redraw when we are done moving it
		winMain.mainCanvas.updateCanvas(true);
	}

	//used to move the genome viewport to a position in the genome equivalent to that the user just dragged
	//the mouse to in the overview canvas
	public void mouseDragged(MouseEvent e)
	{
		//turn off antialias on the main canvas while moving the selection rectangle
		processLineDragRequest(e.getY());
	}

	public void mouseMoved(MouseEvent e)
	{

	}

}
