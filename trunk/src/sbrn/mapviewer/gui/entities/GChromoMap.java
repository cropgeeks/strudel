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
	ChromoMap chromoMap;
	
	// arrays with Feature names and positions for fast access during drawing operations
	String[] allFeatureNames;
	float[] allFeaturePositions;
	
	// these are corresponding arrays and lists which only pertain to the features which are linked to from somewhere
	public LinkedList<Feature> linkedFeatureList = new LinkedList<Feature>();
	String[] linkedFeatureNames;
	float[] linkedFeaturePositions;
	public TreeMap<Integer, String> linkedFeaturePosLookup = new TreeMap<Integer, String>();
	
	// indicates whether this map or part thereof is currently drawn on the canvas
	public boolean isShowingOnCanvas = true;
	
	public TreeMap<Integer, String> highlightedFeatures;
	
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
		GradientPaint gradient = new GradientPaint(0, 0, colour, width/2, 0, offWhite);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width/2, height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width/2, 0, offWhite, width/2 * 2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width/2, 0, width/2 + 1, height);
		
		// draw the index of the map in the genome
		int fontSize = WinMain.mainCanvas.getHeight() / 40;
		Font mapLabelFont = new Font("Arial", Font.BOLD, fontSize);
		g2.setFont(mapLabelFont);
		g2.setColor(Color.WHITE);
		
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
		
		if (owningSet.paintMarkers && isShowingOnCanvas)
		{
			drawLinkedFeatures(g2);
			drawHighlightedFeatureLabels(g2);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	private void drawHighlightedFeatureLabels(Graphics2D g2)
	{
		if (highlightedFeatures != null)
		{
			
			// now draw the selected features
			int count = 0;
			int labelSpacer = 4;
			for (Integer i : highlightedFeatures.keySet())
			{
				// first convert the percent distance from the top of the chromosome into an actual y coordinate
				int y = (int) (height * (i / 100.0));
				
				// decide where to place the label on x
				// on the left hand genome we want the label on the left, right hand genome on the right
				int labelX = 0;
				if (!owningSet.isTargetGenome)
				{
					labelX = width + labelSpacer;
				}
				else
				{
					FontMetrics fm = g2.getFontMetrics();
					int width = fm.stringWidth(highlightedFeatures.get(i));
					labelX = -width - labelSpacer;
				}
				
				// draw the label
				g2.drawString(highlightedFeatures.get(i), labelX, y);
				
				// draw a line from the marker to the label
				// g2.drawLine(0, y, labelX, y);
				
				count++;
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers and labels
	private void drawLinkedFeatures(Graphics2D g2)
	{
		// set font to smaller font size
		g2.setFont(new Font("Arial", Font.PLAIN, 11));
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
			g2.drawLine(0, (int) yPos, width, (int) yPos);			
		}
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
			linkedFeaturePosLookup.put(percentDistToFeat, f.getName());
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
