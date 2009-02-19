package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.entities.*;


public class LabelDisplayManager
{

	private static int fontHeight = 11;


	//------------------------------------------------------------------------------------------------------------------------------------

	// draws labels next to found features
	public static void drawHighlightedFeatureLabels(Graphics2D g2, Feature f1, Feature f2)
	{
		// the usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		//easiest to do this with a local Vector for the two features -- saves duplication 
		Vector<Feature> features = new Vector<Feature>();
		features.add(f1);
		features.add(f2);
		
		//work out the mapset index for the homolog
		int mapSetIndexF2 = MapViewer.winMain.dataContainer.gMapSetList.indexOf(f2.getOwningMap().getGChromoMap().owningSet);

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
			int lineLength = 50;
			int gap = 3;
			
			//x coords
			int labelX = gChromoMap.x - lineLength - gap - stringWidth;
			int lineStartX =  gChromoMap.x;
			int lineEndX =  gChromoMap.x- lineLength;
			
			// next decide where to place the label on x					
			//determine whether the marker should go on the left or right
			boolean markersRight = false;

			//we want the label on the right for the rightmost genome, left for the leftmost genome
			if((features.indexOf(f) == 0 && mapSetIndexF2 == 0) ||
							(features.indexOf(f) == 1 && mapSetIndexF2 == 2))
			{
				markersRight = true;	
			}
			else
			{
				markersRight =  false;
			}
			if (markersRight)
			{				
				lineStartX = gChromoMap.x + gChromoMap.width;		
				labelX = lineStartX + lineLength + gap; 
				lineEndX = labelX - gap;
			}

			//draw a rectangle as a background for the label
			g2.setColor(Colors.highlightedFeatureLabelBackgroundColour);
			float arcSize = fontHeight/1.5f;
			int horizontalGap = 3;
			int verticalGap = 4;
			RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX - horizontalGap, labelY - fontHeight, stringWidth + horizontalGap*2,
							fontHeight + verticalGap, arcSize, arcSize);
			g2.fill(backGroundRect);

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
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		//reset the stroke in case it has been altered elsewhere
		g2.setStroke(new BasicStroke());
		
		//the features we need to draw
		Vector<Feature> features = MapViewer.winMain.fatController.featuresInRange;
		//sort these by position
		Collections.sort(features);
		
		//first work out the features y positions
		//we need to create a LinkedHashMap withe the default positons
		//these will all be at the featureY of the Feature
		LinkedHashMap<Feature, Integer> featurePositions = calculateFeaturePositions(features);

		//now work out the actual positions after correction for collision of labels
		LinkedHashMap<Feature, Integer> laidoutPositions = calculateLabelPositions(features, featurePositions);
		
		// for all features in our list
		for (Feature f : features)
		{
			// get the name of the feature
			String featureName = f.getName();
			int stringWidth = fm.stringWidth(featureName);
			
			// we need these for working out the y positions
			ChromoMap chromoMap = f.getOwningMap();
			GChromoMap gChromoMap = chromoMap.getGChromoMap();
			
			// this is where the label goes
			int labelY = laidoutPositions.get(f);
			int featureY = featurePositions.get(f);
			
			//apply a correction factor to move the label down by half a label height relative to the feature's y pos so
			//that the label's center on y is aligned with the feature y
			labelY = labelY + (fontHeight/2);
			
			// next decide where to place the label on x
			// the amount by which we want to move the label end away from the chromosome (in pixels)
			int lineLength = 50;
			int labelX = gChromoMap.x + gChromoMap.width + lineLength; // this is where the label is drawn from
			int lineStartX = gChromoMap.x + gChromoMap.width; // this is where the line to the label is drawn from
			int lineEndX = labelX - 2; // the label connects to the line here

			//draw a rectangle as a background for the label
			g2.setColor(Colors.foundFeatureLabelBackgroundColour);
//			g2.fillRect(labelX - 2, labelY - fontHeight, stringWidth + 4, fontHeight + 2);
			
			// draw a line from the marker to the label
			g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);
			
			//draw a rectangle as a background for the label
			g2.setColor(Colors.foundFeatureLabelBackgroundColour);
			float arcSize = fontHeight/1.5f;
			int horizontalGap = 3;
			int verticalGap = 4;
			RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX - horizontalGap, labelY - fontHeight, stringWidth + horizontalGap*2,
							fontHeight + verticalGap, arcSize, arcSize);
			g2.fill(backGroundRect);

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
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	private static LinkedHashMap<Feature, Integer> calculateLabelPositions(Vector<Feature> features, LinkedHashMap<Feature, Integer> featurePositions)
	{
		LinkedHashMap<Feature, Integer> labelPositions = (LinkedHashMap<Feature, Integer>)featurePositions.clone();
		
		//the label's height
		int gap = 1;
		int labelHeight = fontHeight + gap;
		
		//first we want to work out where we start drawing the labels relative to the range start point
		//we want the labels fanning out evenly on y both up and downwards from the features themselves
		//first work out the combined height of the labels
		int totalLabelHeight = features.size() * labelHeight;
		//then the height of the interval
		//for this we need the interval start and end values
		MTFindFeaturesInRangePanel ffInRangePanel = MapViewer.winMain.ffInRangeDialog.ffInRangePanel;		
		float intervalStart = ((Number)ffInRangePanel.getRangeStartSpinner().getValue()).floatValue();
		float intervalEnd = ((Number)ffInRangePanel.getRangeEndSpinner().getValue()).floatValue();

		//and the chromosome the interval is on
		String genome = (String) ffInRangePanel.getGenomeCombo().getSelectedItem();
		String chromosome =  (String) ffInRangePanel.getChromoCombo().getSelectedItem();
		GChromoMap gChromoMap = Utils.getGMapByName(chromosome,genome);
		ChromoMap chromoMap = gChromoMap.chromoMap;
		
		//convert the interval values to actual pixel positions on the canvas
		int intervalStartPos = (int) ((chromoMap.getGChromoMap().owningSet.chromoHeight / chromoMap.getStop()) * intervalStart);
		int intervalEndPos = (int) ((chromoMap.getGChromoMap().owningSet.chromoHeight / chromoMap.getStop()) * intervalEnd);
		
		//size of the interval in pixels
		int intervalHeight = intervalEndPos - intervalStartPos;
		
		//the difference between the total label height and the interval height
		int differential = totalLabelHeight - intervalHeight;
		//the label offset on y relative to the start of the features themselves
		//need to subtract this from each label position
		int offset = Math.round(differential / 2.0f);	
		
		//don't want the offset to be negative
		if(differential < 0)
			offset = 0;
		
		//need to check that the label interval is no less than the height of an individual label plus some space at 
		//the top and bottom of it respectively	
		for (int i = 0; i < features.size(); i++)
		{
			try
			{
				Feature f1 = features.get(i);
				Feature f2 = features.get(i+1);

				//if the difference between the feature y pos of this feature and that of the next one is less than the labelheight
				//then we need to shuffle them downwards
				int yDistance = labelPositions.get(f2) - labelPositions.get(f1);
				if(yDistance < labelHeight)
				{
					//move the position of feature 2 down on y by the so it is at the position of feature 1 plus one label height
					//need to make this change both to the map with the laid out positions as well as the default one because
					//the value from the latter will be used in the next iteration
					int newPos = labelPositions.get(f1) + labelHeight;
					labelPositions.put(f2, newPos);
				}
			}
			//this occurs when we process the last element in the features vector -- just ignore
			catch (ArrayIndexOutOfBoundsException e)
			{
			}
		}
		
		//now subtract from each position the offset so things fan out properly
		for (Feature feature : labelPositions.keySet())
		{
			int newPos = labelPositions.get(feature) - offset;
			labelPositions.put(feature, newPos);
		}

		return labelPositions;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private static LinkedHashMap<Feature, Integer> calculateFeaturePositions(Vector<Feature> features)
	{
		LinkedHashMap<Feature, Integer> featurePositions = new LinkedHashMap<Feature, Integer>();
		
		for (Feature f : features)
		{
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

			featurePositions.put(f, featureY);
		}

		return featurePositions;
	}

}//end class
