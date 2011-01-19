package sbrn.mapviewer.gui.entities;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;

public class GChromoMap implements Comparable<GChromoMap>
{

	// ============================vars==================================

	// size stuff
	public int height;
	public int width;

	// position stuff
	//these are relative to the screen bounds at any one time
	public int x;
	public int y;

	public Color colour;
	public String name;

	// the index of the chromosome in the genome
	// starts at 0
	public int index;

	// the owning map set
	public GMapSet owningSet;

	// this is a bounding rectangle which contains the chromosome and which serves the purpose of being able to detect
	// mouse events such as the user clicking on the chromosome to select it or zoom it
	public Rectangle boundingRectangle = new Rectangle();

	// the corresponding ChromoMap object -- this holds the actual data
	public ChromoMap chromoMap;

	// arrays with Feature names and positions for fast access during drawing operations
	public int[] allLinkedFeaturePositions;
	public Feature [] allLinkedFeatures;
	//
	// indicates whether this whole map or part thereof is currently drawn on the canvas
	public boolean isShowingOnCanvas = true;

	// indicates whether this whole map is currently drawn on the canvas
	public boolean isFullyShowingOnCanvas = true;

	// a vector containing features whose labels are to be displayed when the chromosome is drawn
	public Vector<Feature> mouseOverFeatures = new Vector<Feature>();

	//a boolean indicating whether we want to draw the highlighted features or not
	public boolean drawMouseOverFeatures = false;

	// do we have to highlight this map (currently done by changing its colour)
	public boolean highlight = false;

	//this gets set to true when we have selected a set of features for which we want annotation info to
	//be displayed until we deselect it
	public boolean persistHighlightedFeatures = false;

	//boolean to show whether the arrays that hold features positions and names have been inited
	public boolean arraysInitialized = false;

	//the colour in the centre of the chromosome
	Color centreColour;

	//this is the angle at which we draw this map, measured from vertical and going clockwise
	public float angleFromVertical = 90;
	public float undersideBrightness;

	//true if this chromosome is shown fully inverted
	public boolean isFullyInverted = false;
	//true if this chromosome is shown partly inverted
	public boolean isPartlyInverted = false;
	//true while the inversion of the chromosome is in progress
	public boolean inversionInProgress = false;

	//true if we want to draw the number of the chromosome
	public boolean drawChromoIndex = true;

	//the top left corner y position of the chromosome during an inversion event
	public int currentY = 0;
	//a factor used to calculate the currentY value from the angle during the inversion
	public float multiplier = 0;

	//these vars allow us to colour in a region on the chromosome for highlighting
	public boolean highlightChromomapRegion = false;
	public float highlightedRegionStart, highlightedRegionEnd;

	//the zoom factor above which we want to display distance markers on the chromosomes
	float distanceMarkerZoomThreshold;

	// if true, paint a rectangle to indicate the fact that we are panning over a region we want to select for zooming in to
	public boolean drawSelectionRect = false;
	//these are the relevant coordinates for this
	public float selectionRectTopY, selectionRectBottomY, chromoHeightOnSelection;
	//and the relative coordinates that correspond to the positions on the screen
	public float relativeTopY;
	public float relativeBottomY;

	public boolean linkedFeaturesReversed = false;

	//a boolean to indicate whether we should always display labels, regardless of zoom factor
	public boolean alwaysShowAllLabels = false;
//
//	//this is the y coord for the top left corner of this map on the canvas (not screen!)
//	//may be off the visible screen
//	public int yOnCanvas;


	// ============================curve'tors==================================

	public GChromoMap(String name, int index, GMapSet owningSet)
	{
		this.name = name;
		this.index = index;
		this.owningSet = owningSet;
		this.chromoMap = owningSet.mapSet.getMaps().get(index);

		//for convenience also set this object on the ChromoMap object so we can do lookups in either direction
		chromoMap.addGChromoMap(this);
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
			if(isPartlyInverted || isFullyInverted)
			{
				colour = Colors.invertedChromosomeColour;
				if(highlight)
					colour = Colors.invertedChromosomeHighlightColour;
			}
			else if(highlight)
				colour = Colors.chromosomeHighlightColour;
			else
				colour = Colors.genomeColour;

			//this is the colour of the centre of the chromo -- needs to be brighter so we can get the 3d effect
			centreColour = colour.brighter().brighter().brighter().brighter();

			distanceMarkerZoomThreshold = owningSet.thresholdAllMarkerPainting;

			//adjust the y in case we are inverting
			multiplier = Math.abs(((Math.abs(angleFromVertical / 90.0f)) -1.0f) *0.5f);
			currentY = Math.round(multiplier * owningSet.chromoHeight);

			//adjust the  height according to the angle if necessary
			height = (int)(owningSet.chromoHeight * Math.abs(angleFromVertical / 90.0f));

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
			GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, centreColour, true);
			g2.setPaint(gradient);
			g2.fillRect(0, currentY, width, rectHeight);

			//if we have a selected region, colour it in with a different colour
			if(highlightChromomapRegion)
			{
				highlightChromomapRegion(g2);
			}


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
				drawLinkedFeatures(g2);
			}

			//draw the selection rectangle if required
			if (drawSelectionRect)
			{
				drawSelectionRectangle(g2);
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
		//font stuff
		int fontHeight = 9;
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		if (owningSet.zoomFactor >=  distanceMarkerZoomThreshold && !inversionInProgress)
		{
			//the number of markers we want to draw at any one time, regardless of our zoom level
			float numMarkers = Constants.numDistanceMarkers;

			// this is the number of pixels by which the markers get spaced
			float interval = owningSet.chromoHeight / numMarkers;
			float currentY = y;
			if(isFullyInverted)
				currentY = y + height;

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

			// decide where to place the label on x
			// on the leftmost genome we want the label on the left, rightmost genome on the right
			boolean labelOnRight = false;
			int genomeIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(owningSet);

			//we want the label on the right if the owning genome is  the last genome on the right
			if(genomeIndex == (Strudel.winMain.dataContainer.gMapSets.size()-1))
			{
				labelOnRight = true;
			}

			//x coords
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
				//the label we want to draw
				String label = String.valueOf(nf.format(currentVal));
				if(isFullyInverted)
					label = String.valueOf(nf.format(Math.abs(currentVal)));

				int stringWidth = fm.stringWidth(label);

				//this is what we do if the label needs to be on the right
				if(labelOnRight)
				{
					lineStartX =  x + width;
					lineEndX =  lineStartX + lineLength;
					labelX = lineEndX + gap;
				}
				else//label on left
				{
					labelX = x - lineLength - gap - stringWidth;
					lineStartX =  x-1;
					lineEndX =  x- lineLength;
				}

				//y coord
				int labelY = Math.round(currentY) + fontHeight / 2;

				//fill a continuous rectangle next to the chromosome as a background, with the height of the chromosome and the width of the largest label
				int horizontalGap = 3;
				int verticalGap = 2;
				int arcSize = Math.round(fontHeight/1.5f);
				g2.setColor(Colors.distanceMarkerBackgroundColour);
				g2.fillRoundRect(labelX - horizontalGap, labelY - fontHeight, stringWidth + horizontalGap*2, fontHeight + verticalGap, arcSize, arcSize);

				// draw a line from the marker to the label
				g2.setColor(Colors.distanceMarkerColour);
				g2.drawLine(lineStartX, Math.round(currentY), lineEndX, Math.round(currentY));
				//draw the label
				g2.drawString(label, labelX, labelY);

				// increment/decrement
				if(isFullyInverted)
				{
					currentY -= interval;
					currentVal -= increment;
				}
				else
				{
					currentY += interval;
					currentVal += increment;
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------

	// draw the chromosome/map for the purpose of an overview only
	public void paintOverViewMap(Graphics2D g2, int width, int height)
	{
		// draw first half of chromosome
		GradientPaint gradient = new GradientPaint(0, 0, colour, width / 2, 0, centreColour, true);
		g2.setPaint(gradient);
		g2.fillRect(0, 0, width, height);

		// draw the index of the map in the genome
		int smallFontSize = 9;
		Font overviewLabelFont = new Font("Arial", Font.BOLD, smallFontSize);
		g2.setFont(overviewLabelFont);
		g2.setColor(Colors.chromosomeIndexColour);
		g2.drawString(name, width * 2, Math.round(height/2.0f) + Math.round(smallFontSize/2.0f));
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------

	// draws labels next to features
	public void drawMouseOverFeatures(Graphics2D g2)
	{
		if (mouseOverFeatures.size() > 0 && drawMouseOverFeatures)
		{
			LabelDisplayManager.drawFeatureLabelsInRange(this, g2, mouseOverFeatures, true, null);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------

	// initialises the arrays we need for fast drawing
	public void initArrays()
	{

		// init the arrays that hold ALL the features for this map
		int numFeatures = chromoMap.countFeatures();

		allLinkedFeatures = new Feature[numFeatures];
		allLinkedFeaturePositions = new int[numFeatures];
		Vector<Feature> featureList = chromoMap.getFeatureList();
		for (int i = 0, n=featureList.size(); i < n; i++)
		{
			Feature f = featureList.get(i);

			//at this point we need to know whether this feature is involved in any links
			//if it is, we add it to the arrays
			//otherwise it's fine to just have it in the feature list of the corresponding chromomap from where we can access it for
			//other uses such as full feature lists for search ranges etc
			if((f.getLinks() != null && f.getLinks().size() > 0) || Strudel.winMain.dataContainer.gMapSets.size() == 1)
			{
				//the start point of this features in its own units (cM, bp, whatever)
				float start = f.getStart();

				//scale this by the current map height to give us a position in pixels, between zero and the chromosome height
				//then store this value in the array we use for drawing
				allLinkedFeaturePositions[i] =Utils.relativeFPosToPixelsOnGMap(this, start);

				//if the map is inverted we need to store the inverse of this value i.e. the map end value minus the feature position
				if(isFullyInverted || isPartlyInverted)
				{
					allLinkedFeaturePositions[i] = (int) ((owningSet.chromoHeight / chromoMap.getStop()) * (chromoMap.getStop() -start));
				}

				//also store a reference to the feature itself in a parallel array
				allLinkedFeatures[i] = f;
			}
		}
		arraysInitialized = true;
	}


	// -----------------------------------------------------------------------------------------------------------------------------------------

	// draw the markers for the features
	private void drawLinkedFeatures(Graphics2D g2)
	{

		int lastY = -1;
		int numMarkersDrawn = 0;

		if (allLinkedFeaturePositions != null)
		{
			g2.setColor(Colors.featureColour);
			for (int i = 0; i < allLinkedFeaturePositions.length; i++)
			{
				int yPos;
				if (allLinkedFeaturePositions[i] == 0.0f)
				{
					yPos = 0;
				}
				else
				{
					yPos = allLinkedFeaturePositions[i];
				}

				//check whether inversion in progress
				if(inversionInProgress)
					yPos = Math.round((yPos * (height / (float)owningSet.chromoHeight)) + currentY);

				//check whether this position is currently showing on the canvas or not
				boolean featureIsVisible = false;
				int upperViewPortBoundary = owningSet.centerPoint - (Strudel.winMain.mainCanvas.getHeight()/2);
				int lowerViewPortBoundary = owningSet.centerPoint + (Strudel.winMain.mainCanvas.getHeight()/2);
				int fPosPixelsOnCanvas = Utils.pixelsOnChromoToPixelsOnCanvas(this, yPos, inversionInProgress);
				if (fPosPixelsOnCanvas > upperViewPortBoundary && fPosPixelsOnCanvas < lowerViewPortBoundary)
				{
					featureIsVisible = true;
				}

				// draw a line for the marker
				if (yPos != lastY && featureIsVisible)
				{
					numMarkersDrawn++;
					g2.drawLine(0, yPos, width, yPos);
					lastY = yPos;
				}
			}
		}

		Strudel.winMain.mainCanvas.numMarkersDrawn += numMarkersDrawn;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------

	//colours a section of the chromosome in a different colour
	public void highlightChromomapRegion(Graphics2D g2)
	{
		Color highlightColour = Utils.getTonedDownColour(colour);
		Color centreHighlightColour = highlightColour.brighter().brighter().brighter().brighter();

		int start = (int) ((owningSet.chromoHeight / chromoMap.getStop()) * highlightedRegionStart);
		int end =  (int) ((owningSet.chromoHeight / chromoMap.getStop()) * highlightedRegionEnd);
		int rectHeight = end - start;

		GradientPaint gradient = new GradientPaint(0, 0, highlightColour, width / 2, 0, centreHighlightColour);
		g2.setPaint(gradient);
		g2.fillRect(0, start, width / 2, rectHeight);
		// draw second half of chromosome
		GradientPaint whiteGradient = new GradientPaint(width / 2, 0, centreHighlightColour, width, 0, highlightColour);
		g2.setPaint(whiteGradient);
		g2.fillRect(width / 2, start, (width / 2)+1, rectHeight);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------

	// this draws and fills a mildly opaque rectangle delimiting a region we want to select features from
	private void drawSelectionRectangle(Graphics2D g2)
	{
		//convert the absolute values of the y coords of the selection rectangle to relative ones (relative to the chromo)
		relativeTopY = (selectionRectTopY / chromoHeightOnSelection) * chromoMap.getStop();
		relativeBottomY = (selectionRectBottomY / chromoHeightOnSelection) * chromoMap.getStop();

		//now we need to convert them back to absolute ones that take into account the currrent height of the chromosome
		//this is so we can zoom and still show the rectangle which then gets resized appropriately
		int start = Math.round((owningSet.chromoHeight / chromoMap.getStop()) * relativeTopY) ;
		int end =  Math.round((owningSet.chromoHeight / chromoMap.getStop()) * relativeBottomY);
		int rectHeight = end - start;

		//width and x -- always the same
		//want a tight frame round the chromo here
		int rectWidth = Math.round(width*1.5f);
		int rectX = Math.round(- width*0.25f);

		//fill the rectangle with slightly opaque paint
		g2.setPaint(Colors.selectionRectFillColour);
		g2.fillRect(rectX, start, rectWidth, rectHeight);

		//then draw an outline around it
		g2.setColor(Colors.selectionRectOutlineColour);
		g2.drawRect(rectX, start, rectWidth, rectHeight);
	}

	@Override
	public int compareTo(GChromoMap gMap)
	{
		int i = 0;

		if(index == gMap.index)
			i = 0;
		else if(index < gMap.index)
			i = -1;
		else if(index > gMap.index)
			i = 1;

		return i;
	}

}// end class










