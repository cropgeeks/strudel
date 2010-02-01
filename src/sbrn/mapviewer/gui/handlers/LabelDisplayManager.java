package sbrn.mapviewer.gui.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.table.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
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
		//this is what we do when we are exploring a single continuous range on a given chromosome
//		if (Strudel.winMain.mainCanvas.drawFoundFeaturesInRange)
//		{
//			//the features we need to draw
//			Vector<Feature> features = new Vector<Feature>();
//			for (ResultsTableEntry tableEntry : Strudel.winMain.ffResultsPanel.resultsTable.getVisibleEntries())
//			{
//				//get the feature for this entry and add it to the lookup
//				Feature targetF = tableEntry.getTargetFeature();
//				if(!features.contains(targetF))
//					features.add(targetF);
//				//do the same for the homolog
//				Feature homolog = tableEntry.getHomologFeature();
//				if (tableEntry.getHomologFeature() != null && !features.contains(homolog))
//					features.add(homolog);
//			}
//
//			//only draw those features that are actually visible on canvas
//			drawFeatureLabelsInRange(Strudel.winMain.fatController.selectionMap, g2, Utils.checkFeatureVisibility(Strudel.winMain.fatController.selectionMap, features), false, null);
//		}
		//this is what we do when the results come from a name based feature search, i.e. the features can be on a number of different chromosomes
		if (Strudel.winMain.mainCanvas.drawFeaturesFoundByName || Strudel.winMain.mainCanvas.drawFoundFeaturesInRange)
		{
			//we need to have a list of gMaps that have features in the results list
			//for each gMap we need to know the associated features
			HashMap<GMapSet, LinkedList<Feature>> lookup = new HashMap<GMapSet, LinkedList<Feature>>();
			//get the entries from the results table
			TableModel model = Strudel.winMain.ffResultsPanel.resultsTable.getModel();
			if (model instanceof HomologResultsTableModel)
			{
				for (ResultsTableEntry tableEntry : Strudel.winMain.ffResultsPanel.resultsTable.getVisibleEntries())
				{
					//get the feature for this entry and add it to the lookup
					addFeatureToLookup(tableEntry.getTargetFeature(), lookup);
					//do the same for the homolog
					if (tableEntry.getHomologFeature() != null)
						addFeatureToLookup(tableEntry.getHomologFeature(), lookup);
				}
				//then draw labels for each feature list for each gmap as a separate grouping
				for (GMapSet gMapSet : lookup.keySet())
				{
					LinkedList<Feature> features = lookup.get(gMapSet);
					features = Utils.sortFeaturesWithinGMapset(features, gMapSet);
					drawFeatureLabelsInRange(null, g2, features, false, gMapSet);
				}
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------

	private static void addFeatureToLookup(Feature feature, HashMap<GMapSet, LinkedList<Feature>> lookup)
	{
		GMapSet gMapSet = feature.getOwningMap().getGChromoMaps().get(0).owningSet;
		//check whether its gMap is listed here
		if(!lookup.keySet().contains(gMapSet))
		{
			//if not, add it to the lookup
			lookup.put(gMapSet, new LinkedList<Feature>());
		}
		//if the feature is not present in the feature list for this gmap, add it
		if(!lookup.get(gMapSet).contains(feature))
			lookup.get(gMapSet).add(feature);
	}

	//------------------------------------------------------------------------------------------------------------------------------------

	//this just draws a label for a single highlighted feature
	public static void drawHighlightedFeatureLabel(Graphics2D g2, Feature f, Feature homolog, GChromoMap gMap, GChromoMap refMap)
	{
		// the usual font stuff
		g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
		FontMetrics fm = g2.getFontMetrics();

		// we need these for working out the y positions
		ChromoMap chromoMap = f.getOwningMap();
		float mapEnd = chromoMap.getStop();
		// this factor normalises the position to a value between 0 and 100
		float scalingFactor = gMap.height / mapEnd;

		// the y position of the feature itself
		int featureY;
		if (f.getStart() == 0.0f)
		{
			featureY = gMap.y;
		}
		else
		{
			featureY = Math.round(gMap.y + gMap.currentY + (f.getStart() * scalingFactor));
		}

		//the y position of the feature label
		int labelY = featureY + (fontHeight/2);

		drawFeatureLabel(g2, f, gMap, refMap, true, true, false, labelY, featureY,fm);
	}

	//	------------------------------------------------------------------------------------------------------------------------------------

	// draws labels next to found features in a specified range only
	public static void drawFeatureLabelsInRange(GChromoMap gMap, Graphics2D g2, List<Feature> features, boolean isMouseOver, GMapSet gMapSet)
	{
		try
		{
			g2.setFont(new Font("Sans-serif", Font.PLAIN, fontHeight));
			FontMetrics fm = g2.getFontMetrics();

			//first work out the features' y positions
			//we need to create a HashMap with the default positons
			//these will all be at the featureY of the Feature
			HashMap<Feature, Integer> featurePositions = null;
			HashMap<Feature, Integer> laidoutPositions = null;
			if(gMap != null && gMapSet == null)
			{
				featurePositions = calculateFeaturePositionsOnGMap(gMap, features);
			}
			else if(gMap == null && gMapSet != null)
			{
				featurePositions = calculateFeaturePositionsInGMapSet(gMapSet, features);
			}

			//then work out the actual positions after correction for collision of labels
			laidoutPositions = calculateLabelPositionsOnScreen(features, featurePositions);

			// for all features in our list
			for (Feature f : features)
			{
				if (f != null)
				{
					// this is where the label goes
					int labelY = laidoutPositions.get(f);
					int featureY = featurePositions.get(f);

					//if we are doing this at the mapset scale (e.g. when we are drawing labels for found features) we need to
					//figure out the gMap from the gMapSet here
					if(gMap == null && gMapSet != null)
					{
						gMap = Utils.getGMapByNameAndGMapset(f.getOwningMap().getName(), gMapSet);
					}

					drawFeatureLabel(g2, f, gMap, null, true, false,isMouseOver, labelY, featureY, fm);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	//------------------------------------------------------------------------------------------------------------------------------------

	//this draws a label for a single feature
	public static void drawFeatureLabel(Graphics2D g2, Feature f, GChromoMap gMap, GChromoMap refMap, boolean labelOnRight,
					boolean highlight, boolean isMouseOver, int labelY, int featureY,FontMetrics fm)
	{
		// get the name of the feature
		String featureName = f.getName() + " (" + f.getType() + ")";
		int stringWidth = fm.stringWidth(featureName);

		// next decide where to place the label on x
		int lineLength = 30; // the amount by which we want to move the label end away from the chromosome (in pixels)
		int labelX = -1; // this is where the label is drawn from
		int lineStartX = -1; // this is where the line to the label is drawn from
		int lineEndX = -1; // the label connects to the line here

		//we need to work out whether the label should go on the right or the left
		//by default we can have them on the right but if the genome is the last one to the right the label text may extend off screen
		//check for this and place the label to the left if this is the case
		int genomeIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(gMap.owningSet);
		if(!highlight && (genomeIndex == (Strudel.winMain.dataContainer.gMapSets.size()-1)))
		{
			labelOnRight = false;
		}

		//if we do have a homolog we need to work out where it is in relation to this feature and place the label out of the way of the link line
		if(highlight && refMap != null)
		{
			int targetGenomeIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(gMap.owningSet);
			int referenceGenomeIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(refMap.owningSet);

			//we want the label on the right if the target genome is to the right of the reference but only if this is not the last genome on the right
			if((targetGenomeIndex > referenceGenomeIndex) && !(targetGenomeIndex == (Strudel.winMain.dataContainer.gMapSets.size()-1)))
			{
				//place label to the right of the chromo
				labelOnRight = true;
			}
			//otherwise put the label on the left but only if this is not the leftmost genome
			else if(targetGenomeIndex != 0)
			{
				labelOnRight = false;
			}
		}

		//this is what we do if the label needs to be on the right
		if(labelOnRight)
		{
			//place label to the right of the chromo
			lineStartX =  gMap.x + gMap.width;
			lineEndX =  lineStartX + lineLength;
			labelX = lineEndX;
		}
		else//label on left
		{
			lineStartX =  gMap.x;
			lineEndX =  lineStartX - lineLength;
			labelX = lineEndX - stringWidth;
		}

		//set the colour appropriately
		if(highlight)
			g2.setColor(Colors.strongEmphasisLinkColour);
		else if(isMouseOver)
			g2.setColor(Colors.highlightedFeatureColour);
		else
			g2.setColor(Colors.featureColour);

		// draw a line from the marker to the label
		g2.drawLine(lineStartX, featureY, lineEndX, labelY - fontHeight / 2);

		//draw a rectangle as a background for the label
		if(highlight)
			g2.setColor(Colors.highlightedFeatureLabelBackgroundColour);
		float arcSize = fontHeight / 1.5f;
		int horizontalGap = 3;
		int verticalGap = 4;
		RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX - horizontalGap, labelY - fontHeight, stringWidth + horizontalGap * 2, fontHeight + verticalGap, arcSize, arcSize);
		g2.fill(backGroundRect);

		// set the label font colour
		if(isMouseOver || highlight)
			g2.setColor(Colors.highlightedFeatureLabelColour);
		else
			g2.setColor(Colors.featureLabelColour);

		// draw the label
		g2.drawString(featureName, labelX, labelY);

		//if necessary draw a highlighted line for the marker on the chromosome itself
		if(highlight)
		{
			// set the feature colour
			g2.setColor(Colors.highlightedFeatureColour);

			g2.drawLine(gMap.x -1, featureY, gMap.x + gMap.width +1, featureY);
		}
	}


	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//calculates the label positions for a set of features on a given gMap by  -- if necessary -- shuffling labels downwards
	@SuppressWarnings("unchecked")
	private static HashMap<Feature, Integer> calculateLabelPositionsOnScreen(List<Feature> features, HashMap<Feature, Integer> featurePositions)
	{
		//we want to start off with the same kind of order and positions as the feature positions
		HashMap<Feature, Integer> labelPositions = (HashMap<Feature, Integer>)featurePositions.clone();

		//the label's height
		float labelHeight = fontHeight*verticalSpacer;

		//now we want to work out where we start drawing the labels relative to the range start point
		//we want the labels fanning out evenly on y both up and downwards from the features themselves
		//first work out the combined height of the labels
		int totalLabelHeight = Math.round(features.size() * labelHeight);
		//the difference between the total label height and the canvas size
		int excess = totalLabelHeight - Strudel.winMain.mainCanvas.getHeight();

		//the label offset on y relative to the start of the features themselves
		//need to subtract this from each label position
		int offset = -1;
		if(excess > 0)
			offset = Math.round(excess / 2.0f);
		//don't want the offset to be negative because this will put labels in the wrong place
		else
			offset = 0;

		//this is where we shuffle the label positions  relative to the feature positions
		//need to check that the label interval is no less than the height of an individual label plus some space at
		//the top and bottom of it respectively
		for (int i = 0; i < features.size(); i++)
		{
			//retrive the current and the subsequent feature
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
				//then we need to shuffle them along
				int yDistance = labelPositions.get(f2) - labelPositions.get(f1);
				if (yDistance < labelHeight)
				{
					//move the position of feature 2  on y by the so it is at the position of feature 1 plus one label height
					//need to make this change both to the map with the laid out positions as well as the default one because
					//the value from the latter will be used in the next iteration
					int newPos = Math.round(labelPositions.get(f1) + labelHeight);
					labelPositions.put(f2, newPos);
				}
			}
		}

		//now subtract the offset  from each position so the labels fan out properly
		//also apply a correction factor to move the label down by half a label height relative to the feature's y pos so
		//that the label's center on y is aligned with the feature y
		for (Feature feature : labelPositions.keySet())
		{
			//if the difference between the feature y pos of this feature and that of the next one is less than the labelheight
			//then we need to shuffle them along
			int newPos = labelPositions.get(feature) - offset + (fontHeight / 2);
			//put the adjusted value back
			labelPositions.put(feature, newPos);
		}

		return labelPositions;
	}


	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//works out the feature positions for a set of features all contained in a single GChromoMap
	private static HashMap<Feature, Integer> calculateFeaturePositionsOnGMap(GChromoMap gChromoMap, List<Feature> features)
	{

		HashMap<Feature, Integer> featurePositions = new HashMap<Feature, Integer>();

		for (Feature f : features)
		{
			if (f != null)
			{
				//System.out.println("feature name " + f.getName());

				// the y position of the feature itself on the canvas, in pixel coords relative to the canvas boundaries
				int featureY = Utils.relativeFPosToPixelOnCanvas(gChromoMap, f.getStart(), gChromoMap.isFullyInverted);
				featurePositions.put(f, featureY);

				//System.out.println("position of feature " + f.getName() + " on map " + gChromoMap.name + " = " + featureY);
			}
		}

		return featurePositions;
	}

	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//works out the feature positions for a set of features that may lie on different chromosomes within the same GMapSet
	private static HashMap<Feature, Integer> calculateFeaturePositionsInGMapSet(GMapSet gMapSet, List<Feature> features)
	{
		HashMap<Feature, Integer> featurePositions = new HashMap<Feature, Integer>();

		for (Feature f : features)
		{
			if (f != null)
			{
				//				//System.out.println("feature name " + f.getName());

				//if we are doing this at the mapset scale (e.g. when we are drawing labels for found features) we need to
				//figure out the gMap from the gMapSet here
				GChromoMap gChromoMap = Utils.getGMapByNameAndGMapset(f.getOwningMap().getName(), gMapSet);

				// the y position of the feature itself on the canvas, in pixel coords relative to the canvas boundaries
				int featureY = Utils.relativeFPosToPixelOnCanvas(gChromoMap, f.getStart(), gChromoMap.isFullyInverted);
				featurePositions.put(f, featureY);

				//				//System.out.println("position of feature " + f.getName() + " on map " + gChromoMap.name + " = " + featureY);
			}
		}

		return featurePositions;
	}

	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	//draw all labels for all features on this mapset's selected chromosome if this has been requested
	public static void drawLabelsForAllVisibleFeatures(Graphics2D g2, GChromoMap gMap)
	{
		//make a separate vector object with all the linked features for this chromo
		Vector<Feature> vec = new Vector<Feature>();
		for (int i = 0; i < gMap.allLinkedFeatures.length; i++)
		{
			vec.add(gMap.allLinkedFeatures[i]);
		}

		//check whether all these features are showing
		vec = Utils.checkFeatureVisibility(gMap, vec);

		//reverse the order of the features if the map is inverted
		if((gMap.isFullyInverted || gMap.isPartlyInverted))
		{
			Collections.reverse(vec);
		}

		//now draw the labels
		drawFeatureLabelsInRange(gMap, g2, vec, false, null);
	}


	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}//end class