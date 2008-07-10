package sbrn.mapviewer.gui;

import java.awt.Color;
import java.util.*;

import sbrn.mapviewer.gui.entities.*;

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
			colours[i] = Color.getHSBColor(currentHue, 0.6f, 0.6f);
			currentHue += increment;
		}
		return colours;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	public static GChromoMap getSelectedMap(LinkedList<GMapSet> gMapSetList, int x, int y)
	{
		GChromoMap selectedMap = null;
		
		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.contains(x, y))
				{
					selectedMap = gChromoMap;
					break;
				}
			}
		}		
		return selectedMap;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
}
