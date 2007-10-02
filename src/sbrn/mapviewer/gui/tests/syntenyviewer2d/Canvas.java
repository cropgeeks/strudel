package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;
import java.awt.font.*;

import javax.swing.*;

public class Canvas extends JPanel
{

	/* size of the frame */
	int canvasHeight;
	int canvasWidth;

	// bars represent chromosomes
	/* height of chromosomes */
	int chromoHeight;

	/* width of chromosomes */
	int chromoWidth;

	/* the size of the genomes -- hard coded for now */
	int numChromos1 = 7;
	int numChromos2 = 12;
	
	/*the names of the genomes - hard coded for now */
	String genomeName1 = "Barley";
	String genomeName2 = "Rice";

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

	/* number of chromosome in the reference genome that was triggered to have its relationships drawn */
	int triggeredChromo = -1;
	
	int chromoUnit;
	int maxChromos = 0;
	
	SyntenyViewer2DFrame frame;
	
	//genome objects
	Genome genome1 = new Genome(numChromos1, genomeName1, Color.red);
	Genome genome2 = new Genome(numChromos2, genomeName2, Color.blue);	
	Genome [] genomes = new Genome[]{genome1,genome2};

	// =========================================c'tor============================================

	public Canvas(SyntenyViewer2DFrame frame)
	{
		this.frame = frame;	
		setBackground(Color.black);
		calcChromosomePositions();
		makeColours();
		MouseHandler mouseHandler = new MouseHandler(this);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}
	
	public Canvas()
	{
		setBackground(Color.black);
		calcChromosomePositions();
		makeColours();
		MouseHandler mouseHandler = new MouseHandler(this);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		//System.out.println("painting");			
		Graphics2D g2 = (Graphics2D) g;

		calcChromosomePositions();
		
		//draw genomes now
		Color offWhite = new Color(200,200,200);	
		for (int i = 0; i < genomes.length; i++)
		{
			//g2.setColor(genomes[i].colour);
			for (int j = 0; j < genomes[i].numChromos; j++)
			{
				//draw first half of chromo
				GradientPaint gradient = new GradientPaint( genomes[i].xPosition,   genomes[i].yPositions[j], 
								genomes[i].colour,  genomes[i].xPosition + chromoWidth,  genomes[i].yPositions[j], offWhite);
				g2.setPaint(gradient);
				g2.fillRect( genomes[i].xPosition,  genomes[i].yPositions[j], chromoWidth, chromoHeight);
				
				//draw second half of chromo
				GradientPaint whiteGradient = new GradientPaint(genomes[i].xPosition+chromoWidth,  genomes[i].yPositions[j], offWhite, 
								genomes[i].xPosition+chromoWidth + chromoWidth, genomes[i].yPositions[j], genomes[i].colour);
				g2.setPaint(whiteGradient);
				g2.fillRect(genomes[i].xPosition+chromoWidth, genomes[i].yPositions[j], chromoWidth, chromoHeight);	
			}
		}

		// optionally draw lines between chromos if mouse over has been detected
		if (inTriggerArea && triggeredChromo != -1)
		{
			drawLines(g2);
		}
		
		//labels
		drawLabels(g2);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Works out where the chromosomes are to be placed
	 */
	private void calcChromosomePositions()
	{
		//get current size of frame
		canvasHeight = getHeight();
		canvasWidth = getWidth();
		
		//check whether genome 1 or 2 has more chromosomes
		if(numChromos1>numChromos2)
		{
			maxChromos = numChromos1;
		}
		else
		{
			maxChromos = numChromos2;
		}
		
		//work out other coords accordingly:
		
		//the combined height of a chromosome and the space below it extending to the next chromosome in the column
		chromoUnit = canvasHeight/(maxChromos+1); //adding 1 gives us a buffer space	
		// height of chromosomes 
		chromoHeight = (int)(chromoUnit*0.8);
		//space between chromosomes vertically = remainder of whatever in the unit is not chromo
		chromoSpacing = chromoUnit- chromoHeight;
		// width of chromosomes 
		chromoWidth = canvasWidth/150;

		//position of genome 1 i.e. first column of chromos
		genome1.xPosition = (int)(canvasWidth*0.4);
		//position of genome 2 (second column of chromos)
		genome2.xPosition = (int)(canvasWidth*0.6);

		//now work out the y positions for each chromosome in each genome
		for (int i = 0; i < genomes.length; i++)
		{
			//first set the distance from the top of the frame to the top of the first chromo
			int spacer = (canvasHeight - (genomes[i].numChromos*chromoUnit))/2;
			int currentY = spacer;
			//System.out.println("currentY = " + currentY);
			
			//then place the chromos from there
			for (int j = 0; j < genomes[i].numChromos; j++)
			{
				genomes[i].yPositions[j] = currentY;
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
		for (int i = 0; i < numChromos2; i++)
		{
			g2.setColor(colours[i]);
			int numLines = (int) (Math.random() * 10);
			numLines = numLines - 2;

			//g2.setStroke(new BasicStroke(2));
			for (int j = 0; j < numLines; j++)
			{
				g2.drawLine(
								genome1.xPosition + chromoWidth*2,
								genome1.yPositions[triggeredChromo] + getRandomInt(chromoHeight),
								genome2.xPosition,
								genome2.yPositions[i] + getRandomInt(chromoHeight));
			}
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
	 * Makes an array of colours that can be used to draw the lines between chromosomes.
	 * Uses some random numbers but also restricts the range of colours so the overall pallette is not too garish.
	 */
	private void makeColours()
	{
		colours = new Color[maxChromos];
		
		int currentTone = 50;
		int maxGrey = 255;
		float interval = (maxGrey - currentTone)/colours.length;
		
		for (int i = 0; i < colours.length; i++)
		{	
			//System.out.println("currentTone = " + currentTone);	
			colours[i] = new Color(currentTone,getRandomInt(255),getRandomInt(255));
			currentTone = currentTone+ (int)interval;
		}
	}
	
	
//	 --------------------------------------------------------------------------------------------------------------------------------
	
	private void drawLabels(Graphics2D g2)
	{
		//sort out the font and its size
		int fontSize = canvasWidth/70;
		Font font = new Font("Arial",Font.PLAIN, fontSize);
		g2.setFont(font);

		//draw a label next to each of the genomes, half way down the screen
		g2.setColor(Color.white);
		g2.drawString(genomeName1, (int)(canvasWidth*0.3), canvasHeight/2);
		g2.drawString(genomeName2, (int)(canvasWidth*0.7), canvasHeight/2);
		
		//draw a label next to each chromosome
		//genome 1
		for (int i = 0; i < genome1.yPositions.length; i++)
		{
			g2.drawString("" + (i+1), genome1.xPosition - chromoWidth*3, genome1.yPositions[i]+chromoHeight/2);
		}
		//genome 2
		for (int i = 0; i < genome2.yPositions.length; i++)
		{
			g2.drawString("" + (i+1), genome2.xPosition + chromoWidth*4, genome2.yPositions[i]+chromoHeight/2);
		}
	}
	
//	 --------------------------------------------------------------------------------------------------------------------------------
}// end class









