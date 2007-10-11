package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;
import java.awt.font.*;
import java.util.*;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.data.Link;
import sbrn.mapviewer.gui.tests.mainGui.*;

public class Canvas2D extends JPanel
{

	/* size of the frame */
	int canvasHeight;
	int canvasWidth;

	int chromoHeight;
	int chromoWidth;
	
	int maxChromos;
	
	/* space between chromosomes vertically */
	int chromoSpacing;

	/* colors for lines */
	Color[] colours = null;

	/* indicates whether the mouse is in an area that should trigger lines to be drawn */
	boolean inTriggerArea = false;

	/* indicates that the mouse was last in an area that should trigger lines to be drawn */
	boolean previousAreaWasTrigger = false;

	/* indicates that the mouse was last outside any area that should trigger lines to be drawn */
	boolean previousAreaWasNonTrigger = false;

	/* number of chromosome in the target genome that was triggered to have its relationships drawn */
	int selectedChromoIndex = -1;

	int chromoUnit;

	// genome objects
	Genome[] genomes;

	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	private static LinkSet links = null;
	
	LinkedList<LinkSet> linkSubSets;
	
	MapViewerFrame frame;

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

	private void loadData()
	{
		LinkedList<MapSet> mapSets = links.getMapSets();
		MapSet targetData = mapSets.get(0);
		MapSet referenceData = mapSets.get(1);
		
		//set up both genomes appropriately
		genomes = new Genome[] { new Genome(targetData.size(), targetData.getName(), Color.red), 
						new Genome(referenceData.size(), referenceData.getName(), Color.blue) };
		//System.out.println("referenceData.getName() = "+ referenceData.getName());

	}

	// ---------------------------------------------------------------------------------------------------------------------------------

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// System.out.println("painting");
		Graphics2D g2 = (Graphics2D) g;

		calcChromosomePositions();

		// draw genomes now
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(200, 200, 200);
		for (int i = 0; i < genomes.length; i++)
		{
			for (int j = 0; j < genomes[i].chromosomes.length; j++)
			{
				// draw first half of chromo
				GradientPaint gradient = new GradientPaint(genomes[i].xPosition, genomes[i].chromosomes[j].yPosition, genomes[i].colour, genomes[i].xPosition + chromoWidth, genomes[i].chromosomes[j].yPosition, offWhite);
				g2.setPaint(gradient);
				g2.fillRect(genomes[i].xPosition, genomes[i].chromosomes[j].yPosition, chromoWidth, chromoHeight);

				// draw second half of chromo
				GradientPaint whiteGradient = new GradientPaint(genomes[i].xPosition + chromoWidth, genomes[i].chromosomes[j].yPosition, offWhite, genomes[i].xPosition + chromoWidth + chromoWidth, genomes[i].chromosomes[j].yPosition, genomes[i].colour);
				g2.setPaint(whiteGradient);
				g2.fillRect(genomes[i].xPosition + chromoWidth, genomes[i].chromosomes[j].yPosition, chromoWidth,
								chromoHeight);
			}
		}

		// optionally draw lines between chromos if mouse over has been detected
		if (inTriggerArea && selectedChromoIndex != -1)
		{
			drawLines(g2);
		}

		// labels
		drawLabels(g2);
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
		// width of chromosomes
		chromoWidth = canvasWidth / 150;

		//x position of genome 1 i.e. first column of chromos
		genomes[0].xPosition = (int) (canvasWidth * 0.4);
		//x position of genome 2 (second column of chromos)
		genomes[1].xPosition = (int) (canvasWidth * 0.6);

		// now work out the y positions for each chromosome in each genome
		for (int i = 0; i < genomes.length; i++)
		{
			// first set the distance from the top of the frame to the top of the first chromo
			int spacer = (canvasHeight - (genomes[i].chromosomes.length * chromoUnit)) / 2;
			int currentY = spacer;

			// then place the chromos from there
			for (int j = 0; j < genomes[i].chromosomes.length; j++)
			{
				genomes[i].chromosomes[j] = new Chromosome(chromoHeight, chromoWidth, genomes[i].xPosition, currentY);
				currentY += chromoUnit;
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Draws the lines between a chromosome of the reference genome and all potential homologues in the compared genome
	 */
	public void drawLines(Graphics2D g2)
	{
		//System.out.println("drawing lines for selected chromosome " + selectedChromoIndex);
			
		//get the map for the currently selected chromosome
		MapSet targetMapSet = links.getMapSets().get(0);
		ChromoMap selectedMap = targetMapSet.getMap(selectedChromoIndex);
		
		//get all the links between the selected chromosome and the reference mapset
		LinkSet selectedLinks = linkSubSets.get(selectedChromoIndex);
		
		float targetMapStop = selectedMap.getStop();
		
		//get the real coordinates for the selected chromo and the reference chromo
		int selectedChromoX = genomes[0].xPosition + chromoWidth*2;
		int selectedChromoY = genomes[0].chromosomes[selectedChromoIndex].yPosition;
		int referenceChromoX = genomes[1].xPosition;
			
		//for each link in the linkset
		for (Link link : selectedLinks)
		{
			//get the positional data of feature1 (which is on the selected chromo) and the end point of the map
			float feat1Start = link.getFeature1().getStart();
			
			//get the owning map, positional data of feature 2 (which is on a reference chromosome) and the end point of the map
			float feat2Start = link.getFeature2().getStart();
			ChromoMap owningMap = link.getFeature2().getOwningMap();
			float referenceMapStop = owningMap.getStop();			
			int refChromoIndex = owningMap.getOwningMapSet().getMaps().indexOf(owningMap);
			int referenceChromoY = genomes[1].chromosomes[refChromoIndex].yPosition;
						
			//convert these to coordinates by obtaining the coords of the appropriate chromosome object and scaling them appropriately
			int targetY = (int)(feat1Start/(targetMapStop/chromoHeight))+selectedChromoY;
			int referenceY = (int)(feat2Start/(referenceMapStop/chromoHeight))+referenceChromoY;
			
			//draw the line 
			g2.drawLine(selectedChromoX, targetY, referenceChromoX, referenceY);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------	
	
	/**
	 * This method precomputes subsets of links between each target chromosome and the reference genome so that
	 * drawing them is quicker. 
	 */
	private void makeLinkSubSets()
	{
		linkSubSets = new LinkedList<LinkSet>();
		//for each chromosome in the target mapset
		MapSet targetMapSet = links.getMapSets().get(0);
		MapSet referenceMapSet = links.getMapSets().get(1);
		for (ChromoMap map : targetMapSet)
		{
			//make a new subset of the overall linkset containing only its links with the reference genome
			LinkSet selectedLinks = links.getLinksBetweenMapandMapSet(map, referenceMapSet);
			linkSubSets.add(selectedLinks);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Returns a random int that is less than a certain value
	 * 
	 * @param max --
	 *                the maximum to be returned
	 * @return the random int
	 */
	private int getRandomInt(int max)
	{
		int rand = -1;

		while (rand > max || rand < 0)
		{
			rand = (int) (Math.random() * 100);
		}

		return rand;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/*
	 * Makes an array of colours that can be used to draw the lines between chromosomes. Uses some random numbers but also restricts the range of colours so the overall pallette is not too garish.
	 */
	private void makeColours()
	{
		colours = new Color[maxChromos];

		int currentTone = 50;
		int maxGrey = 255;
		float interval = (maxGrey - currentTone) / colours.length;

		for (int i = 0; i < colours.length; i++)
		{
			// System.out.println("currentTone = " + currentTone);
			colours[i] = new Color(currentTone, getRandomInt(255), getRandomInt(255));
			currentTone = currentTone + (int) interval;
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
		//System.out.println("genomes[0].name = "  +genomes[0].name);
		g2.drawString(genomes[0].name, (int) (canvasWidth * 0.3), canvasHeight / 2);
		g2.drawString(genomes[1].name, (int) (canvasWidth * 0.7), canvasHeight / 2);

		// draw a label next to each chromosome
		// target genome
		for (int i = 0; i < genomes[0].chromosomes.length; i++)
		{
			g2.drawString("" + (i + 1), genomes[0].xPosition - chromoWidth * 3,
							genomes[0].chromosomes[i].yPosition + chromoHeight / 2);
		}
		// reference genome
		for (int i = 0; i < genomes[1].chromosomes.length; i++)
		{
			g2.drawString("" + (i + 1), genomes[1].xPosition + chromoWidth * 4,
							genomes[1].chromosomes[i].yPosition + chromoHeight / 2);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------
}// end class

