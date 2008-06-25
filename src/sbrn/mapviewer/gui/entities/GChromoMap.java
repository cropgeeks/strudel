package sbrn.mapviewer.gui.entities;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GChromoMap
{
	int height = 200;
	int width = 10;
	
	
//	============================methods==================================	
	/**
	 * Draws the map from coordinate 0,0 given the current position of the Graphics object
	 */
	public void paintMap(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		//draw the map
//g2.setColor(Color.BLUE);
//		g2.drawString("test", 0,0);
//		g2.fillRect(0,0,mapWidth,mapLength);
		
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(200, 200, 200);
		Color colour = Color.BLUE;
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0,0, colour, width, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0,0, width,height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width, 0, offWhite, width*2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width,0,width, height);
	}	
}
