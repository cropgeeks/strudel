package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;

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
	public float[] allFeaturePositions;
//	public String [] allFeatureNames;
	public Feature [] allFeatures;
	public TreeMap<Integer, Vector<Feature>> allFeaturesPosLookup = new TreeMap<Integer, Vector<Feature>>();
	
	// these are corresponding arrays and lists which only pertain to the features which are linked to from somewhere
	public Vector<Feature> linkedFeatureList = new Vector<Feature>();
	String[] linkedFeatureNames;
	float[] linkedFeaturePositions;
	public TreeMap<Float, Feature> linkedFeaturePosLookup = new TreeMap<Float, Feature>();
	
	// indicates whether this map or part thereof is currently drawn on the canvas
	public boolean isShowingOnCanvas = true;
	
	// a vector containing features whose labels are to be displayed when the chromosome is drawn
	public Vector<Feature> highlightedFeatures = new Vector<Feature>();
	
	//a boolean indicating whether we want to draw the highlighted features or not
	public boolean drawHighlightedFeatures = false;
	
	// do we have to draw a highlighted outline for this map
	public boolean drawHighlightOutline = false;
	
	//this gets set to true when we have selected a set of features for which we want annotation info to
	//be displayed until we deselect it
	public boolean persistHighlightedFeatures = false;
	
	public boolean arraysInitialized = false;
	
	//the colour in the centre of the chromosome
	Color centreColour;
	
	//this is the angle at which we draw this map, measured from vertical and going clockwise
	public float angleFromVertical = 90;
	public float undersideBrightness;
	
	//true if this chromosome is shown fully inverted
	public boolean isFullyInverted = false;
	//true if this chromosome is shown fully inverted
	public boolean isPartlyInverted = false;
	
	public boolean inversionInProgress = false;
	
	public int currentY = 0;
	public float multiplier = 0;

	
	
	// ============================c'tors==================================
	
	public GChromoMap(Color colour, String name, int index, GMapSet owningSet)
	{
		this.colour = colour;
		this.name = name;
		this.index = index;
		this.owningSet = owningSet;
		this.chromoMap = (ChromoMap) owningSet.mapSet.getMaps().get(index);
		centreColour = owningSet.colour.brighter().brighter().brighter().brighter();
		
		//for convenience also set this object on the ChromoMap object so we can do lookups in either direction
		chromoMap.setGChromoMap(this);
	}
	
	// ============================methods==================================
	/**
	 * Draws the map from coordinate 0,0 given the current position of the Graphics object
	 */
	public void paintMap(Graphics g)
	{
		try
		{
			Graphics2D g2 = (Graphics2D) g;		
			
			//determine the fill colour first
			if(isPartlyInverted)
			{
				colour = Colors.chromosomeInversionColour;
				centreColour = Colors.chromosomeInversionColour.brighter().brighter().brighter().brighter();
			}
			else
			{
				colour = owningSet.colour;
				centreColour = owningSet.colour.brighter().brighter().brighter().brighter();
			}
			
			//adjust the y in case we are inverting
			multiplier = Math.abs(((Math.abs(angleFromVertical / 90.0f)) -1.0f) *0.5f);
			currentY = Math.round(multiplier * owningSet.chromoHeight);
			
			//adjust the  height according to the angle if necessary
			height = (int)(owningSet.chromoHeight * Math.abs(angleFromVertical / 90.0f));
			
			//don't let the height fall below the value of the width
			//this is the diameter of the chromosome and this is the minimum size it would ever appear at
			//even if we are looking at it from its bottom end
			if(height < width)
				height = width;
			
			//adjust the colours according to the angle to create a pseudo-3d effect when inverting			
			//first get the main colour and extract its hsb values
			float [] hsb = colour.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
			//reduce the brightness value if necessary
			float currentBrightness = hsb[2];
			//scale by angle
			float newBrightness = Math.abs(angleFromVertical / 90.0f) * currentBrightness;
			//don't let this fall below a threshold
			if(newBrightness < 0.3f)
				newBrightness = 0.3f;
			//set this as the new colour
			colour = Color.getHSBColor(hsb[0], hsb[1], newBrightness);
			
			//next get the highlight center colour and extract its hsb values
			float [] hsbCentreColour = centreColour.RGBtoHSB(centreColour.getRed(), centreColour.getGreen(), centreColour.getBlue(), null);
			//reduce the brightness value if necessary
			float currentCenterBrightness = hsbCentreColour[2];
			//scale by angle
			float newCenterBrightness = Math.abs(angleFromVertical / 90.0f) * currentCenterBrightness;
			//don't let this fall below a threshold
			if(newCenterBrightness < 0.3f)
				newCenterBrightness = 0.3f;
			//set this as the new centreColour
			centreColour = Color.getHSBColor(hsbCentreColour[0], hsbCentreColour[1], newCenterBrightness);
			
			//draw the bounding rectangle in the colour of the chromosome
			g2.setColor(colour);
			
			//need another check here to make sure we are not drawing any rectangle parts below the ellipse
			//this would stick out and destroy the illusion of a 3D inversion process
			//very dirty hack but quickest solution and it works
			int rectHeight = height;
			//the length of the top half of the rectangle above a line representing the centerpoint of the chromosome
			int topHalfLengthOfRect = (owningSet.chromoHeight/2) - currentY;
			//same for the bottom half
			int bottomHalfLengthOfRect = (currentY + height) - (owningSet.chromoHeight/2);
			//if there is more chromo below the center line than there should be, just set the value of the bottom half to be 
			//the same as the top half
			if(bottomHalfLengthOfRect > topHalfLengthOfRect)
				rectHeight = topHalfLengthOfRect * 2;
			
			//now draw the rectangle 
			Rectangle rect = new Rectangle(0, currentY, width, rectHeight);
			g2.draw(rect);
			//fill the rectangle with two different gradient fills
			// draw first half of chromosome		
			GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, centreColour);
			g2.setPaint(gradient);
			g2.fillRect(0, currentY, width / 2, rectHeight);
			// draw second half of chromosome
			GradientPaint whiteGradient = new GradientPaint(width / 2, 0, centreColour, width, 0, colour);
			g2.setPaint(whiteGradient);
			g2.fillRect(width / 2, currentY, width / 2, rectHeight);
			
			//if the chromosome is being inverted
			if(angleFromVertical != 90 && angleFromVertical != -90)
			{
				//now draw two ellipses
				//these represent the underside and the top of the cylinder which may or may not visible depending on the angle and
				//which also changes shape with the angle
				//the constant in the following equations (0.0055f) is derived from a linear function that describes the movement of
				//the ellipse up and down the chromosome as a function of the angle
				float ellipseHeight = (float) (Math.cos(Math.toRadians(angleFromVertical)) * width);
				float bottomEllipseY = (((-0.0055f * angleFromVertical) + 0.5f) * owningSet.chromoHeight) - ellipseHeight/2.0f;	
				float topEllipseY = (((0.0055f * angleFromVertical) + 0.5f) * owningSet.chromoHeight) - ellipseHeight/2.0f;
				
				//draw an ellipse representing the top of the chromosome
				//needs to be drawn in two halves so we can add the gradient to create the illusion of a highlight			
				Arc2D a1 = new Arc2D.Float(0,topEllipseY,width,ellipseHeight,90,180,Arc2D.PIE);
				int gradientStart = 0;
				int gradientEnd = width/2;
				GradientPaint gp = new GradientPaint(gradientStart,width/2, colour, gradientEnd,width/2, centreColour, false);
				// Fill with a gradient.
				g2.setPaint(gp);
				g2.fill(a1);
				//second half
				Arc2D a2 = new Arc2D.Float(0,topEllipseY,width,ellipseHeight,270,180,Arc2D.PIE);
				gradientStart = width/2;
				gradientEnd = width;
				gp = new GradientPaint(gradientStart,width/2, centreColour, gradientEnd,width/2, colour, false);
				// Fill with a gradient.
				g2.setPaint(gp);
				g2.fill(a2);
				
				//draw an ellipse representing the underside of the chromosome
				Ellipse2D bottomEllipse2D = new Ellipse2D.Float(0, bottomEllipseY, width, ellipseHeight);
				undersideBrightness = (1.0f/(Math.abs(angleFromVertical / 90.0f))) * 0.1f;
				//make sure this is not any brighter than the centreColour brightness
				if(undersideBrightness > hsbCentreColour[2])
					undersideBrightness = hsbCentreColour[2];
				g2.setColor(Color.getHSBColor(hsbCentreColour[0], hsbCentreColour[1], undersideBrightness));
				g2.fill(bottomEllipse2D);
			}
			
			// now draw features and labels as required
			if (owningSet.paintAllMarkers && isShowingOnCanvas)
			{
				drawAllFeatures(g2);
			}
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draws a set of distance markers
	public void drawDistanceMarkers(Graphics2D g2)
	{
		if (owningSet.zoomFactor > 25 && !inversionInProgress)
		{
			//the number of markers we want to draw at any one time, regardless of our zoom level
			float numMarkers = 50;
			
			// this is the number of pixels by which the markers get spaced
			float interval = owningSet.chromoHeight / numMarkers;
			float currentY = y;
			
			// this is the numerical amount by which we want to separate the marker values
			// this gets scaled by the maximum value at the chromosome end and can be in
			// centiMorgan or in base pairs
			float increment = chromoMap.getStop() / numMarkers;
			
			// the current marker value we want to print
			float currentVal = 0;
			
			// need to format the number appropriately
			NumberFormat nf = NumberFormat.getInstance();
			
			// check first whether we are dealing with ints or floating point numbers for the chromosome distances
			if (chromoMap.getStop() % 1 == 0) // this is an int
			{
				nf.setMaximumFractionDigits(0);
			}
			else// it's a float
			{
				// we want two decimals here
				nf.setMaximumFractionDigits(2);
				nf.setMinimumFractionDigits(2);
			}
			
			// set the colour to white
			g2.setColor(Colors.distanceMarkerColour);
			
			// font stuff
			int fontHeight = 9;
			g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
			
			// decide where to place the label on x
			// on the left hand genome we want the label on the left, right hand genome on the right
			int labelX = 0; // this is where the label is drawn from
			int lineStartX = 0; // this is where the line to the label is drawn from
			int lineEndX = 0; // the label connects to the line here
			
			// the amount by which we want to move the label away from the chromosome (in pixels)
			int lineLength = 8;
			// the amount we want to separate the label and the line by, in pixels
			int gap = 5;
			
			//draw
			for (int i = 0; i <= numMarkers; i++)
			{
				//x coords
				labelX = x+ width + lineLength + gap;
				lineStartX =  x+ width;
				lineEndX =  x+ width + lineLength;
				
				// draw a line from the marker to the label
				g2.drawLine(lineStartX, Math.round(currentY), lineEndX, Math.round(currentY));
				//draw the label
				g2.drawString(String.valueOf(nf.format(currentVal)), labelX,
								Math.round(currentY) + fontHeight / 2);
				
				// increment
				currentY += interval;
				currentVal += increment;
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the chromosome/map for the purpose of an overview only
	public void paintOverViewMap(Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		// this colour is the lightest in the double gradient which produces the ambient light effect
		// i.e. this is the colour one will see in the centre of the chromosome (= the topmost part of the simulated cylinder)
		
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, centreColour);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width / 2, height);
		
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width / 2, 0, centreColour, width / 2 * 2, 0, colour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width / 2, 0, width / 2, height);
		
		// draw the index of the map in the genome
		int smallFontSize = 9;
		Font overviewLabelFont = new Font("Arial", Font.BOLD, smallFontSize);
		g2.setFont(overviewLabelFont);
		g2.setColor(Colors.chromosomeIndexColour);
		g2.drawString(String.valueOf(index + 1), width * 2, height / 2);
		
		if (drawHighlightOutline)
		{
			g2.setColor(Colors.outlineColour);
			g2.drawRect(0, 0, width - 1, height);
		}
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draws labels next to features
	public void drawHighlightedFeatures(Graphics2D g2)
	{
		// the usual font stuff
		int fontHeight = 12;
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();
		
		if (highlightedFeatures.size() > 0 && 	drawHighlightedFeatures)
		{
			// for all features in our list
			for (Feature f : highlightedFeatures)
			{
				// get the name of the feature
				String featureName = f.getName();
				
				int stringWidth = fm.stringWidth(featureName);
				
				// we need these for working out the y positions
				float mapEnd = chromoMap.getStop();
				// this factor normalises the position to a value between 0 and 100
				float scalingFactor = height / mapEnd;
				
				// the y position of the feature itself
				int featureY = Math.round(y + (f.getStart() * scalingFactor));
				
				//if the map is inverted we need to use the inverse of this value i.e. the map end value minus the feature position
				if(inversionInProgress || isFullyInverted)
				{
					featureY = Math.round(y + ((mapEnd - f.getStart()) * scalingFactor));
				}
				
				// now work out the y position of the feature label
				// size and half size of our feature list
				int listSize = highlightedFeatures.size();
				float halfListSize = listSize / 2.0f;
				// this is where the label goes
				int labelY = 0;
				// the index of the feature in the list
				int index = highlightedFeatures.indexOf(f);
				
				// the offset is the amount (in px) by which we need to move the label up or down relative to the feature itself
				float offset = 0;
				// if the list contains only a single feature
				if (listSize == 1)
				{
					offset = fontHeight / 2;
				}
				// more than 1 feature in the list
				else
				{
					// work out whether the list size is an even or odd number
					boolean evenNumber = listSize % 2 == 0;
					if (evenNumber)
					{
						// if the index is smaller than half the list size, subtract a multiple of the fontHeight from the y
						if ((index + 1) <= halfListSize)
						{
							offset = -(fontHeight * (halfListSize - index - 1));
						}
						// if it is bigger, add it instead
						else
						{
							offset = fontHeight * ((index + 1) - halfListSize);
						}
					}
					// odd number
					else
					{
						// this should give us the number half way between the first and last index
						int midPoint = (int) halfListSize;
						
						// if the index is the midpoint
						if (index == midPoint)
						{
							offset = fontHeight / 2;
						}
						// index is less than the midpoint
						else if (index < midPoint)
						{
							offset = -(fontHeight * (halfListSize - index - 1));
						}
						// index is greater than the midpoint
						else
						{
							offset = fontHeight * ((index + 1) - halfListSize);
						}
					}
				}
				
				// now set the y position of the label
				labelY = featureY + (int) offset;
				
				// next decide where to place the label on x			
				int labelSpacer = 30; // the amount by which we want to move the label away from the chromosome (in pixels)
				int labelX = x - stringWidth - labelSpacer; // this is where the label is drawn from
				int lineStartX = x; // this is where the line to the label is drawn from
				int labelGap = 3; // the gap between the label and the line
				int lineEndX = lineStartX - labelSpacer + labelGap; // the label connects to the line here
				
				// set the colour to white
				g2.setColor(Colors.featureLabelColour);
				
				// draw the label
				g2.drawString(featureName, labelX, labelY);
				
				// draw a line from the marker to the label
				g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);
				
				// draw a line for the marker on the chromosome itself
				//first set the colour accordingly
				g2.setColor(Colors.highlightedFeatureColour);
				g2.drawLine(lineStartX, featureY, lineStartX + width - 1, featureY);
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// draw a coloured outline using the bounding rectangle of this map
	public void drawHighlightOutline(Graphics2D g2)
	{
		if (drawHighlightOutline && !inversionInProgress)
		{
			g2.setColor(Colors.outlineColour);
			g2.draw(boundingRectangle);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
	// initialises the arrays we need for fast drawing
	public void initArrays()
	{
		if(owningSet.paintAllMarkers && isShowingOnCanvas)
		{
			// init the arrays that hold ALL the features for this map
			int numFeatures = chromoMap.countFeatures();
			allFeatures = new Feature[numFeatures];
			allFeaturePositions = new float[numFeatures];
			Vector<Feature> featureList = chromoMap.getFeatureList();
			for (int i = 0; i < featureList.size(); i++)
			{
				Feature f = featureList.get(i);
				//the start point of this features in its own units (cM, bp, whatever)
				float start = f.getStart();
				//scale this by the current map height to give us a position in pixels, between zero and the chromosome height
				//then store this value in the array we use for drawing
				allFeaturePositions[i] = (int) ((owningSet.chromoHeight / chromoMap.getStop()) * start);
				//if the map is inverted we need to store the inverse of this value i.e. the map end value minus the feature position
				if(isFullyInverted || isPartlyInverted)
				{
					allFeaturePositions[i] = (int) ((owningSet.chromoHeight / chromoMap.getStop()) * (chromoMap.getStop() -start));
				}
				//also store the feature itself in a parallel array
				allFeatures[i] = f;
			}
			arraysInitialized = true;
		}
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------------------------------
	
	// draw the markers for the features
	private void drawAllFeatures(Graphics2D g2)
	{
		if (allFeaturePositions != null)
		{
			g2.setColor(Colors.featureColour);
			for (int i = 0; i < allFeaturePositions.length; i++)
			{
				float yPos;
				if (allFeaturePositions[i] == 0.0f)
				{
					yPos = 0.0f;
				}
				else
				{
					yPos = allFeaturePositions[i];
				}
				
				//check whether inversion in progress
				if(inversionInProgress)
					yPos = (yPos * (height / (float)owningSet.chromoHeight)) + currentY;

				// draw a line for the marker
				g2.drawLine(0, (int) yPos, width - 1, (int) yPos);
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
