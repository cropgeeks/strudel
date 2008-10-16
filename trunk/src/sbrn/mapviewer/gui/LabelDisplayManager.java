package sbrn.mapviewer.gui;

import java.awt.*;
import java.util.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.entities.*;


public class LabelDisplayManager
{
	
	// draws labels next to features
	public static void drawFoundFeatures(Graphics2D g2)
	{
		// the usual font stuff
		int fontHeight = 12;
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();
		
		for(GMapSet gMapSet : MapViewer.winMain.mainCanvas.gMapSetList)
		{		
			for(GChromoMap gMap : gMapSet.gMaps)
			{
				// for all features in our list
				for (Feature f : gMap.foundFeatures)
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
						featureY = Math.round(gChromoMap.y + (f.getStart() * scalingFactor));
					}
					
					// now work out the y position of the feature label
					// size and half size of our feature list
					int listSize = gMap.foundFeatures.size();
					// this is where the label goes
					int labelY = 0;
					// the index of the feature in the list
					int index = gMap.foundFeatures.indexOf(f);
					
					// the offset is the amount (in px) by which we need to move the label up or down relative to the feature itself
					float offset = 0;
					// if the list contains only a single feature
					if (listSize == 1 || gMapSet.zoomFactor >= gMapSet.singleChromoViewZoomFactor)
					{
						labelY = featureY + fontHeight/2;
					}
					// more than 1 feature in the list
					else
					{
						float interval = gMap.height / listSize;
						offset = index * interval;					
						labelY = gMap.y  + fontHeight + Math.round(offset);
					}
					
					// next decide where to place the label on x
					// the amount by which we want to move the label end away from the chromosome (in pixels)
					int lineLength = 15;
					int labelX = gChromoMap.x + gChromoMap.width + lineLength; // this is where the label is drawn from
					int lineStartX = gChromoMap.x + gChromoMap.width; // this is where the line to the label is drawn from
					int lineEndX = labelX - 2; // the label connects to the line here
					
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
		}
	}
}
