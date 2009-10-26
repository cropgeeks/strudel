package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import sbrn.mapviewer.gui.handlers.*;

public class MainCanvas extends JPanel
{
	
	// ============================vars==================================
	
	// the parent component
	WinMain winMain;
	
	// size of the canvas panel
	public int canvasHeight;
	public int canvasWidth;
	
	// space the chromosomes vertically by this fixed amount
	public int chromoSpacing = 15;
	
	// do we need to draw links?
	public boolean drawLinks = false;
	
	// if true, antialias everything
	//this is a flag set by code within the application 
	public boolean antiAlias = false;
	//in addition we have a flag set by the user (through a button) -- userPrefAntialias
	//stored in the Prefs 
	//this overrides the local flag if it is set to false
	//otherwise we ignore it
	
	// if true, paint a rectangle to indicate the fact that we are panning over a region we want to select for zooming in to
	public boolean drawSelectionRect = false;
	//these are the relevant coordinates for this
	public Rectangle selectionRect = new Rectangle();
	
	//the handler for all zooming related events
	public CanvasZoomHandler zoomHandler;
	
	//the chromosome height in pixels that we first init the chromos to
	public int initialChromoHeight = 0;
	
	//the canvas height at a zoom factor of 1 
	public int initialCanvasHeight = 0;
	
	//this object handles the display of homology links
	public LinkDisplayManager linkDisplayManager;
	
	// Back-buffer for rendering
	private BufferedImage buffer;
	// Does the buffer need redrawn before use?
	boolean redraw = true;
	
	//true if we want to display individual features the user has searched for with the find dialog
	public boolean drawHighlightFeatures = false;
	
	//true if we want to display features within a certain range the user has searched for with the find dialog
	public boolean drawFoundFeaturesInRange = false;
	
	public int topBottomSpacer = 0;
	
	// width of chromosomes -- set this to a fixed fraction of the screen width for now
	//gets set in the paintCanvas method
	public int chromoWidth = 0;
	
	// ============================curve'tor==================================
	
	public MainCanvas()
	{
		this.winMain = MapViewer.winMain;
		zoomHandler = new CanvasZoomHandler(this);
		
		//this is for detecting key events
		addKeyListener(new CanvasKeyListener(this));
		setFocusable(true);
	}
	
	// ============================methods==================================
	
	public void updateCanvas(boolean invalidate)
	{
		redraw = invalidate;
		repaint();
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------
	
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		
		Graphics2D g = (Graphics2D) graphics;
		
		// Does the back-buffer need to be re-created before use
		if (redraw || buffer == null)
		{
			// Do we need to create a new buffer (if the screen size has changed)
			if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight())
			{
				MapViewer.logger.fine("Creating buffer of " + ((getWidth()*getHeight()*3)/1024) + " kB");
				buffer = (BufferedImage) createImage(getWidth(), getHeight());
			}
			
			// Render an image to the buffer
			Graphics2D bufferGraphics = buffer.createGraphics();
			paintCanvas(bufferGraphics);
			bufferGraphics.dispose();
		}
		
		// Render the back-buffer
		g.drawImage(buffer, 0, 0, null);
		
		// Render any additional overlay images (highlights, mouse-overs etc)
		
		// this optionally draws a rectangle delimiting a region we want to zoom in on
		if (drawSelectionRect)
		{
			//fill the rectangle first
			g.setPaint(Colors.panZoomRectFillColour);
			g.fill(selectionRect);		
			//now draw the rectangle's outline
			g.setColor(Colors.panZoomRectOutlineColour);
			g.draw(selectionRect);
		}
		
		
		//the next lot of features can all be drawn with antialiasing on without too much effect on performance
		//looks much better, especially on the distance markers
		
		//now we need to draw the rest of the things relating to the map
		//this needs to be done after drawing the links so it is all visible on top of the links
		for (GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				if(gChromoMap.isShowingOnCanvas)
				{
					if(Prefs.showDistanceMarkers)
					{
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						gChromoMap.drawDistanceMarkers(g);
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					}
					gChromoMap.drawMouseOverFeatures(g);
					gChromoMap.drawHighlightOutline(g);
					
				}
			}
		}
		
		//this just draws a label for a single highlighted features -- need to be drawn at the very end to be on top of everything else
		if (drawHighlightFeatures)
		{
			LabelDisplayManager.drawHighlightedFeatureLabel(g, winMain.fatController.highlightFeature);
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------
	
	// paint the genomes or portions thereof onto this canvas
	private void paintCanvas(Graphics2D g2)
	{
		// check whether we need antialiasing on
		checkAntiAliasing(g2);
		
		//background
		setBackground(Colors.mainCanvasBackgroundColour);
		
		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		// create a bounding rectangle the size of the currently visible canvas
		Rectangle canvasBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
		
		//background gradient from top to bottom, dark to light, starts black
		Color b1 = Colors.backgroundGradientStartColour;
		Color b2 = Colors.backgroundGradientEndColour;
		g2.setPaint(new GradientPaint(canvasWidth / 2, 0, b1, canvasWidth / 2, canvasHeight, b2));
		g2.fillRect(0, 0, canvasWidth, canvasHeight);
		
		// width of chromosomes -- set this to a fixed fraction of the screen width for now
		chromoWidth = Math.round(canvasWidth / 40);
		
		//for all maps sets
		for (GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			checkMarkerPaintingThresholds(gMapSet);
			checkForLabelDrawing(gMapSet);
			
			//calculate the x position for this genome
			int numGenomes = winMain.dataContainer.gMapSets.size();
			int genomeInterval = getWidth()/numGenomes;
			int spacerLeft = genomeInterval/2;
			gMapSet.xPosition = (genomeInterval * winMain.dataContainer.gMapSets.indexOf(gMapSet)) + spacerLeft - chromoWidth/2;				
			
			// work out the other coordinates needed
			// these are genome specific because we can have a different zoom factor for each genome
			// the total amount of space we have for drawing on vertically, in pixels
			int availableSpaceVertically = canvasHeight - (chromoSpacing * 2);
			// the combined height of all the vertical spaces between chromosomes
			int allSpacers = chromoSpacing * (winMain.dataContainer.maxChromos - 1);
			
			// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
			// this may be off the visible canvas in a northerly direction
			int currentY = 0;
			
			// this is what we do at a zoom factor of 1 (at startup but also after zoom reset)
			if (gMapSet.zoomFactor == 1)
			{
				// the height of a chromosome
				gMapSet.chromoHeight = (availableSpaceVertically - allSpacers) / winMain.dataContainer.maxChromos;
				initialChromoHeight = gMapSet.chromoHeight;
				initialCanvasHeight = canvasHeight;
				
				//the zoom factor at which we would fit a single chromosome (but nothing else) on the visible portion of the canvas
				gMapSet.singleChromoViewZoomFactor = canvasHeight / gMapSet.chromoHeight;
				gMapSet.thresholdAllMarkerPainting = gMapSet.singleChromoViewZoomFactor;
				
				// the total vertical extent of the genome, excluding top and bottom spacers
				gMapSet.totalY = (gMapSet.numMaps * gMapSet.chromoHeight) + ((gMapSet.numMaps - 1) * chromoSpacing);
				gMapSet.centerPoint = Math.round(gMapSet.totalY / 2.0f);
				// the space at the top and bottom -- should be equal
				topBottomSpacer = (canvasHeight - gMapSet.totalY) / 2;
				
				// we want to fit all the chromosomes on at a zoom factor of 1 so we only use the top spacer when this is the case
				currentY = topBottomSpacer;
			}
			// this is what we do when we are zoomed in
			else
			{
				// start drawing at minus half the total height of the entire genome plus half the canvasheight
				currentY = -(gMapSet.totalY / 2) + canvasHeight / 2 - (gMapSet.centerPoint - (gMapSet.totalY / 2));
			}
			
			
			// check that this number is even
			boolean evenNumber = chromoWidth % 2 == 0;
			// if it isn't just add 1 -- otherwise we get into trouble with feature line widths exceeding the width of the chromosome
			if (!evenNumber)
				chromoWidth += 1;
			
			// now paint the chromosomes in this genome
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// we use the same x position for all chromosomes in this genome
				int x = Math.round(gMapSet.xPosition);
				
				// the map draws itself from 0,0 always but we need move the origin of the graphics object to the actual
				// coordinates where we want things drawn
				g2.translate(x, currentY);
				
				// need to set the current height and width and coords on the chromomap before we draw it
				// this is purely so we have it stored somewhere
				gChromoMap.x = x;
				gChromoMap.y = currentY;
				gChromoMap.yOnCanvas = currentY;
				gChromoMap.height = gMapSet.chromoHeight;
				gChromoMap.width = chromoWidth;
				// update its bounding rectangle (used for hit detection)
				gChromoMap.boundingRectangle.setBounds(gChromoMap.x, gChromoMap.y,
								gChromoMap.width, gChromoMap.height);
				
				MapViewer.logger.finest("gChromoMap.arraysInitialized = " + gChromoMap.arraysInitialized);
				
				if (canvasBounds.contains(gChromoMap.boundingRectangle) || canvasBounds.intersects(gChromoMap.boundingRectangle))
				{
					gChromoMap.isShowingOnCanvas = true;
					if (canvasBounds.contains(gChromoMap.boundingRectangle))
					{
						gChromoMap.isFullyShowingOnCanvas = true;
					}
					else
					{
						gChromoMap.isFullyShowingOnCanvas = false;
					}
				}
				else
				{
					gChromoMap.isShowingOnCanvas = false;
				}

				//check whether the arrays that hold the data for drawing features etc have been inited
				//if not, do it now (only needs to be done here once, at startup)
				if (!gChromoMap.arraysInitialized)
					gChromoMap.initArrays();				
				
				//if the map is meant to be visible on the canvas at this time
				if (gChromoMap.isShowingOnCanvas)
				{
					// get the map to draw itself (from 0,0 always)
					gChromoMap.paintMap(g2);
				}
				
				// now move the graphics object's origin back to 0,0 to preserve the overall coordinate system
				g2.translate(-x, -currentY);
				
				// increment the y position so we can draw the next one
				currentY += gMapSet.chromoHeight + chromoSpacing;
			}
		}
		// optionally draw all the currently selected links between chromos
		if (drawLinks)
		{
			linkDisplayManager.drawAllLinks(g2);
		}
		//this draws homologies for features in a contiguous range on a chromosome
		if (drawFoundFeaturesInRange)
		{
			if (MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().isSelected() || MapViewer.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().isSelected())
			{
				linkDisplayManager.drawHighlightedLinksInRange(g2);
			}
		}
		
		//we also want to check whether there are any links to display that are to be highlighted after a name based search for
		//features and links originating from them
		if (drawHighlightFeatures)
		{
			linkDisplayManager.drawHighlightedLink(g2, winMain.fatController.highlightFeature, winMain.fatController.highlightFeatureHomolog, true);
		}
		
		//this draws labels of features in a contiguous range on a chromosome
		//need to do this in this order so things are drawn on top of each other in the right sequence
		if (drawFoundFeaturesInRange && (MapViewer.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().isSelected() || MapViewer.winMain.foundFeaturesTableControlPanel.getShowLabelsCheckbox().isSelected()))
		{
			LabelDisplayManager.drawLabelsForFoundFeatures(g2);
		}

		//last we want to draw the chromosome indexes so they are painted on top of all other stuff
		for (GMapSet gMapSet : winMain.dataContainer.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				//if the map is meant to be visible on the canvas at this time
				if (gChromoMap.isShowingOnCanvas && !gChromoMap.inversionInProgress)
				{		
					if(gMapSet.alwaysShowAllLabels)
						LabelDisplayManager.drawLabelsForAllVisibleFeatures(g2, gMapSet);
					drawMapIndex(g2, gChromoMap, gMapSet);
				}
			}
		}
		// also need to update the overview canvases from here
		winMain.fatController.updateOverviewCanvases();
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// draw the index of the map in the genome
	private void drawMapIndex(Graphics2D g2, GChromoMap gChromoMap, GMapSet gMapSet)
	{
		//the index we want to draw
		int mapIndex = gChromoMap.index;
		
		//font stuff
		int fontSize = Math.round(WinMain.mainCanvas.getHeight() / 40);
		Font mapLabelFont = new Font("Arial", Font.BOLD, fontSize);
		g2.setFont(mapLabelFont);
		g2.setColor(Colors.chromosomeIndexColour);
		
		// decide where to place the label on y
		int labelY = 0;	
		//position of index with this var is in the center of the chromosome regardless of chromo position
		int chromoCenterPos = gChromoMap.y + Math.round(gChromoMap.height / 2.0f) + (fontSize/2);
		
		//draw the index in the center of each chromosome
		labelY = chromoCenterPos;
		
		//turn text antialiasing on
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//draw the label
		FontMetrics fm = g2.getFontMetrics();
		//		String indexLabel = String.valueOf(mapIndex + 1);
		String indexLabel = gChromoMap.name;
		int stringWidth = fm.stringWidth(indexLabel);
		g2.drawString(indexLabel, gChromoMap.x + gChromoMap.width + 10, labelY);
		
		//turn text antialiasing off again
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	// used to scroll up and down the canvas
	public void moveGenomeViewPort(GMapSet gMapSet, int newCenterPoint)
	{
		MapViewer.logger.fine("moveGenomeViewPort for " + gMapSet.name + " to centerpoint " + newCenterPoint);
		
		//the center point is an absolute value in pixels which is the offset from the top of the genome to the current
		//point in the center of the screen on y		
		//update the centerpoint to the new value
		gMapSet.centerPoint = newCenterPoint;
		updateCanvas(true);
		
		//update overviews
		winMain.fatController.updateOverviewCanvases();
		
		//now update the arrays with the position data
		MapViewer.winMain.fatController.initialisePositionArrays();
		
		//		//update the canvas
		MapViewer.winMain.mainCanvas.updateCanvas(true);	
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void checkMarkerPaintingThresholds(GMapSet selectedSet)
	{
		// check whether we need to display markers and labels
		if (selectedSet.zoomFactor >= selectedSet.thresholdAllMarkerPainting)
		{
			selectedSet.paintAllMarkers = true;
		}
		else if (selectedSet.zoomFactor < selectedSet.thresholdAllMarkerPainting)
		{
			if(selectedSet.overrideMarkersAutoDisplay)
				selectedSet.paintAllMarkers = true;
			else
				selectedSet.paintAllMarkers = false;
		}
		else
		{
			selectedSet.paintAllMarkers = false;
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public void checkForLabelDrawing(GMapSet gMapSet)
	{
		//check whether we should have the alwaysShowAllLabelsButton enabled
		//we don't want this option if we are looking at more than one chromo on screen
		if ((gMapSet.zoomFactor >= gMapSet.singleChromoViewZoomFactor - 1 && gMapSet.singleChromoViewZoomFactor != 0))
		{
			gMapSet.zoomControlPanel.alwaysShowAllLabelsButton.setEnabled(true);
		}
		else
		{
			gMapSet.alwaysShowAllLabels = false;
			gMapSet.zoomControlPanel.alwaysShowAllLabelsButton.setSelected(false);
			gMapSet.zoomControlPanel.alwaysShowAllLabelsButton.setEnabled(false);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------
	
	public BufferedImage getImageBuffer()
	{
		return buffer;
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------
	
	private void checkAntiAliasing(Graphics2D g)
	{
		// first check whether we need antialiasing on
		if (antiAlias)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		else
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		
		//this additional flag set by the user can override what we just set here
		//if it is set to true we ignore it though and just do what we were doing above anyway
		if(!Prefs.userPrefAntialias)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}
	
}// end class
