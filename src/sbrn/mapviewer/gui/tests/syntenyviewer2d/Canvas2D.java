package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JPanel;

import sbrn.mapviewer.data.ChromoMap;
import sbrn.mapviewer.data.Link;
import sbrn.mapviewer.data.LinkSet;
import sbrn.mapviewer.data.MapSet;
import sbrn.mapviewer.gui.tests.mainGui.MapViewerFrame;

public class Canvas2D extends JPanel
{
	
	// size of the frame
	int canvasHeight;
	int canvasWidth;
	
	// size of the chromos
	int chromoHeight;
	
	// the maximum nuber of chromos in any one of the genomes involved
	int maxChromos;
	
	// space between chromosomes vertically
	int chromoSpacing;
	
	// colors for line
	Color[] colours = null;
	
	// indicates whether the mouse is in an area that should trigger lines to be drawn
	boolean inTriggerArea = false;
	
	// number of chromosome in the target genome that was triggered to have its relationships drawn
	int selectedChromoIndex = -1;
	
	// the height of a chromosome and a vertical spacer interval combined
	int chromoUnit;
	
	// genome objects
	Genome[] genomes;
	
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	LinkSet links = null;
	
	// the frame we are displaying the canvas in
	MapViewerFrame frame;
	
	// a hashtable that contains chromomaps from the target genome as keys and LinkedList objects as values which in
	// turn hold a list of LinkSet objects each, where each Linkset represents the links between the chromomap and a chromomap in the reference
	// genome
	Hashtable<ChromoMap, LinkedList> linkSetLookup;
	
	// the total vertical distance in pixels on the canvas we want to allow to be drawn on
	int totalVerticalSpace = -1;
	
	// a value for a minimum space in pixels between the topmost and bottommost chromosomes and the edge of the canvas
	int minVertBuffer = 50;
	
	// =========================================c'tor============================================
	
	public Canvas2D(MapViewerFrame frame, LinkSet links)
	{
		this.frame = frame;
		this.links = links;
		loadData();
		makeLinkSubSets();
		setBackground(Color.black);
		calcChromosomePositions();
		makeColours();
		MouseHandler mouseHandler = new MouseHandler(frame);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}
	
	// =========================================methods===============================================
	
	private void loadData()
	{
		LinkedList<MapSet> mapSets = links.getMapSets();
		MapSet targetData = mapSets.get(0);
		MapSet referenceData = mapSets.get(1);
		
		// set up both genomes appropriately
		genomes = new Genome[]
		{ new Genome(targetData.size(), targetData.getName(), Color.red),
						new Genome(referenceData.size(), referenceData.getName(), Color.blue) };
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// System.out.println("painting");
		Graphics2D g2 = (Graphics2D) g;
		
		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// work out where we have to place the chromosomes
		calcChromosomePositions();
		
		// draw the chromosomes in both genomes
		drawGenomes(g2);
		
		// if the zoom factor is sufficiently high, draw the markers onto the chromosome and marker labels next to it
		drawFeatures();
		
		// optionally draw lines between chromos if mouse over has been detected
		if (inTriggerArea && selectedChromoIndex != -1)
		{
			drawLines(g2);
		}
		
		// labels
		drawLabels(g2);
		
		// scroll controls
		drawScrollControls(g2);
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Works out where the chromosomes are to be placed
	 */
	private void calcChromosomePositions()
	{
		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		// work out what we can draw on vertically
		totalVerticalSpace = canvasHeight - minVertBuffer * 2;
		
		// check whether genome 1 or 2 has more chromosomes
		maxChromos = 0;
		if (genomes[0].chromosomes.length > genomes[1].chromosomes.length)
		{
			maxChromos = genomes[0].chromosomes.length;
		}
		else
		{
			maxChromos = genomes[1].chromosomes.length;
		}
		
		// work out other coords accordingly:
		
		// the combined height of a chromosome and the space below it extending to the next chromosome in the column
		chromoUnit = canvasHeight / (maxChromos + 1); // adding 1 gives us a buffer space
		// height of chromosomes
		chromoHeight = (int) (chromoUnit * 0.8);
		// space between chromosomes vertically = remainder of whatever in the unit is not chromo
		chromoSpacing = chromoUnit - chromoHeight;
		
		// x position of genome 1 i.e. first column of chromos
		genomes[0].xPosition = (int) (canvasWidth * 0.35);
		// x position of genome 2 (second column of chromos)
		genomes[1].xPosition = (int) (canvasWidth * 0.65);
		
		// now work out the y positions for each chromosome in each genome
		for (int i = 0; i < genomes.length; i++)
		{
			// first set the distance from the top of the frame to the top of the first chromo
			int spacer = (canvasHeight - (genomes[i].chromosomes.length * chromoUnit)) / 2;
			int currentY = spacer;
			
			// width of chromosomes
			genomes[i].chromoWidth = canvasWidth / 200;
			
			// then place the chromos from there
			for (int j = 0; j < genomes[i].chromosomes.length; j++)
			{
				// get the map for the currently selected chromosome
				MapSet targetMapSet = links.getMapSets().get(i);
				ChromoMap chromoMap = targetMapSet.getMap(j);
				
				genomes[i].chromosomes[j] = new Chromosome(chromoHeight, genomes[i].chromoWidth, genomes[i].xPosition,
								currentY, j,chromoMap);
				currentY += chromoUnit;
			}
			genomes[i].currentVerticalExtent = currentY;
			// System.out.println("currentVerticalExtent of genome " + genomes[i].name + " = " + genomes[i].currentVerticalExtent );
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// draws columns of chromosomes onto the canvas, or a detailed continuous section of genome
	private void drawGenomes(Graphics2D g2)
	{
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(200, 200, 200);
		
		// draw the chromosomes
		for (int i = 0; i < genomes.length; i++)
		{
			// draw all the chromosomes individually
			if (genomes[i].drawChromos)
			{
				// these vectors store info about what area we have painted the chromosomes in
				Vector<Rectangle> rectangles = new Vector<Rectangle>();
				Vector<Chromosome> chromosomes = new Vector<Chromosome>();
				
				for (int j = 0; j < genomes[i].chromosomes.length; j++)
				{
					// draw first half of chromo
					GradientPaint gradient = new GradientPaint(genomes[i].xPosition, genomes[i].chromosomes[j].yPosition, genomes[i].colour, genomes[i].xPosition + genomes[i].chromoWidth, genomes[i].chromosomes[j].yPosition, offWhite);
					g2.setPaint(gradient);
					g2.fillRect(genomes[i].xPosition, genomes[i].chromosomes[j].yPosition,
									genomes[i].chromoWidth, chromoHeight);
					
					// draw second half of chromo
					GradientPaint whiteGradient = new GradientPaint(genomes[i].xPosition + genomes[i].chromoWidth, genomes[i].chromosomes[j].yPosition, offWhite, genomes[i].xPosition + genomes[i].chromoWidth + genomes[i].chromoWidth, genomes[i].chromosomes[j].yPosition, genomes[i].colour);
					g2.setPaint(whiteGradient);
					g2.fillRect(genomes[i].xPosition + genomes[i].chromoWidth,
									genomes[i].chromosomes[j].yPosition,
									genomes[i].chromoWidth, chromoHeight);
					
					// add the bounding rectangle for the whole chromosome and the Chromosome itself
					Rectangle boundingRect = new Rectangle(genomes[i].xPosition, genomes[i].chromosomes[j].yPosition, genomes[i].chromoWidth, chromoHeight);
					rectangles.add(boundingRect);
					chromosomes.add(genomes[i].chromosomes[j]);
				}
				// now update the visible genome area
				genomes[i].visibleGenomeArea.makeChromoAreaMap(rectangles, chromosomes);
			}
			
			// draw a zoomed in detail section only
			else
			{
				// need to adjust the chromosome width by the zoomfactor
				// only do this if the zoom factor is greater than null
				if (genomes[i].zoomFactor > 1)
				{
					int newWidth = (int) (genomes[i].chromoWidth * genomes[i].zoomFactor * 0.05);
					if (newWidth > genomes[i].chromoWidth)
					{
						genomes[i].chromoWidth = newWidth;
					}
					else
					{
						genomes[i].chromoWidth += genomes[i].chromoWidth * 0.2;
					}
				}
				
				// this time fill the whole canvas vertically, as allowed by the limit set in totalVerticalSpace
				// draw first half of chromo
				float xFirstHalf = genomes[i].xPosition + genomes[i].chromoWidth;
				GradientPaint gradient = new GradientPaint(genomes[i].xPosition, minVertBuffer, genomes[i].colour, xFirstHalf, minVertBuffer, offWhite);
				g2.setPaint(gradient);
				g2.fillRect(genomes[i].xPosition, minVertBuffer, genomes[i].chromoWidth,
								totalVerticalSpace);
				
				// draw second half of chromo
				float xSecondHalf = genomes[i].xPosition + genomes[i].chromoWidth * 2;
				GradientPaint whiteGradient = new GradientPaint(genomes[i].xPosition + genomes[i].chromoWidth, minVertBuffer, offWhite, xSecondHalf, minVertBuffer, genomes[i].colour);
				g2.setPaint(whiteGradient);
				g2.fillRect(genomes[i].xPosition + genomes[i].chromoWidth, minVertBuffer,
								genomes[i].chromoWidth, totalVerticalSpace);
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	 */
	public void drawLines(Graphics2D g2)
	{
		// System.out.println("drawing lines for selected chromosome " + selectedChromoIndex);
		
		// get the map for the currently selected chromosome
		MapSet targetMapSet = links.getMapSets().get(0);
		ChromoMap selectedMap = targetMapSet.getMap(selectedChromoIndex);
		
		// get all the links between the selected chromosome and the reference mapset
		LinkedList linkSets = linkSetLookup.get(selectedMap);
		
		float targetMapStop = selectedMap.getStop();
		// get the real coordinates for the selected chromo and the reference chromo
		int selectedChromoX = genomes[0].xPosition + genomes[0].chromoWidth * 2;
		int selectedChromoY = genomes[0].chromosomes[selectedChromoIndex].yPosition;
		int referenceChromoX = genomes[1].xPosition;
		
		for (Object selectedLinks : linkSets)
		{
			// change the colour of the graphics object so that each link subset is colour coded
			int index = linkSets.indexOf(selectedLinks);
			g2.setColor(colours[index]);
			
			// transparency
			// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			
			// for each link in the linkset
			for (Link link : (LinkSet) selectedLinks)
			{
				// get the positional data of feature1 (which is on the selected chromo) and the end point of the map
				float feat1Start = link.getFeature1().getStart();
				
				// get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
				float feat2Start = link.getFeature2().getStart();
				ChromoMap owningMap = link.getFeature2().getOwningMap();
				float referenceMapStop = owningMap.getStop();
				int refChromoIndex = owningMap.getOwningMapSet().getMaps().indexOf(owningMap);
				int referenceChromoY = genomes[1].chromosomes[refChromoIndex].yPosition;
				
				// convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
				int targetY = (int) (feat1Start / (targetMapStop / chromoHeight)) + selectedChromoY;
				int referenceY = (int) (feat2Start / (referenceMapStop / chromoHeight)) + referenceChromoY;
				
				// draw the line
				g2.drawLine(selectedChromoX, targetY, referenceChromoX, referenceY);
			}
			
			// restore normal opacity for drawing
			// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method precomputes subsets of links between each target chromosome and the reference genome so that drawing them is quicker.
	 */
	private void makeLinkSubSets()
	{
		linkSetLookup = new Hashtable<ChromoMap, LinkedList>();
		
		MapSet targetMapSet = links.getMapSets().get(0);
		MapSet referenceMapSet = links.getMapSets().get(1);
		
		// for each chromosome in the target mapset
		for (ChromoMap targetMap : targetMapSet)
		{
			// create a new LinkedList which holds all the linksets of links between this chromosome and the reference chromosomes
			LinkedList<LinkSet> linkSets = new LinkedList<LinkSet>();
			// for each reference chromosome
			for (ChromoMap refMap : referenceMapSet)
			{
				// make a linkset that contains only the links between this chromo and the target chromo
				LinkSet linkSubset = links.getLinksBetweenMaps(targetMap, refMap);
				// add the linkset to the list
				linkSets.add(linkSubset);
			}
			
			// then add the list to the hashtable
			linkSetLookup.put(targetMap, linkSets);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Makes an array of colours that can be used to draw the lines between chromosomes. Uses some random numbers but also restricts the range of colours so the overall pallette is not too garish.
	 */
	private void makeColours()
	{
		colours = new Color[maxChromos];
		float increment = 1 / (float) maxChromos;
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
//			colours[i] = Color.getHSBColor(currentHue, 0.1f, 1);
			currentHue += increment;
		}	
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawLabels(Graphics2D g2)
	{
		// sort out the font and its size
		int fontSize = canvasWidth / 50;
		Font font = new Font("Arial", Font.PLAIN, fontSize);
		g2.setFont(font);
		
		// draw a label next to each of the genomes, half way down the screen
		g2.setColor(Color.white);
		
		// antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// genome names appear next to the column of chromosomes
		// g2.drawString(genomes[0].name, (int) (canvasWidth * 0.2), canvasHeight / 2);
		// g2.drawString(genomes[1].name, (int) (canvasWidth * 0.8), canvasHeight / 2);
		
		// draw a label next to each chromosome
		// target genome
		for (int i = 0; i < genomes[0].chromosomes.length; i++)
		{
			g2.drawString("" + (i + 1), genomes[0].xPosition - genomes[0].chromoWidth * 3,
							genomes[0].chromosomes[i].yPosition + chromoHeight / 2);
		}
		// reference genome
		for (int i = 0; i < genomes[1].chromosomes.length; i++)
		{
			g2.drawString("" + (i + 1), genomes[1].xPosition + genomes[1].chromoWidth * 4,
							genomes[1].chromosomes[i].yPosition + chromoHeight / 2);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawTriangle(Graphics2D g2, int x, int y, boolean pointUpwards, int size)
	{
		
		int sides = 3;
		int[] xpoints = new int[sides];
		int[] ypoints = new int[sides];
		
		if (pointUpwards)
		{
			xpoints[0] = x;
			xpoints[1] = x + size;
			xpoints[2] = x - size;
			
			ypoints[0] = y - size * 2;
			ypoints[1] = y - size;
			ypoints[2] = y - size;
		}
		else
		{
			xpoints[0] = x;
			xpoints[1] = x + size;
			xpoints[2] = x - size;
			
			ypoints[0] = y;
			ypoints[1] = y - size;
			ypoints[2] = y - size;
		}
		
		Polygon triangle = new Polygon(xpoints, ypoints, sides);
		g2.draw(triangle);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawScrollControls(Graphics2D g2)
	{
		// get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		// color (dark green)
		g2.setColor(new Color(0, 160, 0));
		
		// size
		int size = 20;
		
		// positioning
		int xLeft = (int) (canvasWidth * 0.05);
		int y = (int) (canvasHeight / 2);
		int xRight = (int) (canvasWidth * 0.95);
		int spacer = 5;
		
//		// left hand controls
//		drawTriangle(g2, xLeft, y + size - spacer, true, size);
//		drawTriangle(g2, xLeft, y + size + spacer, false, size);
//		
//		// right hand controls
//		drawTriangle(g2, xRight, y + size - spacer, true, size);
//		drawTriangle(g2, xRight, y + size + spacer, false, size);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawFeatures()
	{
		// if the zoom factor is sufficiently high, draw the markers onto the chromosome and marker labels next to it
		for (int i = 0; i < genomes.length; i++)
		{
			if (genomes[i].zoomFactor >= 2)
			{
				// first find out what chromosome is currently selected
				
				// find out which region of the chromosome we are zoomed into
				// scale this appropriately
				// get the chromomap
				// get all the features that have their start position in the currently shown interval
				// draw them
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	public void zoomTo(int mousePressedX, int mousePressedY, int mouseReleasedX, int mouseReleasedY)
	{
		// System.out.println("zooming into area delimited by " + mouseReleasedX +"," + mouseReleasedY);
		
		// work out the size of the selected area
		int selectedYDistance = mouseReleasedY - mousePressedY;
		// System.out.println("selectedYDistance = "+ selectedYDistance);
		
		// find out whether the zoom was in the target or the reference genome
		boolean tgSelected = false;
		boolean rgSelected = false;
		Genome selectedGenome = null;
		if (mousePressedX < genomes[0].xPosition && mouseReleasedX > genomes[0].xPosition)
		{
			// we have selected an area of the target genome
			System.out.println("target genome selected");
			selectedGenome = genomes[0];
		}
		else
			if (mousePressedX < genomes[1].xPosition && mouseReleasedX > genomes[1].xPosition)
			{
				// we have selected an area of the reference genome
				System.out.println("reference genome selected");
				selectedGenome = genomes[1];
			}
		
		// work out new zoom factor from the selected area
		selectedGenome.zoomFactor += selectedGenome.currentVerticalExtent / selectedYDistance;
		selectedGenome.currentVerticalExtent = totalVerticalSpace;
		selectedGenome.drawChromos = false;
		System.out.println("new zoom factor  = " + selectedGenome.zoomFactor);
		
		//check whether we have any selected chromosomes in our selected area
		Rectangle selectedRect = new Rectangle(mousePressedX, mousePressedY, 
						mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY);
		
		//get the map with intersecting rectangles
		HashMap<Rectangle, Chromosome> selectedChromos = selectedGenome.visibleGenomeArea.getIntersectingChromos(selectedRect);
		//update the existing one
		selectedGenome.visibleGenomeArea.chromosomeAreas = selectedChromos;

		// repaint the canvas
		repaint();
		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	public void resetZoomFactor(int genomeIndex)
	{
		if (genomeIndex == 0)
		{
			genomes[0].zoomFactor = 1;
			genomes[0].drawChromos = true;
		}
		else
			if (genomeIndex == 1)
			{
				genomes[1].zoomFactor = 1;
				genomes[1].drawChromos = true;
			}
		
		repaint();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
}// end class

