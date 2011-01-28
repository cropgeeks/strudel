package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
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

	// if true, paint a rectangle to indicate the fact that we are panning over a region we want to select for zooming in to
	public boolean drawZoomSelectionRectangle = false;
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
	// Back-buffer for AA-rendering
	private BufferedImage aaBuffer;
	// Does the buffer need redrawn before use?
	private boolean redraw = true;

	//true if we want to display individual features the user has searched for with the find dialog
//	public boolean drawHighlightFeatures = false;

	//true if we want to display features within a certain range the user has searched for with the range dialog
	public boolean drawFoundFeaturesInRange = false;

	//true if we want to display features within a certain range the user has searched for with the find dialog
	public boolean drawFeaturesFoundByName = false;
	
	//true if we want links drawn from all features within a certain range on the chromosome
	public boolean drawLinksOriginatingInRange = false;

	// a minimum space (in pixels) we want to leave at the top and the bottom of the tallest genome
	public int topBottomSpacer = 0;

	// the total amount of space we have for drawing on vertically, in pixels
	int availableSpaceVertically = 0;
	// the combined height of all the vertical spaces between chromosomes
	int allSpacers = 0;

	// width of chromosomes -- set this to a fixed fraction of the screen width for now
	//gets set in the paintCanvas method
	public int chromoWidth = 0;
	//this is the minimum chromosome width we always want
	public int minimumChromosomeWidth = 10;

	// Objects for multicore rendering
	public static int cores = Runtime.getRuntime().availableProcessors();
	public static ExecutorService executor;
	public static Future<?>[] tasks;

	public int numMarkersDrawn = 0;

	Rectangle canvasBounds = null;

	// ============================curve'tor==================================

	public MainCanvas()
	{
		this.winMain = Strudel.winMain;
		zoomHandler = new CanvasZoomHandler(this);

		//this is for detecting key events
		addKeyListener(new CanvasKeyListener());
		setFocusable(true);

		// Prepare the background threads that will do the main painting
		executor = Executors.newFixedThreadPool(cores);
		tasks = new Future[cores];
	}

	// ============================methods==================================

	public void updateCanvas(boolean invalidate)
	{
		setRedraw(invalidate);
		repaint();
	}

	// ---------------------------------------------------------------------------------------------------------------------------------


	@Override
	public void paintComponent(Graphics graphics)
	{
//		System.out.println("\n=====main canvas paintComponent " + System.currentTimeMillis());

		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		int w = getWidth();
		int h = getHeight();

		// Does the back-buffer need to be re-created before use
		if (isRedraw() || buffer == null || buffer.getWidth() != w || buffer.getHeight() != h)
		{
			if (buffer == null || buffer.getWidth() != w || buffer.getHeight() != h)
			{
//				buffer = (BufferedImage) createImage(w, h);
//				aaBuffer = (BufferedImage) createImage(w, h);
				buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				aaBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			}

			// Render an image to the buffer
			Graphics2D bufferGraphics = buffer.createGraphics();
			paintCanvas(bufferGraphics, false);
			bufferGraphics.dispose();

			// Set up a post-render AA thread for a pretty repaint when ready
			if(Prefs.userPrefAntialias)
				new AntiAliasRepaintThread(aaBuffer);
		}

		// Render the back-buffer
		if (AntiAliasRepaintThread.hasImage && Prefs.userPrefAntialias)
			g.drawImage(aaBuffer, 0, 0, null);
		else
			g.drawImage(buffer, 0, 0, null);

		// Render any additional overlay images (highlights, mouse-overs etc)

		// this optionally draws a rectangle delimiting a region we want to zoom in on
		if (drawZoomSelectionRectangle)
		{
			//fill the rectangle first
			g.setPaint(Colors.panZoomRectFillColour);
			g.fill(selectionRect);
			//now draw the rectangle's outline
			g.setColor(Colors.panZoomRectOutlineColour);
			g.draw(selectionRect);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//this draws the distance markers and mouseover labels on all genomes
		drawMouseOverLabels(g);

		//update the hint panel
		HintPanel.upDate();
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------

	//draws the and mouseover labels on all genomes
	private void drawMouseOverLabels(Graphics2D g2)
	{
		//now we need to draw the rest of the things relating to the map
		//this needs to be done after drawing the links so it is all visible on top of the links
		for (GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				if(gChromoMap.isShowingOnCanvas)
				{
					gChromoMap.drawMouseOverFeatures(g2);
				}
			}
		}
	}


	//------------------------------------------------------------------------------------------------------------------------------------------------

	//draws the distance markers on all genomes
	private void drawDistanceMarkers(Graphics2D g2)
	{
		//now we need to draw the rest of the things relating to the map
		//this needs to be done after drawing the links so it is all visible on top of the links
		for (GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				if(gChromoMap.isShowingOnCanvas)
				{
					gChromoMap.drawDistanceMarkers(g2);
				}
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------

	// paint the genomes or portions thereof onto this canvas
	public void paintCanvas(Graphics2D g2, Boolean killMe)
	{
		//work out general position and colour parameters
		calcRequiredParams(g2);

		//for all mapsets
		for (GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			if (killMe) return;

			checkMarkerPaintingThresholds(gMapSet);

			//calculate the x position for this genome
			int numGenomes = winMain.dataSet.gMapSets.size();
			int genomeInterval = getWidth()/numGenomes;
			int spacerLeft = genomeInterval/2;
			gMapSet.xPosition = (genomeInterval * winMain.dataSet.gMapSets.indexOf(gMapSet)) + spacerLeft - chromoWidth/2;

			// work out the other coordinates needed
			// these are genome specific because we can have a different zoom factor for each genome
			// currentY is the y position at which we start drawing the genome, chromo by chromo, top to bottom
			// this may be off the visible canvas in a northerly direction
			int currentY = calcMapSetSpecificParams(gMapSet);

			// now paint the chromosomes in this genome
			// for each chromosome in the genome
			drawMaps(gMapSet,g2, currentY);

		}

		boolean dimNormalLinks = Strudel.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().isSelected()
			&& (drawFoundFeaturesInRange || drawFeaturesFoundByName);

		// optionally draw all the currently selected links between chromos
		if (drawLinks && !killMe)
		{
			linkDisplayManager.drawAllLinks(g2, killMe, dimNormalLinks);
		}

		//this draws homologies for features in a contiguous range on a chromosome
		if ((drawFoundFeaturesInRange || drawFeaturesFoundByName) && !killMe)
		{
			if (Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayHomologsCheckBox().isSelected() || Strudel.winMain.foundFeaturesTableControlPanel.getShowHomologsCheckbox().isSelected())
			{
				linkDisplayManager.drawHighlightedLinksInRange(g2, winMain.fatController.selectedMap);
			}
		}
		
		//this dynamically draws any links in a range that we are highlighting with a mouse action
		if(drawLinksOriginatingInRange)
			linkDisplayManager.drawLinksForFeatureSet(linkDisplayManager.featuresSelectedByRange, g2, false, winMain.fatController.selectedMap);

		//distance markers along the maps
		if (!killMe && Prefs.showDistanceMarkers)
			drawDistanceMarkers(g2);

		if (!killMe)
			drawAllMapColours(g2);

		//this draws labels of features in a contiguous range on a chromosome
		//need to do this in this order so things are drawn on top of each other in the right sequence
		if (!killMe && (drawFoundFeaturesInRange || drawFeaturesFoundByName) && (Strudel.winMain.ffInRangeDialog.ffInRangePanel.getDisplayLabelsCheckbox().isSelected() || Strudel.winMain.foundFeaturesTableControlPanel.getShowLabelsCheckbox().isSelected()))
		{
			LabelDisplayManager.drawLabelsForFoundFeatures(g2, winMain.fatController.selectedMap);
		}			
		
		//we also want to check whether there are any links to display that are to be highlighted after a name based search for
		//features and links originating from them
		if (!killMe && winMain.fatController.highlightedTableEntries != null && Strudel.winMain.dataSet.gMapSets.size() > 1)
		{
			linkDisplayManager.drawHighlightedLinksForTableEntries(winMain.fatController.highlightedTableEntries, g2, winMain.fatController.selectedMap);
			LabelDisplayManager.drawLabelsForTableEntries(winMain.fatController.highlightedTableEntries,g2, true, winMain.fatController.selectedMap);
		}	

		//last we want to draw the chromosome indexes so they are painted on top of all other stuff
		if (!killMe)
			drawAllMapIndices(g2);

		// also need to update the overview canvases from here
		winMain.fatController.updateOverviewCanvases();

		setRedraw(false);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//this method sets all the mapset specific parameters we need to work out for each drawing operation
	private int calcMapSetSpecificParams(GMapSet gMapSet)
	{
		int currentY = 0;
		// this is what we do at a zoom factor of 1 (at startup but also after zoom reset)
		if (gMapSet.zoomFactor == 1)
		{
			// the height of a chromosome
			gMapSet.chromoHeight = (availableSpaceVertically - allSpacers) / winMain.dataSet.maxChromos;
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

		return currentY;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//this method sets all the general parameters we need to work out for each drawing operation such as chromosome widths etc
	private void calcRequiredParams(Graphics2D g2)
	{	
		numMarkersDrawn = 0;

		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();

		// create a bounding rectangle the size of the currently visible canvas
		canvasBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());

		//background gradient from top to bottom, dark to light, starts black
		Color b1 = Colors.backgroundGradientStartColour;
		Color b2 = Colors.backgroundGradientEndColour;
		g2.setPaint(new GradientPaint(canvasWidth / 2, 0, b1, canvasWidth / 2, canvasHeight, b2));
		g2.fillRect(0, 0, canvasWidth, canvasHeight);

		setChromosomeWidth();

		// the total amount of space we have for drawing on vertically, in pixels
		availableSpaceVertically = canvasHeight - (chromoSpacing * 2);
		// the combined height of all the vertical spaces between chromosomes
		allSpacers = chromoSpacing * (winMain.dataSet.maxChromos - 1);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Sets the width of all chromosomes shown on the canvas.
	 *
	 * Algorithm for this:
	 *
	 * Calculate the ideal chromosome width (this is a dynamically calculated value that changes with the canvas width itself)
	 *  if we can't have at least 4 times the chromo width between genomes, reduce the chromo width rather than the space between genomes
	 *  if this brings the chromo width to below our safe threshold, set it to the safe threshold
	 *
	 *  The effect of this is that if we are short of space then we reduce the chromosome width before we start reducing the space between genomes.
	 */
	private void setChromosomeWidth()
	{
		// ideal width of chromosomes -- set to a fixed fraction of the screen width
		int maxChromoWidth = Math.round(canvasWidth / 40);
		chromoWidth = maxChromoWidth;

		//ideally we want something like at least 4 times the chromowidth between genomes or else the links get awfully cluttered
		//if we have so many genomes that this is impossible then we have to reduce this or else go for the minimum width
		int numGenomes = winMain.dataSet.gMapSets.size();
		//the multiplier we use for this
		int multiplier = 4;
		int combinedWidthAllGenomes = numGenomes*chromoWidth;
		int unfilledSpaceHorizontally = getWidth() - combinedWidthAllGenomes;
		float actualMultipleOfChromoWidth = unfilledSpaceHorizontally/(float)combinedWidthAllGenomes;
		//reduce the width appropriately if necessary
		if(actualMultipleOfChromoWidth < multiplier)
			chromoWidth = (unfilledSpaceHorizontally/multiplier) / numGenomes;

		//but don't let this fall below the minimum
		if(chromoWidth < minimumChromosomeWidth)
			chromoWidth = minimumChromosomeWidth;

		// check that this number is even
		boolean evenNumber = chromoWidth % 2 == 0;
		// if it isn't, just add 1 -- otherwise we get into problems with feature line widths exceeding the width of the chromosome
		if (!evenNumber)
			chromoWidth += 1;
	}


	// -----------------------------------------------------------------------------------------------------------------------------------

	//draws all the maps in one mapset
	private void drawMaps(GMapSet gMapSet,Graphics2D g2, int currentY)
	{
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
			gChromoMap.height = gMapSet.chromoHeight;
			gChromoMap.width = chromoWidth;
			// update its bounding rectangle (used for hit detection)
			gChromoMap.boundingRectangle.setBounds(gChromoMap.x, gChromoMap.y,
							gChromoMap.width, gChromoMap.height);

			if (canvasBounds.contains(gChromoMap.boundingRectangle) || canvasBounds.intersects(gChromoMap.boundingRectangle))
			{
				gChromoMap.isShowingOnCanvas = true;
				gChromoMap.isFullyShowingOnCanvas = canvasBounds.contains(gChromoMap.boundingRectangle);
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

	// -----------------------------------------------------------------------------------------------------------------------------------

	//draws an index number next to each map in each genome
	private void drawAllMapIndices(Graphics2D g2)
	{
		//last we want to draw the chromosome indexes so they are painted on top of all other stuff
		for (GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				//if the map is meant to be visible on the canvas at this time
				if (gChromoMap.isShowingOnCanvas && !gChromoMap.inversionInProgress)
				{
					if(gChromoMap.alwaysShowAllLabels)
						LabelDisplayManager.drawLabelsForAllVisibleFeatures(g2, gChromoMap);
					drawMapIndex(g2, gChromoMap);
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	private void drawAllMapColours(Graphics2D g2)
	{
		// Font stuff copied from drawMapIndex() below to try to keep the colour labels a similar size
		int fontSize = Math.round(WinMain.mainCanvas.getHeight() / 40);
		Font mapLabelFont = new Font("Arial", Font.BOLD, fontSize);
		g2.setFont(mapLabelFont);
		int fontHeight = g2.getFontMetrics().getHeight()-4;

		for (GMapSet gMapSet : winMain.dataSet.gMapSets)
		{
			// for each chromosome in the genome
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				//if the map is meant to be visible on the canvas at this time
				if (gChromoMap.isShowingOnCanvas && !gChromoMap.inversionInProgress)
				{
					// decide where to place the label on y
					int labelY = 0;
					//position of index with this var is in the center of the chromosome regardless of chromo position
					int chromoCenterPos = gChromoMap.y + Math.round(gChromoMap.height / 2.0f) + (fontSize/2);

					//draw the index in the center of each chromosome
					labelY = chromoCenterPos;

					// Does it have a custom colour? If so, mark it next to the label
					if (gChromoMap.chromoMap.r != -1)
					{
						Color c = Utils.getChromosomeColor(gChromoMap.chromoMap);

						g2.setColor(c);
						g2.fillRect(gChromoMap.x -25, labelY-fontHeight, 15, fontHeight);
						g2.setColor(Color.white);
						g2.drawRect(gChromoMap.x -25, labelY-fontHeight, 15, fontHeight);
					}
				}
			}
		}
	}

	// draw the index of the map in the genome
	private void drawMapIndex(Graphics2D g2, GChromoMap gChromoMap)
	{
		//font stuff - also see drawAllMapColours() above if changing this code!
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
		String indexLabel = gChromoMap.name;
		g2.drawString(indexLabel, gChromoMap.x + gChromoMap.width + 10, labelY);

		//turn text antialiasing off again
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}


	// -----------------------------------------------------------------------------------------------------------------------------------

	// used to scroll up and down the canvas
	public void moveGenomeViewPort(GMapSet gMapSet, int newCenterPoint)
	{
		//the center point is an absolute value in pixels which is the offset from the top of the genome to the current
		//point in the center of the screen on y
		//update the centerpoint to the new value
		gMapSet.centerPoint = newCenterPoint;
		updateCanvas(true);

		//update overviews
		winMain.fatController.updateOverviewCanvases();

		//now update the arrays with the position data
		Strudel.winMain.fatController.initialisePositionArrays();

		//repaint
		updateCanvas(true);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	//checks whether we want to draw markers on the chromsomes
	//this should only happen if we have exceeded a threshold zoom factor for the owning mapset or if the user has explicitly
	//requested it by pressing a button
	public void checkMarkerPaintingThresholds(GMapSet selectedSet)
	{
		// check whether we need to display markers and labels
		if (selectedSet.zoomFactor >= selectedSet.thresholdAllMarkerPainting)
		{
			selectedSet.showAllFeatures = true;
		}
		else if (selectedSet.zoomFactor < selectedSet.thresholdAllMarkerPainting)
		{
			if(selectedSet.overrideMarkersAutoDisplay)
				selectedSet.showAllFeatures = true;
			else
				selectedSet.showAllFeatures = false;
		}
		else
		{
			selectedSet.showAllFeatures = false;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	public BufferedImage getImageBuffer()
	{
		return buffer;
	}

	/**
	 * @return the redraw
	 */
	public boolean isRedraw()
	{
		return redraw;
	}

	/**
	 * @param redraw the redraw to set
	 */
	public void setRedraw(boolean redraw)
	{
		this.redraw = redraw;
	}

	//----------------------------------------------------------------------------------------------------------------------------------------


}// end class