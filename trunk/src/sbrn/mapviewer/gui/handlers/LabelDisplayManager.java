package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;


public class LabelDisplayManager
{

	private static int fontHeight = 11;


	//------------------------------------------------------------------------------------------------------------------------------------

	// draws labels next to found features
	public static void drawHighlightedFeatureLabels(Graphics2D g2)
	{
		// the usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		//for the purpose of this we can combine the elements in the foundFeatures and foundFeatureHomologs vectors
		int size = MapViewer.winMain.fatController.foundFeatures.size()
			+ MapViewer.winMain.fatController.foundFeatureHomologs.size();
		Vector<Feature> featuresAndHomologs = new Vector<Feature>(size);
		featuresAndHomologs.addAll(MapViewer.winMain.fatController.foundFeatures);
		featuresAndHomologs.addAll(MapViewer.winMain.fatController.foundFeatureHomologs);

		// for all features in our list
		for (Feature f : featuresAndHomologs)
		{
			// get the name of the feature
			String featureName = f.getName();
			int stringWidth = fm.stringWidth(featureName);

			// we need these for working out the y positions
			ChromoMap chromoMap = f.getOwningMap();
			GChromoMap gChromoMap = chromoMap.getGChromoMap();
			float mapEnd = chromoMap.getStop();
			// this factor normalises the position to a value between 0 and 100
			float scalingFactor = gChromoMap.height / mapEnd;

//			// the y position of the feature itself
			int featureY;
			if (f.getStart() == 0.0f)
			{
				featureY = gChromoMap.y;
			}
			else
			{
				featureY = Math.round(gChromoMap.y + gChromoMap.currentY + (f.getStart() * scalingFactor));
			}		
			//check whether the map is inverted			
			if(gChromoMap.isPartlyInverted)
			{
				featureY = (int) ((mapEnd - f.getStart()) / (mapEnd / gChromoMap.height)) + (gChromoMap.y + gChromoMap.currentY);
			}

			//the y position of the feature label
			int labelY = featureY + (fontHeight/2);

			// next decide where to place the label on x
			// the amount by which we want to move the label end away from the chromosome (in pixels)
			int lineLength = 40;
			int gap = 3;
//			int labelX = gChromoMap.x + gChromoMap.width + lineLength; // this is where the label is drawn from
//			int lineStartX = gChromoMap.x + gChromoMap.width; // this is where the line to the label is drawn from
//			int lineEndX = labelX - 2; // the label connects to the line here

			//x coords
			int labelX = gChromoMap.x - lineLength - gap - stringWidth;
			int lineStartX =  gChromoMap.x;
			int lineEndX =  gChromoMap.x- lineLength;

			//draw a rectangle as a background for the label
			g2.setColor(Colors.highlightedFeatureLabelBackgroundColour);
			g2.fillRect(labelX - 2, labelY - fontHeight, stringWidth + 4, fontHeight + 2);

			// set the label colour
			g2.setColor(Colors.highlightedFeatureLabelColour);
			// draw the label
			g2.drawString(featureName, labelX, labelY);

			// draw a line from the marker to the label
			g2.setColor(Colors.highlightedFeatureLabelBackgroundColour);
			g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);

			// set the feature colour
			g2.setColor(Colors.highlightedFeatureColour);
			// draw a line for the marker on the chromosome itself
			g2.drawLine(gChromoMap.x, featureY, gChromoMap.x + gChromoMap.width - 1, featureY);
		}
	}

//	------------------------------------------------------------------------------------------------------------------------------------
	
	// draws labels next to found features
	public static void drawFeatureLabelsInRange(Graphics2D g2)
	{
		// the usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();
		
		//reset the stroke in case it has been altered elesewhere
		g2.setStroke(new BasicStroke());
		
		//the features we need to draw
		Vector<Feature> features = MapViewer.winMain.fatController.featuresInRange;

		// for all features in our list
		for (Feature f : features)
		{
			// get the name of the feature
			String featureName = f.getName();
			int stringWidth = fm.stringWidth(featureName);

			// we need these for working out the y positions
			ChromoMap chromoMap = f.getOwningMap();
			GChromoMap gChromoMap = chromoMap.getGChromoMap();
			float mapEnd = chromoMap.getStop();
			// this factor normalises the position to a value between 0 and 100
			float scalingFactor = gChromoMap.height / mapEnd;

			// the y position of the feature itself
			int featureY;
			if (f.getStart() == 0.0f)
			{
				featureY = gChromoMap.y;
			}
			else
			{
				featureY = Math.round(gChromoMap.y + gChromoMap.currentY + (f.getStart() * scalingFactor));
			}		
			//check whether the map is inverted			
			if(gChromoMap.isPartlyInverted)
			{
				featureY = (int) ((mapEnd - f.getStart()) / (mapEnd / gChromoMap.height)) + (gChromoMap.y + gChromoMap.currentY);
			}
			
			// now work out the y position of the feature label
			// size and half size of our feature list
			int listSize = MapViewer.winMain.fatController.featuresInRange.size();
			float halfListSize = listSize / 2.0f;
			// this is where the label goes
			int labelY = 0;
			// the index of the feature in the list
			int index = features.indexOf(f);
			
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
			// the amount by which we want to move the label end away from the chromosome (in pixels)
			int lineLength = 50;
			int labelX = gChromoMap.x + gChromoMap.width + lineLength; // this is where the label is drawn from
			int lineStartX = gChromoMap.x + gChromoMap.width; // this is where the line to the label is drawn from
			int lineEndX = labelX - 2; // the label connects to the line here

			//draw a rectangle as a background for the label
			g2.setColor(Colors.foundFeatureLabelBackgroundColour);
			g2.fillRect(labelX - 2, labelY - fontHeight, stringWidth + 4, fontHeight + 2);
			
			// draw a line from the marker to the label
			g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);

			// set the label font colour
			g2.setColor(Colors.highlightedFeatureLabelColour);
			// draw the label
			g2.drawString(featureName, labelX, labelY);

			// set the feature colour
			g2.setColor(Colors.highlightedFeatureColour);
			// draw a line for the marker on the chromosome itself
			g2.drawLine(gChromoMap.x, featureY, gChromoMap.x + gChromoMap.width - 1, featureY);
		}
	}

}//end class
