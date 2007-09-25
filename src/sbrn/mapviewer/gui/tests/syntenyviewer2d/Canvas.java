package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;

import javax.swing.*;

public class Canvas extends JPanel
{

	/* size of the frame */
	int frameHeight = 800;
	int frameWidth = 400;

	// bars represent chromosomes
	/* height of chromosomes */
	int barHeight = 30;

	/* width of chromosomes */
	int barWidth = 8;

	/* the size of the genome 1 -- hard coded for now */
	int numChromos1 = 7;

	/* the size of the genome 2 -- hard coded for now */
	int numChromos2 = 12;

	/* leave this amount of space top and bottom */
	int bufferSpace = 20;

	/* leave this amount of space either side of the columns of chromosomes */
	int sideSpace = 100;

	/* leave this amount of space between columns of chromosomes */
	int colSpacing = 200;

	/* space between chromosomes vertically */
	int chromoSpacing = barHeight;

	/* colors for lines */
	Color[] colours = new Color[]
	{ Color.CYAN, Color.YELLOW, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.GREEN,
					Color.CYAN, Color.YELLOW, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE,
					Color.PINK, Color.GREEN, };

	/* chromosome x positions -- genome 1 */
	int genome1Xposition = sideSpace;

	/* chromosome y positions -- genome 1 */
	int[] genome1Ypositions = null;

	/* chromosome x positions -- genome 2 */
	int genome2Xposition = sideSpace + colSpacing;

	/* chromosome y positions -- genome 2 */
	int[] genome2Ypositions = null;

	/* indicates whether the mouse is in an area that should trigger lines to be drawn */
	boolean inTriggerArea = false;

	/* indicates that the mouse was last in an area that should trigger lines to be drawn */
	boolean previousAreaWasTrigger = false;

	/* indicates that the mouse was last outside any area that should trigger lines to be drawn */
	boolean previousAreaWasNonTrigger = false;

	/* number of chromosome in the reference genome that was triggered to have its relationships drawn */
	int triggeredChromo = -1;

	// =========================================c'tor============================================

	public Canvas()
	{
		setBackground(Color.black);
		calcChromosomePositions();
		MouseHandler mouseHandler = new MouseHandler(this);
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------

	public void paint(Graphics g)
	{

		Graphics2D g2 = (Graphics2D) g;
		super.paintComponents(g);

		// draw genome 1
		g2.setColor(Color.RED);
		for (int i = 0; i < numChromos1; i++)
		{
			g2.fillRoundRect(genome1Xposition, genome1Ypositions[i], barWidth, barHeight, 5, 5);
		}

		// draw genome 2
		g2.setColor(Color.BLUE);
		for (int i = 0; i < numChromos2; i++)
		{
			g2.fillRoundRect(genome2Xposition, genome2Ypositions[i], barWidth, barHeight, 5, 5);
		}

		// optionally draw lines between chromos if mouse over has been detected
		if (inTriggerArea && triggeredChromo != -1)
		{
			drawLines(g2);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Works out where the chromosomes are to be placed
	 */
	private void calcChromosomePositions()
	{
		int currentY = (frameHeight - (numChromos1 * barHeight * 2)) / 2;

		// genome 1
		genome1Ypositions = new int[numChromos1];
		for (int i = 0; i < numChromos1; i++)
		{
			genome1Ypositions[i] = currentY;
			currentY += chromoSpacing * 2;
		}

		// genome 2
		currentY = (frameHeight - (numChromos2 * barHeight * 2)) / 2 + bufferSpace;
		genome2Ypositions = new int[numChromos2];
		for (int i = 0; i < numChromos2; i++)
		{
			genome2Ypositions[i] = currentY;
			currentY += chromoSpacing * 2;
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

			for (int j = 0; j < numLines; j++)
			{
				g2.drawLine(
								genome1Xposition + barWidth,
								genome1Ypositions[triggeredChromo] + getRandomInt(barHeight),
								genome2Xposition,
								genome2Ypositions[i] + getRandomInt(barHeight));
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
}// end class
