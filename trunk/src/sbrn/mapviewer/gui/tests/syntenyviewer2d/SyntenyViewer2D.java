package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

/**
 * This class is a JFrame GUI component which visualises synteny between two genomes. Users can hover the mouse
 * over a chromosome of the reference genome and the relationships with chromosomes of the other genome are drawn accordingly. 
 * 
 * Currently only uses randomly generated data for demo purposes -- proper data support still to be implemented.
 * 
 * @author Micha Bayer, Scottish Crop Research Institute
 * 
 */
public class SyntenyViewer2D extends JFrame implements MouseInputListener
{
	/*size of the frame*/
	int frameHeight = 800;
	int frameWidth = 400;
	
	// bars represent chromosomes
	/*height of chromosomes*/
	private int barHeight = 30;
	
	/*width of chromosomes*/
	private int barWidth = 8;
	
	/*the size of the genome 1  -- hard coded for now*/
	private int numChromos1 = 7;
	
	/*the size of the genome 2  -- hard coded for now*/
	private int numChromos2 = 12;
	
	/*leave this amount of space top and bottom*/
	int bufferSpace = 20;
	
	/*leave this amount of space either side of the columns of chromosomes*/
	int sideSpace = 100;
	
	/*leave this amount of space between columns of chromosomes*/
	int colSpacing = 200;
	
	/*space between chromosomes vertically*/
	int chromoSpacing = barHeight;
	
	/*colors for lines*/
	private Color[] colours = new Color[]
	                                    { Color.CYAN, Color.YELLOW, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.GREEN,
					Color.CYAN, Color.YELLOW, Color.RED, Color.BLACK, Color.MAGENTA, Color.ORANGE,
					Color.PINK, Color.GREEN, };
	
	/*chromosome x positions -- genome 1*/
	private int genome1Xposition = sideSpace;
	
	/*chromosome y positions -- genome 1*/
	private int[] genome1Ypositions = null;
	
	/*chromosome x positions -- genome 2*/
	private int genome2Xposition = sideSpace + colSpacing;
	
	/*chromosome y positions -- genome 2*/
	private int[] genome2Ypositions = null;
	
	/*indicates whether the mouse is in an area that should trigger lines to be drawn*/
	boolean inTriggerArea = false;
	
	/*indicates that the mouse was last in an area that should trigger lines to be drawn*/
	boolean previousAreaWasTrigger = false;
	
	/*indicates that the mouse was last outside any area that should trigger lines to be drawn*/
	boolean previousAreaWasNonTrigger = false;
	
	/* number of chromosome in the reference genome that was triggered to have its relationships drawn*/
	int triggeredChromo = -1;
	
	static SyntenyViewer2D comp;
	
//====================================constructor====================================
	
	public SyntenyViewer2D()
	{
		calcChromosomePositions();
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
//=======================================methods====================================	
	
	public static void main(String s[])
	{
		comp = new SyntenyViewer2D();
		comp.setSize(400, 800);
		comp.setBackground(Color.BLACK);
		comp.setVisible(true);
		comp.setTitle("AllByAllComp2D");	
		comp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.out.println("background colour = " + comp.getBackground().toString());

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
		currentY =  (frameHeight - (numChromos2 * barHeight * 2)) / 2 +bufferSpace;
		genome2Ypositions = new int[numChromos2];
		for (int i = 0; i < numChromos2; i++)
		{
			genome2Ypositions[i] = currentY;
			currentY += chromoSpacing * 2;
		}
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
	
	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Draws the lines between a chromsome of the reference genome and all potential homologues in the
	 * compared genome
	 */
	public void drawLines(Graphics2D g2)
	{
		for (int i = 0; i < numChromos2; i++)
		{
			g2.setColor(colours[i]);
			int numLines = (int) (Math.random() * 10);
			numLines = numLines-2;
			
			for (int j = 0; j < numLines; j++)
			{
				g2.drawLine(genome1Xposition + barWidth, genome1Ypositions[triggeredChromo] + getRandomInt(barHeight), genome2Xposition, genome2Ypositions[i] + getRandomInt(barHeight));
			}
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Returns a random int that is less than a certain value
	 * @param max -- the maximum to be returned
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
	
	public void mouseMoved(MouseEvent arg0)
	{

		int x = arg0.getX();
		int y = arg0.getY();
		
		System.out.println("previousAreaWasTrigger = " + previousAreaWasTrigger);
		System.out.println("previousAreaWasNonTrigger = " + previousAreaWasNonTrigger);

		// check if mouse is now in area to the right of one of
		// the
		// reference chromosomes
		for (int i = 0; i < numChromos1; i++)
		{
			// work out the trigger area for each chromosome
			int triggerXMin = sideSpace;
			int triggerXMax = sideSpace + colSpacing;
			int triggerYMin = genome1Ypositions[i];
			int triggerYMax = genome1Ypositions[i] + barHeight;
			
			if (x > triggerXMin && x < triggerXMax && y > triggerYMin && y < triggerYMax)
			{
				triggeredChromo = i;
				inTriggerArea = true;
				break;
			}
			else
			{
				inTriggerArea = false;
				triggeredChromo = -1;
			}
		}
		
		// only repaint if we have just entered a new kind of
		// area
		// just entered trigger area
		if (inTriggerArea && !previousAreaWasTrigger)
		{
			repaint();
			previousAreaWasTrigger = true;
			previousAreaWasNonTrigger = false;
		}
		// just entered non-trigger area
		if (!inTriggerArea && previousAreaWasTrigger)
		{
			repaint();
			previousAreaWasTrigger = false;
			previousAreaWasNonTrigger = true;
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	// don't need these just now
	public void mouseClicked(MouseEvent arg0){}	
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}	
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
	public void mouseDragged(MouseEvent arg0){}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
}// end class

