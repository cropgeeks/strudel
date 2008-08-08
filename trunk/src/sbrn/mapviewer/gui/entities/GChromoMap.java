package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.WinMain;

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
	public int index;
	
	// the owning map set
	public GMapSet owningSet;
	
	// this is a bounding rectangle which contains the chromosome and which serves the purpose of being able to detect
	// mouse events such as the user clicking on the chromosome to select it or zoom it
	public Rectangle boundingRectangle = new Rectangle();
	
	// the corresponding ChromoMap object -- this holds the actual data
	public ChromoMap chromoMap;
	
	// arrays with Feature names and positions for fast access during drawing operations
	String[] allFeatureNames;
	float[] allFeaturePositions;
	public Hashtable<Integer, Feature> allFeaturesPosLookup = new Hashtable<Integer, Feature>();
	
	// these are corresponding arrays and lists which only pertain to the features which are linked to from somewhere
	public LinkedList<Feature> linkedFeatureList = new LinkedList<Feature>();
	String[] linkedFeatureNames;
	float[] linkedFeaturePositions;
	public TreeMap<Integer, Feature> linkedFeaturePosLookup = new TreeMap<Integer, Feature>();
	
	// indicates whether this map or part thereof is currently drawn on the canvas
	public boolean isShowingOnCanvas = true;
	
	// a vector containing features whose lables are to be displayed when the chromosome is drawn
	public Vector<Feature> highlightedFeatures;
	
	// do we have to draw a highlighted outline for this map
	public boolean drawHighlightOutline = false;
	
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
		Color offWhite = new Color(170, 170, 170);
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width / 2, height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width / 2, 0, offWhite, width, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width / 2, 0, width / 2 , height);
		
		// draw the index of the map in the genome
		int fontSize = WinMain.mainCanvas.getHeight() / 40;
		Font mapLabelFont = new Font("Arial", Font.BOLD, fontSize);
		g2.setFont(mapLabelFont);
		g2.setColor(new Color(150, 150, 150));		
		// decide where to place the label with the chromosome number
		// on the left hand genome we want the label on the left, right hand genome on the right
		// reference genome (right):
		if (!owningSet.isTargetGenome)
		{
			g2.drawString(String.valueOf(index + 1), width * 6, height / 2);
		}
		// target genome (left):
		else
		{
			g2.drawString(String.valueOf(index + 1), -width * 6, height / 2);
		}
		
		//now draw features and labels as required
		if (owningSet.paintLinkedMarkers && isShowingOnCanvas)
		{
			drawLinkedFeatures(g2);
			drawHighlightedFeatureLabels(g2);
		}
		else if(owningSet.paintAllMarkers && isShowingOnCanvas)
		{
			drawAllFeatures(g2);
			drawHighlightedFeatureLabels(g2);
		}
		
		//this draws a yellow outline round the map if it is selected
		if (drawHighlightOutline)
		{
			highlightMapOutline(g2);
		}
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the chromosome/map for the purpose of an overview only
	public void paintOverViewMap(Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		Color offWhite = new Color(180, 180, 180);
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width / 2, height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width / 2, 0, offWhite, width / 2 * 2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width / 2, 0, width / 2 , height);

		// draw the index of the map in the genome
		int smallFontSize = 9;
		Font overviewLabelFont = new Font("Arial", Font.BOLD, smallFontSize);
		g2.setFont(overviewLabelFont);
		g2.setColor(new Color(150, 150, 150));
		g2.drawString(String.valueOf(index + 1), width *2, height / 2);

		if (drawHighlightOutline)
		{
			g2.setColor(Color.YELLOW);
			g2.drawRect(0, 0, width-1, height);
		}

	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draws a subset of labels next to features
	private void drawHighlightedFeatureLabels(Graphics2D g2)
	{
		if (highlightedFeatures != null)
		{
			// the index of the feature in the vector of features to be labelled
			int index = 0;
			// the amount by which we want tto move the label away from the chromosome
			int labelSpacer = 20;
			
			// for all features in our vector
			for (Feature f : highlightedFeatures)
			{
				// get the name of the feature
				String featureName = highlightedFeatures.get(index).getName();
				
				// font stuff
				int fontHeight = 13;
				g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
				FontMetrics fm = g2.getFontMetrics();
				int stringWidth = fm.stringWidth(featureName);
				
				// we need these for working out the y positions
				float mapEnd = chromoMap.getStop();
				float scalingFactor = height / mapEnd;
				
				// the y position of the feature itself
				int featureY;
				if (f.getStart() == 0.0f)
				{
					featureY = 0;
				}
				else
				{
					featureY = (int) (f.getStart() * scalingFactor);
				}
				
				// the y position of the feature label
				int labelY = featureY + fontHeight / 2;
				
				// decide where to place the label on x
				// on the left hand genome we want the label on the left, right hand genome on the right
				int labelX = 0; // this is where the label is drawn from
				int lineStartX = 0; // this is where the line to the label is drawn from
				int labelLineEnd = 0; // the label connects to the line here
				// right hand genome (reference)
				if (!owningSet.isTargetGenome)
				{
					labelX = width + labelSpacer;
					lineStartX = width;
					labelLineEnd = labelX - 3;
				}
				// left hand genome (target)
				else
				{
					labelX = -stringWidth - labelSpacer;
					lineStartX = 0;
					labelLineEnd = -labelSpacer + 3;
				}

				
				// draw the label
				g2.drawString(featureName, labelX, labelY);
				
				// draw a line from the marker to the label
				g2.drawLine(lineStartX, featureY, labelLineEnd, labelY - fontHeight / 2);
				
				index++;
			}
		}
	}
	
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// draw a coloured outline using the bounding rectangle of this map
	public void highlightMapOutline(Graphics2D g2)
	{
		g2.setColor(Color.YELLOW);
		g2.drawRect(0, 0, (int) boundingRectangle.getWidth(), (int) boundingRectangle.getHeight());
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// initialises the arrays we need for fast drawing
	private void initArrays()
	{
		// init the arrays that hold ALL the features for this map
		int numFeatures = chromoMap.countFeatures();
		allFeatureNames = new String[numFeatures];
		allFeaturePositions = new float[numFeatures];
		LinkedList<Feature> featureList = chromoMap.getFeatureList();
		for (int i = 0; i < featureList.size(); i++)
		{
			Feature f = featureList.get(i);
			allFeatureNames[i] = f.getName();
			allFeaturePositions[i] = f.getStart();
			
			// also add this to a lookup table that we can use to look up features by location
			// the percent distance from the top of the chromosome to the location of this feature
			int percentDistToFeat = (int) (f.getStart() * (100 / chromoMap.getStop()));
			allFeaturesPosLookup.put(new Integer(percentDistToFeat), f);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	public void initLinkedFeatureArrays()
	{
		// init the arrays that hold only the linked to subset of features
		int numLinkedToFeatures = linkedFeatureList.size();
		linkedFeatureNames = new String[numLinkedToFeatures];
		linkedFeaturePositions = new float[numLinkedToFeatures];
		for (int i = 0; i < linkedFeatureList.size(); i++)
		{
			Feature f = linkedFeatureList.get(i);
			linkedFeatureNames[i] = f.getName();
			linkedFeaturePositions[i] = f.getStart();
			
			// also add this to a lookup table that we can use to look up features by location
			// the percent distance from the top of the chromosome to the location of this feature
			int percentDistToFeat = (int) (f.getStart() * (100 / chromoMap.getStop()));
			linkedFeaturePosLookup.put(percentDistToFeat, f);
		}
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers and labels
	private void drawAllFeatures(Graphics2D g2)
	{	
		System.out.println("drawing ALL features for map " + name);
		
		g2.setColor(Color.WHITE);
		
		float mapEnd = chromoMap.getStop();
		float scalingFactor = height / mapEnd;
		
		for (int i = 0; i < allFeaturePositions.length; i++)
		{
			float yPos;
			if (allFeaturePositions[i] == 0.0f)
			{
				yPos = 0.0f;
			}
			else
			{
				yPos = allFeaturePositions[i] * scalingFactor;
			}
			// draw a line for the marker
			g2.drawLine(0, (int) yPos, width-1, (int) yPos);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers and labels
	private void drawLinkedFeatures(Graphics2D g2)
	{
		System.out.println("drawing LINKED features for map " + name);
		
		g2.setColor(Color.GREEN);
		
		float mapEnd = chromoMap.getStop();
		float scalingFactor = height / mapEnd;

		for (int i = 0; i < linkedFeaturePositions.length; i++)
		{
			float yPos;
			if (linkedFeaturePositions[i] == 0.0f)
			{
				yPos = 0.0f;
			}
			else
			{
				yPos = linkedFeaturePositions[i] * scalingFactor;
			}
			// draw a line for the marker
			g2.drawLine(0, (int) yPos, width-1, (int) yPos);

		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
