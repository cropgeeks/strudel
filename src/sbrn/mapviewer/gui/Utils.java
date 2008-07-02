package sbrn.mapviewer.gui;

import java.awt.Color;

public class Utils
{
	// --------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Makes an array of colours that can be used to draw the lines between chromosomes. Uses some random numbers but also restricts the range of colours so the overall pallette is not too garish.
	 */
	public static Color[] makeColours(int numColours)
	{
		Color[] colours = new Color[numColours];
		float increment = 1 / (float) numColours;
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
			colours[i] = Color.getHSBColor(currentHue, 1, 1);
			currentHue += increment;
		}
		return colours;
	}
}
