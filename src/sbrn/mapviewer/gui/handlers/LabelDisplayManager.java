package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

/*
 * Class for handling all things label-related (working out label positions, drawing sets of features or individual features etc)
 */
public class LabelDisplayManager
{
	
	//this font height is used for all labels drawn
	private static int fontHeight = 11;
	
	//a multiplier for the font height that allows us to space the labels vertically
	private static float verticalSpacer = 1.3f;
	
	//-------------------------------------------------------------------------------------------------------------------------------
	
	//draws labels for a set of features that the user searched for 
	public static void drawLabelsForFoundFeatures(Graphics2D g2)
	{
		//the features we need to draw
		Vector<Feature> features = FeatureSearchHandler.featuresInRange;
		
		//all the features should be on the same map so we can just query the first element for its parent map
		GChromoMap gChromoMap = features.get(0).getOwningMap().getGChromoMap();	
		Vector<GChromoMap> gMaps = new Vector<GChromoMap>();
		gMaps.add(gChromoMap);
		
		//only draw those features that are actually visible on canvas
		drawFeatureLabelsInRange(g2, Utils.checkFeatureVisibility(features), false);		
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//this just draws a label for a single highlighted feature
	public static void drawHighlightedFeatureLabel(Graphics2D g2, Feature f, Feature homolog)
	{
		// the usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();	
		
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
		
		//the y position of the feature label
		int labelY = featureY + (fontHeight/2);
		
		// next decide where to place the label on x
		// the amount by which we want to move the label end away from the chromosome (in pixels)
		int lineLength = 50;	
		//x coords
		int labelX = -1;
		int lineStartX =  -1;
		int lineEndX =  -1;
		
		//if we have no homologs that we are drawing links to we can just place the label to the left of the chromo always
		//it will not get in the way of anything there		
		lineStartX =  gChromoMap.x;
		lineEndX =  lineStartX - lineLength;
		labelX = lineEndX - stringWidth;
		
		//if we do have a homolog we need to work out where it is in relation to this feature and place the label out of the way of the link line
		if(homolog != null)
		{
			int targetGenomeIndex = MapViewer.winMain.dataContainer.allMapSets.indexOf(f.getOwningMap().getOwningMapSet());
			int referenceGenomeIndex = MapViewer.winMain.dataContainer.allMapSets.indexOf(homolog.getOwningMap().getOwningMapSet());
			
			if(targetGenomeIndex > referenceGenomeIndex)
			{
				//place label to the right of the chromo
				lineStartX =  gChromoMap.x + gChromoMap.width;
				lineEndX =  lineStartX + lineLength;
				labelX = lineEndX;
			}
		}
		
		// draw a line from the marker to the label
		g2.setColor(Colors.strongEmphasisLinkColour);
		g2.drawLine(lineStartX, featureY, lineEndX , labelY - fontHeight / 2);
		
		//draw a rounded rectangle as a background for the label
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
		
		
		// set the feature colour
		g2.setColor(Colors.highlightedFeatureColour);
		
		// draw a line for the marker on the chromosome itself
		g2.drawLine(gChromoMap.x -1, featureY, gChromoMap.x + gChromoMap.width +1, featureY);
		
	}
	
	//	------------------------------------------------------------------------------------------------------------------------------------
	
	// draws labels next to found features in a specified range only
	public static void drawFeatureLabelsInRange(Graphics2D g2, Vector<Feature> features, boolean isMouseOver)
	{	
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();
		
		//first work out the features' y positions
		//we need to create a LinkedHashMap withe the default positons
		//these will all be at the featureY of the Feature
		HashMap<Feature, Integer> featurePositions = calculateFeaturePositions(features);
		
		//now work out the actual positions after correction for collision of labels
		HashMap<Feature, Integer> laidoutPositions = calculateLabelPositions(features, featurePositions);
		
		// for all features in our list
		for (Feature f : features)
		{
			if (f != null)
			{				
				// get the name of the feature
				String featureName = f.getName();
				int stringWidth = fm.stringWidth(featureName);
				// this is where the label goes
				int labelY = laidoutPositions.get(f);
				int featureY = featurePositions.get(f);
				
				// next decide where to place the label on x
				int mapSetX = Math.round(f.getOwningMap().getGChromoMap().owningSet.xPosition);
				int chromoWidth = MapViewer.winMain.mainCanvas.chromoWidth;
				// the amount by which we want to move the label end away from the chromosome (in pixels)
				int lineLength = 50;
				int labelX = mapSetX + chromoWidth + lineLength; // this is where the label is drawn from
				int lineStartX = mapSetX + chromoWidth; // this is where the line to the label is drawn from
				int lineEndX = labelX - 2; // the label connects to the line here
				if (isMouseOver)
				{
					//set the colour to highlight feature
					g2.setColor(Colors.highlightedFeatureColour);
				}
				else
				{
					//set the colour to draw feature normally
					g2.setColor(Colors.featureColour);
				}
				
				// draw a line to highlight the marker on the chromosome itself
				g2.drawLine(mapSetX, featureY, mapSetX + MapViewer.winMain.mainCanvas.chromoWidth - 1, featureY);
				// draw a line from the marker to the label
				g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);
				//draw a rectangle as a background for the label
				float arcSize = fontHeight / 1.5f;
				int horizontalGap = 3;
				int verticalGap = 4;
				RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX - horizontalGap, labelY - fontHeight, stringWidth + horizontalGap * 2, fontHeight + verticalGap, arcSize, arcSize);
				g2.fill(backGroundRect);
				// set the label font colour
				g2.setColor(Colors.highlightedFeatureLabelColour);
				// draw the label
				g2.drawString(featureName, labelX, labelY);
			}
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//calculates the label positions for a set of features by  -- if necessary -- shuffling labels downwards
	@SuppressWarnings("unchecked")
	private static HashMap<Feature, Integer> calculateLabelPositions(Vector<Feature> features, HashMap<Feature, Integer> featurePositions)
	{
		//we want to start off with the same kind of order and positions as the feature positions
		HashMap<Feature, Integer> labelPositions = (HashMap<Feature, Integer>)featurePositions.clone();

		//the label's height
		float labelHeight = fontHeight*verticalSpacer;
		
		//this is where we shuffle the label positions downwards relative to the feature positions
		//need to check that the label interval is no less than the height of an individual label plus some space at 
		//the top and bottom of it respectively	
		for (int i = 0; i < features.size(); i++)
		{
			Feature f1 = features.get(i);

			//when we get to the last feature we just  break out of the loop
			if(i == features.size()-1)
			{
				break;
			}

			Feature f2 = features.get(i+1);
			
			if (f1 != null && f2 != null)
			{
				//if the difference between the feature y pos of this feature and that of the next one is less than the labelheight
				//then we need to shuffle them downwards
				int yDistance = labelPositions.get(f2) - labelPositions.get(f1);
				if (yDistance < labelHeight)
				{
					//move the position of feature 2 down on y by the so it is at the position of feature 1 plus one label height
					//need to make this change both to the map with the laid out positions as well as the default one because
					//the value from the latter will be used in the next iteration
					int newPos = Math.round(labelPositions.get(f1) + labelHeight);						
					labelPositions.put(f2, newPos);	
				}
			}
		}
		
		//now we want to work out where we start drawing the labels relative to the range start point
		//we want the labels fanning out evenly on y both up and downwards from the features themselves
		//first work out the combined height of the labels
		int totalLabelHeight = Math.round(features.size() * labelHeight);	
		//the difference between the total label height and the canvas size
		int excess = totalLabelHeight - MapViewer.winMain.mainCanvas.getHeight();
		
		//the label offset on y relative to the start of the features themselves
		//need to subtract this from each label position
		int offset = Math.round(excess / 2.0f);	
		//if we are just doing the mouse overs we want the labels to fan out in the usual "butterfly" shape
		if(excess < 0 && !features.get(0).getOwningMap().getGChromoMap().owningSet.alwaysShowAllLabels)
			offset = Math.round(totalLabelHeight/2) - Math.round(labelHeight/2);
		//if we are showing all labels for this genome we need to fan them out downwards from the topmost feature
		//don't want the offset to be negative because this will put labels in the wrong place
		else
			offset = 0;

		//now subtract the offset  from each position so the labels fan out properly
		//also apply a correction factor to move the label down by half a label height relative to the feature's y pos so
		//that the label's center on y is aligned with the feature y
		for (Feature feature : labelPositions.keySet())
		{
			int newPos = labelPositions.get(feature) - offset + (fontHeight / 2);
			//put the adjusted value back
			labelPositions.put(feature, newPos);
		}
		
		return labelPositions;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//works out the feature positions for a set of features
	private static HashMap<Feature, Integer> calculateFeaturePositions(Vector<Feature> features)
	{
		HashMap<Feature, Integer> featurePositions = new HashMap<Feature, Integer>();
		
		for (Feature f : features)
		{
			if (f != null)
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
				if (gChromoMap.isPartlyInverted)
				{
					featureY = (int) ((mapEnd - f.getStart()) / (mapEnd / gChromoMap.height)) + (gChromoMap.y + gChromoMap.currentY);
				}
				featurePositions.put(f, featureY);
			}
		}
		
		return featurePositions;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	//draw all labels for all features on this chromosome if this has been requested at the level of the mapset
	public static void drawLabelsForAllVisibleFeatures(Graphics2D g2, GMapSet gMapSet)
	{
		//combine all the features from the visible maps into one
		Vector<Feature> combinedFeatures = new Vector<Feature>();
		Vector<GChromoMap> gMaps = gMapSet.getVisibleMaps();
		for (GChromoMap gMap : gMaps)
		{
			//get all the features of this map and put them into the combined features vector
			combinedFeatures.addAll(Arrays.asList(gMap.allLinkedFeatures));
		}
		
		//now draw the labels
		//only use those features that are actually visible on canvas
		combinedFeatures = Utils.checkFeatureVisibility(combinedFeatures);
		drawFeatureLabelsInRange(g2, combinedFeatures, false);
	}
	
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
}//end class
