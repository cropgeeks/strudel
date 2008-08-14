package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.text.*;
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
	public TreeMap<Integer, LinkedList<Feature>> allFeaturesPosLookup = new TreeMap<Integer, LinkedList<Feature>>();
	
	// these are corresponding arrays and lists which only pertain to the features which are linked to from somewhere
	public LinkedList<Feature> linkedFeatureList = new LinkedList<Feature>();
	String[] linkedFeatureNames;
	float[] linkedFeaturePositions;
	public TreeMap<Float, Feature> linkedFeaturePosLookup = new TreeMap<Float, Feature>();
	
	// indicates whether this map or part thereof is currently drawn on the canvas
	public boolean isShowingOnCanvas = true;
	
	// a vector containing features whose lables are to be displayed when the chromosome is drawn
	public LinkedList<Feature> highlightedFeatures;
	
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
		g2.fillRect(width / 2, 0, width / 2, height);
		
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
		
		// now draw features and labels as required
		if (owningSet.paintAllMarkers && isShowingOnCanvas)
		{
			drawAllFeatures(g2);
			drawHighlightedFeatureLabels(g2);
		}
		
		// this draws a yellow outline round the map if it is selected
		if (drawHighlightOutline)
		{
			highlightMapOutline(g2);
		}
		
//		if (owningSet.zoomFactor > 1)
//			drawDistanceMarkers(g2);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	private void drawDistanceMarkers(Graphics2D g2)
	{
		float numMarkers = 100;
		float interval = owningSet.chromoHeight / numMarkers;
		float currentY = 0;
		
		// set the colour to grey
		g2.setColor(new Color(130, 130, 130));
		Font font = new Font("Arial", Font.PLAIN, 10);
		g2.setFont(font);
		
		for (int i = 0; i <= numMarkers; i++)
		{
			g2.drawLine(-2, (int) currentY, -width / 4, (int) currentY);
			g2.drawString(String.valueOf(i), -25, currentY + 5);
			currentY += interval;
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
		g2.fillRect(width / 2, 0, width / 2, height);
		
		// draw the index of the map in the genome
		int smallFontSize = 9;
		Font overviewLabelFont = new Font("Arial", Font.BOLD, smallFontSize);
		g2.setFont(overviewLabelFont);
		g2.setColor(new Color(150, 150, 150));
		g2.drawString(String.valueOf(index + 1), width * 2, height / 2);
		
		if (drawHighlightOutline)
		{
			g2.setColor(Color.YELLOW);
			g2.drawRect(0, 0, width - 1, height);
		}
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draws labels next to features
	private void drawHighlightedFeatureLabels(Graphics2D g2)
	{
		if (highlightedFeatures != null)
		{
			// the amount by which we want to move the label away from the chromosome (in pixels)
			int labelSpacer = 20;
			
			// for all features in our list
			for (Feature f : highlightedFeatures)
			{
				// get the name of the feature
				String featureName = f.getName();
				
				// font stuff
				int fontHeight = 14;
				g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
				FontMetrics fm = g2.getFontMetrics();
				int stringWidth = fm.stringWidth(featureName);
				
				// we need these for working out the y positions
				float mapEnd = chromoMap.getStop();
				// this factor normalises the position to a value between 0 and 100
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
				
				// now work out the y position of the feature label
				// size and half size of our feature list
				int listSize = highlightedFeatures.size();
				float halfListSize = listSize / 2.0f;
				// this is where the label goes
				int labelY = 0;
				// the index of the feature in the list
				int index = highlightedFeatures.indexOf(f);
				
				float correction = 0;
				// if the list contains only a single feature
				if (listSize == 1)
				{
					correction = fontHeight / 2;
				}
				else
				// more than 1 feature in the list
				{
					// work out whether the list size is an even or odd number
					boolean evenNumber = listSize % 2 == 0;
					if (evenNumber)
					{
						// if the index is smaller than half the list size, subtract a multiple of the fontHeight from the y
						if ((index + 1) <= halfListSize)
						{
							correction = -(fontHeight * (halfListSize - index - 1));
						}
						// if it is bigger, add it instead
						else
						{
							correction = fontHeight * ((index + 1) - halfListSize);
						}
					}
					else
					// odd number
					{
						// this should give us the number half way between the first and last index
						int midPoint = (int) halfListSize;
						
						// if the index is the midpoint
						if (index == midPoint)
						{
							correction = fontHeight / 2;
						}
						// index is less than the midpoint -- subtract a multiple of the font height
						else if (index < midPoint)
						{
							correction = -(fontHeight * (halfListSize - index - 1));
						}
						// index is greater than the midpoint
						else
						{
							correction = fontHeight * ((index + 1) - halfListSize);
						}
					}
				}
				
				labelY = featureY + (int) correction;
				
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
			
			// check whether we have an entry in the map with this key already
			// we do often have features mapped to the same location, especially on genetic maps
			// if the map does contain this key
			if (allFeaturesPosLookup.containsKey(percentDistToFeat))
			{
				// add the feature to the linked list that forms the value to this key
				allFeaturesPosLookup.get(percentDistToFeat).add(f);
			}
			else
			// we do not have this key in the map yet
			{
				// make a new list and add it with this float as a key
				LinkedList<Feature> list = new LinkedList<Feature>();
				list.add(f);
				allFeaturesPosLookup.put(new Integer(percentDistToFeat), list);
			}
		}
		
		// System.out.println("==========================================");
		// System.out.println("calculating relative feature starts for map " + name);
		// System.out.println("size of this map = " + allFeaturesPosLookup.keySet().size());
		// int count = 0;
		// for (Integer it : allFeaturesPosLookup.keySet())
		// {
		// LinkedList list = allFeaturesPosLookup.get(it);
		// // now round this number to two decimals so we can compare it reliably to input values
		// System.out.println(it + " = " + allFeaturesPosLookup.get(it).toString());
		// count += list.size();
		// }
		// System.out.println("count = " + count);
		
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
			float percentDistToFeat = f.getStart() * (100 / chromoMap.getStop());
			// now round this number to two decimals so we can compare it reliably to input values
			percentDistToFeat = Float.parseFloat(new DecimalFormat("0.##").format(percentDistToFeat));
			// System.out.println("percentDistToFeat = " + percentDistToFeat);
			
			linkedFeaturePosLookup.put(percentDistToFeat, f);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers and labels
	private void drawAllFeatures(Graphics2D g2)
	{
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
			g2.drawLine(0, (int) yPos, width - 1, (int) yPos);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
