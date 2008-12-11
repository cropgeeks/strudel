package sbrn.mapviewer.gui;

import java.awt.*;
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
		for (GMapSet gMapSet : MapViewer.winMain.mainCanvas.gMapSetList)
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
		System.out.println("gMapSet found =  " + gMapSet.name);
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
		for (GMapSet gMapSet : MapViewer.winMain.mainCanvas.gMapSetList)
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
		for (GMapSet gMapSet : winMain.mainCanvas.gMapSetList)
		{
			// check whether a line drawn at y intersects within one of the bounding rectangles of our chromosomes
			//we can just use a rectangle a single pixel wide for this purpose so we can use the existing API for the Rectangle class
			Rectangle intersectLine = null;
			
			//we need to set the intersect line up so it extends only over the part of the screen we want to test for intersection in
			//this depends on the index of the mapset in the list
			//if we have two genomes
			if(winMain.mainCanvas.gMapSetList.size() == 2)
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
			else if(winMain.mainCanvas.gMapSetList.size() == 3)
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
	
	// --------------------------------------------------------------------------------------------------------------------------------
}
