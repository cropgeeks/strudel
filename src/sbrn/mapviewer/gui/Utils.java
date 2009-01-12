package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;

public class Utils
{
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//finds a genome by name
	public static GMapSet getGMapSetByName(String name)
	{
		GMapSet foundSet = null;
		
		//we need to search all chromomaps in all mapsets for this	
		// for all gmapsets
		for (GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSetList)
		{
			if(gMapSet.name.equals(name))
				foundSet = gMapSet;
		}
		
		return foundSet	;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//finds a Feature by name
	public static GChromoMap getGMapByName(String gMapName, String gMapSetName)
	{
		GChromoMap foundMap = null;
		
		//we need to search all chromomaps in all mapsets for this	
		// for all gmapsets
		GMapSet gMapSet =  getGMapSetByName(gMapSetName);
		
		// for all gchromomaps within each mapset
		for (GChromoMap gChromoMap : gMapSet.gMaps)
		{
			if(gChromoMap.name.equals(gMapName))
				foundMap = gChromoMap;
		}
		
		return foundMap;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//finds a Feature by name
	public static Feature getFeatureByName(String featureName)
	{
		Feature f = null;
		
		//we need to search all chromomaps in all mapsets for this	
		// for all gmapsets
		for (GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSetList)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				//get the ChromoMap object
				//look up the name in this
				f = gChromoMap.chromoMap.getFeature(featureName);
				if(f!=null)
				{
					return f;
				}
			}
		}
		
		return f;
	}
	
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
			colours[i] = Color.getHSBColor(currentHue, 0.4f, 0.8f);
			currentHue += increment;
		}
		return colours;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	//returns a toned down (strongly darkened) version of the Color colour, with the dominant 
	//primary colour being picked out and then a dark version of this being returned
	public static Color getTonedDownColour(Color colour)
	{
		Color darkenedColour = null;
		int darkValue = 30;
		
		//extract the current RGB values
		float maxValue = 0;
		int maxChannel = -1;
		float [] rgb = colour.getRGBColorComponents(null);
		for (int i = 0; i < rgb.length; i++)
		{
			if(rgb[i] > maxValue)
			{
				maxValue = rgb[i];
				maxChannel = i;
			}
		}
		
		switch (maxChannel)
		{
			case 0:
				darkenedColour =  new Color(darkValue, 0, 0);
				break;
			case 1:
				darkenedColour = new Color(0, darkValue, 0);
				break;
			case 2: 
				darkenedColour = new Color(0, 0, darkValue);
				break;
		}
		
		return darkenedColour;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
	
	
	//check whether we have a map at the coordinates x and y 
	public static GChromoMap getSelectedMap(Vector<GMapSet> gMapSetList, int x, int y)
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
	
	//check whether there is a map intersecting with a horizontal line drawn across part of the canvas at the level of  the coordinate y 
	public static GChromoMap getSelectedMap(WinMain winMain, int gMapSetIndex, int y)
	{
		GChromoMap selectedMap = null;
		
		// for each genome
		for (GMapSet gMapSet : MapViewer.winMain.dataContainer.gMapSetList)
		{
			// check whether a line drawn at y intersects within one of the bounding rectangles of our chromosomes
			//we can just use a rectangle a single pixel wide for this purpose so we can use the existing API for the Rectangle class
			Rectangle intersectLine = null;
			
			//we need to set the intersect line up so it extends only over the part of the screen we want to test for intersection in
			//this depends on the index of the mapset in the list
			//if we have one genome only
			if(MapViewer.winMain.dataContainer.gMapSetList.size() == 1)
			{
				intersectLine = new Rectangle(0,y,winMain.mainCanvas.getWidth(),1);
			}
			//if we have two genomes
			else if(MapViewer.winMain.dataContainer.gMapSetList.size() == 2)
			{
				//gMapSetList at 0 is target genome (left), at 1 is reference genome (right)
				if(gMapSetIndex == 0)
				{
					intersectLine = new Rectangle(0,y,winMain.mainCanvas.getWidth()/2,1);
				}
				else //index ==1
				{
					intersectLine = new Rectangle(winMain.mainCanvas.getWidth()/2,y,winMain.mainCanvas.getWidth(),1);
				}
			}
			//if we have three genomes
			else if(MapViewer.winMain.dataContainer.gMapSetList.size() == 3)
			{
				//gMapSetList at 0 is target genome (left), at 1 is reference genome (right)
				if(gMapSetIndex == 0)
				{
					intersectLine = new Rectangle(0,y,winMain.mainCanvas.getWidth()/3,1);
				}
				else if(gMapSetIndex == 1)
				{
					intersectLine = new Rectangle(winMain.mainCanvas.getWidth()/3,y,winMain.mainCanvas.getWidth()/3,1);
				}
				else if(gMapSetIndex == 2)
				{
					intersectLine = new Rectangle((winMain.mainCanvas.getWidth()/3)*2,y,winMain.mainCanvas.getWidth()/3,1);
				}
			}
			
			//now check all the chromosomes' bounding rectangles in this mapset for intersection			
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.intersects(intersectLine))
				{
					selectedMap = gChromoMap;
					return selectedMap;
				}
			}
		}		
		return selectedMap;
	}
	
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// finds out which of the two genomes the current selection relates to
	public static int getSelectedSet(MouseEvent e)
	{
		// figure out which genome the user is zooming
		
		int index = -1;
		
		// if we have one genome (target) only
		if (MapViewer.winMain.dataContainer.gMapSetList.size() == 1)
		{
			index = 0;
		}
		// if we have two genomes 
		else if (MapViewer.winMain.dataContainer.gMapSetList.size() == 2)
		{
			// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
			if (e.getX() < MapViewer.winMain.mainCanvas.getWidth() / 2)
			{
				// left hand side hit
				index = 0;
			}
			else
			{
				// right hand side hit
				index = 1;
			}
		}
		//if we have three genomes
		else if (MapViewer.winMain.dataContainer.gMapSetList.size() == 3)
		{
			int oneThirdCanvas = Math.round(MapViewer.winMain.mainCanvas.getWidth() / 3);
			
			// simply divide the canvas in two halves for this and figure out where on the x axis the hit has occurred
			if (e.getX() <= oneThirdCanvas)
			{
				// left hand side hit
				index = 0;
			}
			else if(e.getX() > oneThirdCanvas && e.getX() <= oneThirdCanvas*2)
			{
				// middle hit
				index = 1;
			}
			else if(e.getX() > oneThirdCanvas*2)
			{
				//right hand side hit
				index = 2;
			}
		}
		
		return index;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------
}
