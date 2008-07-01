package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;

public class GChromoMap
{
	
	// ============================vars==================================
	
	// size stuff
	public int height;
	public int width;
	
	// position stuff
	public int x;
	public int y;
	
	public Color colour;
	public String name;
	
	// the index of the chromosome in the genome
	// starts at 1
	int index;
	
	// the owning map set
	public GMapSet owningSet;
	
	// this is a bounding rectangle which contains the chromosome and which serves the purpose of being able to detect
	// mouse events such as the user clicking on the chromosome to select it or zoom it
	public Rectangle boundingRectangle = new Rectangle();
	
	// the corresponding ChromoMap object -- this holds the actual data
	ChromoMap chromoMap;
	
	// arrays with Feature names and positions for fast access during drawing operations
	String[] featureNames;
	float[] featurePositions;
	
	// ============================c'tors==================================
	
	public GChromoMap(Color colour, String name, int index, GMapSet owningSet)
	{
		this.colour = colour;
		this.name = name;
		this.index = index;
		this.owningSet = owningSet;
		this.chromoMap = (ChromoMap) owningSet.mapSet.getMaps().get(index);
		
		initArrays();
	}
	
	// ============================methods==================================
	/**
	 * Draws the map from coordinate 0,0 given the current position of the Graphics object
	 */
	public void paintMap(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		// draw the map
		
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(200, 200, 200);
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0, 0, colour, width, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width, height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width, 0, offWhite, width * 2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width, 0, width, height);
		
		// draw the index of the map in the genome
		g2.drawString(String.valueOf(index + 1), -20, 20);
		
		if (owningSet.paintMarkers)
		{
			drawFeatures(g2);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers and labels
	private void drawFeatures(Graphics2D g2)
	{
		g2.setColor(Color.GREEN);
		
		float mapEnd = chromoMap.getStop();
		float scalingFactor = height / mapEnd;
		
		for (int i = 0; i < featurePositions.length; i++)
		{
			float yPos;
			if (featurePositions[i] == 0.0f)
			{
				yPos = 0.0f;
			}
			else
			{
				yPos = featurePositions[i] * scalingFactor;
			}
			// draw a line for the marker
			g2.drawLine(0, (int) yPos, width * 2, (int) yPos);
			
			if (owningSet.paintLabels)
			{
				g2.drawString(featureNames[i], width * 2 + 5, (int) yPos + 5);
			}
		}
		
//		Iterator iter = chromoMap.iterator();
//		float mapEnd = chromoMap.getStop();
//		float scalingFactor = height / mapEnd;
//		System.out.println("====================================");
//		System.out.println("drawing features for chromo " + name + " -- total " + chromoMap.countFeatures());
//		while (iter.hasNext())
//		{
//			Feature f = (Feature) iter.next();
//			String featureName = f.getName();
//			
//			// now work out where we need to draw the line for the feature
//			float featStart = f.getStart();
//			System.out.println("featStart = " + featStart);
//			System.out.println("mapEnd = " + mapEnd);
//			System.out.println("feature name = " + featureName);
//			float yPos;
//			if (featStart == 0.0f)
//			{
//				yPos = 0.0f;
//			}
//			else
//			{
//				yPos = featStart * scalingFactor;
//				System.out.println("height = " + height);
//				System.out.println("yPos = " + yPos);
//			}
//			
//			// draw a line for the marker
//			g2.drawLine(0, (int) yPos, width * 2, (int) yPos);
//			
//			if (owningSet.paintLabels)
//			{
//				g2.drawString(featureName, width * 2 + 5, (int) yPos + 5);
//			}
//		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// initialises the arrays we need for fast drawing
	private void initArrays()
	{
		int numFeatures = chromoMap.countFeatures();
		featureNames = new String[numFeatures];
		featurePositions = new float[numFeatures];
		
		LinkedList<Feature> featureList = chromoMap.getFeatureList();
		for (int i = 0; i < featureList.size(); i++)
		{		
			Feature f = featureList.get(i);
			featureNames[i] = f.getName();
			featurePositions[i] = f.getStart();
		}
	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------
}
