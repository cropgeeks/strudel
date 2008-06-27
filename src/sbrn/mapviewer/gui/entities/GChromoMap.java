package sbrn.mapviewer.gui.entities;

import java.awt.*;

public class GChromoMap
{
	
//	============================vars==================================	
	
	//size stuff
	public int height;
	public int width;
	
	//position stuff
	public int x;
	public int y;
	
	public Color colour;
	public String name;
	
	//the index of the chromosome in the genome
	//starts at 1
	int index;
	
	//the owning map set
	public GMapSet owningSet;
	
	//this is a bounding rectangle which contains the chromosome and which serves the purpose of being able to detect 
	//mouse events such as the user clicking on the chromosome to select it or zoom it
	public Rectangle boundingRectangle = new Rectangle();
	
//	============================c'tors==================================	
	

	public GChromoMap(Color colour, String name,int index,GMapSet owningSet)
	{
		this.colour = colour;
		this.name = name;
		this.index = index;
		this.owningSet = owningSet;
	}	
	
//	============================methods==================================	
	/**
	 * Draws the map from coordinate 0,0 given the current position of the Graphics object
	 */
	public void paintMap(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//draw the map
		
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(200, 200, 200);
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0,0, colour, width, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0,0, width,height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width, 0, offWhite, width*2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width,0,width, height);
				
		//draw the index of the map in the genome
		g2.drawString(String.valueOf(index+1), -20,20);
	}
//----------------------------------------------------------------------------------------------------------------------------------------------
}
