package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class Utils
{

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//finds a genome by name
	public static MapSet getMapSetByName(String name, LinkedList<MapSet> mapsetList)
	{
		MapSet foundSet = null;

		//we need to search all chromomaps in all mapsets for this
		// for all gmapsets
		for (MapSet mapSet : mapsetList)
		{
			if(mapSet.getName().equalsIgnoreCase(name))
				foundSet = mapSet;
		}

		return foundSet	;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//finds a MapSet by name
	public static MapSet getMapSetByName(String name)
	{
		MapSet foundSet = null;

		// for all mapsets
		for (MapSet mapSet : Strudel.winMain.dataContainer.allMapSets)
		{
			if(mapSet.getName().equals(name))
				foundSet = mapSet;
		}

		return foundSet	;
	}


	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//finds a map by name; if there are multiple instrances of the gmapset the map belongs to, this will return
	//the first one
	public static GChromoMap getGMapByName(String gMapName, String gMapSetName)
	{
		GChromoMap foundMap = null;

		//we need to search all chromomaps in all mapsets for this
		GMapSet foundSet = null;
		// for all gmapsets
		for (GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
		{
			if(gMapSet.name.equals(gMapSetName))
			{
				foundSet = gMapSet;
				break;
			}
		}

		// for all gchromomaps within each mapset
		for (GChromoMap gChromoMap : foundSet.gMaps)
		{
			if(gChromoMap.name.equals(gMapName))
				foundMap = gChromoMap;
		}

		return foundMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//finds a map by name; if there are multiple instrances of the gmapset the map belongs to, this will return
	//the first one
	public static GChromoMap getGMapByNameAndGMapset(String gMapName, GMapSet targetSet)
	{
		GChromoMap foundMap = null;

		//we need to search all chromomaps in all mapsets for this
		GMapSet foundSet = null;
		// for all gmapsets
		for (GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
		{
			if(gMapSet == targetSet)
			{
				foundSet = gMapSet;
				break;
			}
		}

		// for all gchromomaps within each mapset
		for (GChromoMap gChromoMap : foundSet.gMaps)
		{
			if(gChromoMap.name.equals(gMapName))
				foundMap = gChromoMap;
		}

		return foundMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//finds a Feature by name in the centrally held set of GMapSets
	public static ArrayList<Feature> getFeatureByName(String featureName)
	{
		ArrayList<Feature> allFeatures = new ArrayList<Feature>();

		//we need to search all chromomaps in all mapsets for this
		// for all gmapsets
		for (GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
		{
			// for all gchromomaps within each mapset
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				//get the ChromoMap object
				//look up the name in this
				ArrayList<Feature> features = gChromoMap.chromoMap.getFeatures(featureName);
				if(features.size() > 0)
				{
					allFeatures.addAll(features);
				}
			}
		}

		return allFeatures;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static void buildLinkSetFromFeatureLists(LinkSet linkSet, ArrayList<Feature> f1List, ArrayList<Feature> f2List, String blastScoreStr, String linkAnnotation) throws ParseException
	{
		// Pair up every instance of f1 with f2
		for (Feature f1: f1List)
			for (Feature f2: f2List)
			{
				Link link = new Link(f1, f2);
				linkSet.addLink(link);

				// We also add the Link to each Feature so the Feature
				// itself knows about the links it has with others
				f1.getLinks().add(link);
				f2.getLinks().add(link);

				//add the BLAST score as evidence
				DecimalFormat df = new DecimalFormat("0.###E0");
				Number blastScore = df.parse(blastScoreStr);
				link.setBlastScore(blastScore.doubleValue());

				//add the annotation, if there is any
				if(linkAnnotation != null)
					link.setAnnotation(linkAnnotation);

				// TODO: Do we want to add a list of references Features to the Feature object itself, so it knows who it links to?
				// If so, how do we deal with, eg removing MapSets andkeeping these lists (and the LinkSet!) up to date.
			}
	}



	// --------------------------------------------------------------------------------------------------------------------------------

	//this method makes a link for each possible combination of features in list features1 and features2
	//we need the multiple links because each the features in a given list all have the same name but different positions and have to be treated as synonymous but distinct
	public static void addLinkToLinkset(LinkSet linkSet, ArrayList<Feature> features1, ArrayList<Feature> features2, String blastScoreStr, String linkAnnotation)
	{
		for (Feature feature1 : features1)
		{
			for (Feature feature2 : features2)
			{
				// Pair up every instance of f1 with f2
				Link link = new Link(feature1, feature2);
				linkSet.addLink(link);
				// We also add the Link to each Feature so the Feature
				// itself knows about the links it has with others
				feature1.getLinks().add(link);
				feature2.getLinks().add(link);
				//add the BLAST score as evidence
				try
				{
					DecimalFormat df = new DecimalFormat("0.###E0");
					Number blastScore = df.parse(blastScoreStr);
					link.setBlastScore(blastScore.doubleValue());
				}
				catch (ParseException e)
				{
					throw new NumberFormatException("The homology between " + feature1 + " and " + feature2 + " contains an invalid e-Value. ");
				}
				//add the annotation, if there is any
				if (linkAnnotation != null)
					link.setAnnotation(linkAnnotation);
			}
		}

		// TODO: Do we want to add a list of references Features to the Feature object itself, so it knows who it links to?
		// If so, how do we deal with, eg removing MapSets andkeeping these lists (and the LinkSet!) up to date.

	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static ArrayList <Feature> getFeaturesByName(String name, LinkedList<MapSet> mapSets )
	{
		ArrayList <Feature> allFeatures = new ArrayList<Feature>();
		for(MapSet mapSet : mapSets)
		{
			ArrayList <Feature> features = getFeaturesByName(name, mapSet);
			allFeatures.addAll(features);
		}
		return allFeatures;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// Searches over single MapSet to find a feature whose name matches the one given.
	public static ArrayList <Feature> getFeaturesByName(String name, MapSet mapSet)
	{
		ArrayList <Feature> foundFeatures = new ArrayList<Feature>();

		for (ChromoMap map: mapSet.getMaps())
		{
			ArrayList<Feature> features = map.getFeatures(name);
			if(features != null && features.size() > 0)
				foundFeatures.addAll(features);
		}

		return foundFeatures;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/*
	 * Makes an array of colours that can be used to draw the lines between chromosomes. Uses some random numbers but also restricts the range of colours so the overall pallette is not too garish.
	 */
	public static Color[] makeColours(int numColours)
	{
		Color[] colours = new Color[numColours];
		float increment = 1 / (float) numColours;
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
			colours[i] = Color.getHSBColor(currentHue, 0.4f, 0.8f);
			currentHue += increment;
		}
		return colours;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	//returns a toned down (strongly darkened) version of the Color colour, with the dominant
	//primary colour being picked out and then a dark version of this being returned
	public static Color getTonedDownColour(Color colour)
	{
		Color darkenedColour = null;
		int darkValue = 30;

		//extract the current RGB values
		float maxValue = 0;
		int maxChannel = -1;
		float [] rgb = colour.getRGBColorComponents(null);
		for (int i = 0; i < rgb.length; i++)
		{
			if(rgb[i] > maxValue)
			{
				maxValue = rgb[i];
				maxChannel = i;
			}
		}

		switch (maxChannel)
		{
			case 0:
				darkenedColour =  new Color(darkValue, 0, 0);
				break;
			case 1:
				darkenedColour = new Color(0, darkValue, 0);
				break;
			case 2:
				darkenedColour = new Color(0, 0, darkValue);
				break;
		}

		return darkenedColour;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	//check whether we have a map that intersects with the rectangle passed in
	public static GChromoMap getSelectedMap(Rectangle intersectionRect, GMapSet gMapSet)
	{
		GChromoMap selectedMap = null;

		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in the genome

		for (GChromoMap gChromoMap : gMapSet.gMaps)
		{
			// check whether the hit falls within its current bounding rectangle
			if (gChromoMap.boundingRectangle.intersects(intersectionRect))
			{
				selectedMap = gChromoMap;
				break;
			}
		}

		return selectedMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------


	//check whether we have a map that intersects with the rectangle passed in
	public static GChromoMap getSelectedMap(Rectangle intersectionRect)
	{
		GChromoMap selectedMap = null;

		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in each genome
		for (GMapSet gMapSet : Strudel.winMain.dataContainer.gMapSets)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.intersects(intersectionRect))
				{
					selectedMap = gChromoMap;
					break;
				}
			}
		}
		return selectedMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------


	//check whether we have a map at the coordinates x and y
	public static GChromoMap getSelectedMap(LinkedList<GMapSet> gMapSetList, int x, int y)
	{
		GChromoMap selectedMap = null;

		// check whether the point x,y lies within one of the bounding rectangles of our chromosomes
		// for each chromosome in each genome
		for (GMapSet gMapSet : gMapSetList)
		{
			for (GChromoMap gChromoMap : gMapSet.gMaps)
			{
				// check whether the hit falls within its current bounding rectangle
				if (gChromoMap.boundingRectangle.contains(x, y))
				{
					selectedMap = gChromoMap;
					break;
				}
			}
		}
		return selectedMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	//check whether there is a map intersecting with a horizontal line drawn across part of the canvas at the level of  the coordinate y
	public static GChromoMap getSelectedMap(WinMain winMain, int gMapSetIndex, int y)
	{
		GChromoMap selectedMap = null;

		//the number of genomes we have loaded
		int numGenomes = Strudel.winMain.dataContainer.gMapSets.size();
		//the size of the sectors occupied by each of the genomes on the main canvas
		int interValSize = Math.round(Strudel.winMain.mainCanvas.getWidth() / numGenomes);

		// check whether a line drawn at y intersects within one of the bounding rectangles of our chromosomes
		//we can just use a rectangle a single pixel wide for this purpose so we can use the existing API for the Rectangle class
		int xLeft = gMapSetIndex * interValSize;
		Rectangle intersectLine = new Rectangle(xLeft, y, interValSize, 1);

		//now check all the chromosomes' bounding rectangles in this mapset for intersection
		for (GChromoMap gChromoMap : Strudel.winMain.dataContainer.gMapSets.get(gMapSetIndex).gMaps)
		{
			// check whether the hit falls within its current bounding rectangle
			if (gChromoMap.boundingRectangle.intersects(intersectLine))
			{
				selectedMap = gChromoMap;
				return selectedMap;
			}
		}
		return selectedMap;
	}


	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// finds out which of the genomes the current selection relates to and return its index in the list of mapsets
	public static int getSelectedSetIndex(MouseEvent e)
	{
		//the index of the mapset in the list kept by the DataContainer object
		int index = -1;
		//the number of genomes we have loaded
		int numGenomes = Strudel.winMain.dataContainer.gMapSets.size();
		//the size of the sectors occupied by each of the genomes on the main canvas
		int interValSize = Math.round(Strudel.winMain.mainCanvas.getWidth() / numGenomes);
		//where we had our mouse hit
		int xHit = e.getX();
		//now simply return the value we get by dividing the x location by the interval size, while throwing the remainder away
		index = xHit/interValSize;

		return index;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	public static void visitURL(String html)
	{
		try
		{
			if (SystemUtils.jreVersion() >= 1.6)
				visitURL6(html);
			else
				visitURL5(html);
		}
		catch (Exception e)
		{
			TaskDialog.error("Error: URL not specified or specified incorrectly", "Close");
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// Java6 method for visiting a URL
	private static void visitURL6(String html)
	throws Exception
	{
		Desktop desktop = Desktop.getDesktop();

		URI uri = new URI(html);
		desktop.browse(uri);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// Java5 (OS X only) method for visiting a URL
	private static void visitURL5(String html)
	throws Exception
	{
		// See: http://www.centerkey.com/java/browser/

		Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
		Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] {String.class});

		openURL.invoke(null, new Object[] {html});
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	public static  AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon, ActionListener actionListener, boolean enabled)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton(title);
		else
			button = new JButton(title);

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.addActionListener(actionListener);
		button.setMargin(new Insets(2, 1, 2, 1));
		button.setEnabled(enabled);

		if (SystemUtils.isMacOS())
		{
			button.putClientProperty("JButton.buttonType", "bevel");
			button.setMargin(new Insets(-2, -1, -2, -1));
		}

		return button;
	}

	public static  AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon, Action action)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton(action);
		else
			button = new JButton(action);

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(2, 1, 2, 1));

		if (SystemUtils.isMacOS())
		{
			button.putClientProperty("JButton.buttonType", "bevel");
			button.setMargin(new Insets(-2, -1, -2, -1));
		}

		return button;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/**converts the position of a feature on its map (fPos) from its original units (centiMorgans or base pairs) to a pixel
	 * position on its owning gMap, in pixels and assuming the map starts at zero pixels
	 */
	public static int relativeFPosToPixelsOnGMap(GChromoMap gMap, float fPos)
	{
		return Math.round((gMap.currentHeight / gMap.chromoMap.getStop()) * fPos);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/**
	 * converts the position of a feature on its map (fPos) from its original units (centiMorgans or base pairs) to a pixel
	 * position on the canvas, in pixels; this may or may not be within the boundaries of the currently visible part of the canvas
	 */
	public static int relativeFPosToPixelOnCanvas(GChromoMap gMap, float fPos, boolean inverting)
	{
		int fDist = Math.round((gMap.currentHeight / gMap.chromoMap.getStop()) * fPos);
		int fPosOnCanvas =   fDist + gMap.y + gMap.currentY;

		if(inverting)
			return gMap.y + gMap.currentY + gMap.currentHeight - fDist;

		return fPosOnCanvas;
	}

	/**
	 * Converts the position of a feature on its gMap (in pixels, assuming the map starts at zero) to an absolute position
	 * on the canvas, in pixels; this may or may not be within the boundaries of the currently visible part of the canvas
	 */
	// --------------------------------------------------------------------------------------------------------------------------------

	public static int pixelsOnChromoToPixelsOnCanvas(GChromoMap gMap, int fPos, boolean inverted)
	{
		int fPosOnScreen =  fPos + gMap.spaceAboveMap + gMap.currentY;

		if(inverted)
			return gMap.spaceAboveMap + gMap.currentY + gMap.currentHeight - fPos;

		return fPosOnScreen;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	//returns the vertical extent of the genome above the gMap in pixels
	//this includes the spaces between chromosomes and the chromosomes themselves
	public static int calcSpaceAboveGMap(GChromoMap gMap)
	{
//		System.out.println("gMap.index for map "+gMap.name+" = " + gMap.index);

		//how many chromosomes are above this map in the genome
		int numChromosAbove = gMap.index;
		//combined spaces between chromos
		int spacer = gMap.owningSet.chromoSpacing;
		//what is the combined height of all chromos above this one
		int totalChromoSpaceAbove = 0;
		for(int i = 0; i < numChromosAbove; i++)
		{
			GChromoMap gChromoMap = gMap.owningSet.gMaps.get(i);
			totalChromoSpaceAbove += gChromoMap.currentHeight;
		}

		return totalChromoSpaceAbove + (numChromosAbove * spacer);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static Vector<Feature> checkFeatureVisibility(GChromoMap gMap, Vector<Feature> features)
	{
		Vector<Feature> visibleFeatures = new Vector<Feature>();

		//for each feature in the array
		for (Feature feature : features)
		{
			if (feature != null)
			{
				//check whether this position is currently showing on the canvas or not
				if (checkFeatureVisibility(gMap, feature))
				{
					//if it is, add it
					visibleFeatures.add(feature);
				}
			}
		}

		return visibleFeatures;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static boolean checkFeatureVisibility(GChromoMap gMap, Feature feature)
	{
		boolean featureIsVisible = false;

		if (feature != null)
		{
			//get the relative position on the map
			float fStart = feature.getStart();
			//convert this to an absolute position on the canvas in pixels
			int pixelPos = -1;
			if(!gMap.isFullyInverted || !gMap.isPartlyInverted)
				pixelPos = relativeFPosToPixelOnCanvas(gMap, fStart, false);
			else
				pixelPos = relativeFPosToPixelOnCanvas(gMap, fStart, true);

			//check whether this position is currently showing on the canvas or not
			if (pixelPos > 0 && pixelPos < Strudel.winMain.mainCanvas.getHeight())
			{
				featureIsVisible = true;
			}
		}

		return featureIsVisible;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	//returns all links for features we have searched for by name or range
	public static LinkedList<Link> getLinksForFeatures(Vector<Feature> features)
	{
		LinkedList<Link> homologies = new LinkedList<Link>();

		//parse the strings out into the table model and populate as appropriate
		for (Feature f : features)
		{
			if (f != null)
			{
				//get all the links this feature is involved in
				//for each link
				for (Link link : f.getLinks())
				{
					//create a new entry in the homologies list
					homologies.add(link);
				}
			}
		}

		return homologies;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static void labelToHyperlink(JLabel label, String url)
	{
		final String URL = url;
		// Turns the label into a blue mouse-over clickable link to a website
		label.setForeground(Color.blue);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				Utils.visitURL(URL);
			}
		});
	}


	// --------------------------------------------------------------------------------------------------------------------------------

	//checks whether two gmapsets are next to each other, in either direction
	public static boolean areMapSetsAdjacent(GMapSet set1, GMapSet set2)
	{
		int index1 = Strudel.winMain.dataContainer.gMapSets.indexOf(set1);
		int index2 = Strudel.winMain.dataContainer.gMapSets.indexOf(set2);
		return Math.abs(index1 - index2) == 1;
	}

	// --------------------------------------------------------------------------------------------------------------------------------


	//returns the closest instance of a GChromoMap of the genome represented by ChromoMap refMap,
	//relative to GChromoMap targetGMap
	public static GChromoMap getClosestGMap(ChromoMap refMap, GChromoMap targetGMap)
	{
		GChromoMap closestMap = null;

		int targetIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(targetGMap.owningSet);
		int leastDistance = Integer.MAX_VALUE;

		for(GChromoMap refGMap : refMap.getGChromoMaps())
		{
			int refIndex = Strudel.winMain.dataContainer.gMapSets.indexOf(refGMap.owningSet);
			int dist = Math.abs(refIndex - targetIndex);

			if(dist < leastDistance)
			{
				leastDistance = dist;
				closestMap = refGMap;
			}
		}

		return closestMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static ChromoMap pickRefMapFromFeaturesInLink(Link link, GChromoMap targetGMap)
	{
		ChromoMap feat1Map = link.getFeature1().getOwningMap();
		ChromoMap feat2Map = link.getFeature2().getOwningMap();
		//we don't know which of these maps is the target one so we need to find out
		ChromoMap refMap = null;
		if(targetGMap.chromoMap == feat1Map)
			refMap = feat2Map;
		else
			refMap = feat1Map;

		return refMap;
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	//puts a list of features from the same gMapSet into the right sort order according to their layout on screen (i.e. top to bottom within the genome)
	public static LinkedList<Feature> sortFeaturesWithinGMapset(LinkedList<Feature> features, GMapSet gMapSet)
	{
		LinkedList<Feature> sortedFeatures = new LinkedList<Feature>();
		TreeMap<GChromoMap, LinkedList<Feature>> lookup = new TreeMap<GChromoMap, LinkedList<Feature>>();

		//sort the features into their maps first
		for(Feature f : features)
		{
			addFeatureToLookup( gMapSet, f, lookup);
		}
		//then sort each feature list from each map in its natural (ascending) order
		for(GChromoMap map : lookup.keySet())
		{
			Collections.sort(lookup.get(map));
			//put the features from each map into the sorted list in order of the maps within the mapset
			sortedFeatures.addAll(lookup.get(map));
		}

		return sortedFeatures;
	}
	//------------------------------------------------------------------------------------------------------------------------------------

	private static void addFeatureToLookup(GMapSet gMapSet, Feature feature, TreeMap<GChromoMap, LinkedList<Feature>> lookup)
	{
		GChromoMap chromoMap = getGMapByNameAndGMapset(feature.getOwningMap().getName(), gMapSet);
		//check whether its gMap is listed here
		if(!lookup.keySet().contains(chromoMap))
		{
			//if not, add it to the lookup
			lookup.put(chromoMap, new LinkedList<Feature>());
		}
		//if the feature is not present in the feature list for this gmap, add it
		if(!lookup.get(chromoMap).contains(feature))
			lookup.get(chromoMap).add(feature);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static void sendFeedback()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.mail(new URI("mailto:strudel@scri.ac.uk?subject=Strudel%20Feedback"));
		}
		catch (Exception e) { }
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public static void repositionDraggedMap(MouseEvent e)
	{
		Strudel.winMain.mainCanvas.dragMap(e);
		GMapSet selectedSet = Strudel.winMain.fatController.draggedMap.owningSet;

		//look for the closest map
		GChromoMap selectedMap = Utils.getSelectedMap(Strudel.winMain.mainCanvas.ghostRect, selectedSet);
		if(selectedMap != null)
		{
			//the index of the map below which we want to insert the dragged map
			int insertionIndex = -1;
			//check where we overlap -- could be top or bottom
			if(selectedMap.y <= Strudel.winMain.mainCanvas.ghostRect.getY())
				insertionIndex = selectedMap.index;
			else
				insertionIndex = selectedMap.index -1;

			//shuffle the indexes of all maps in this genome down by one, starting from the insertion index
			for(GChromoMap gMap : selectedSet.gMaps)
			{
				if(gMap.index > insertionIndex && gMap.index < selectedMap.index)
					gMap.index += 1;
			}

			//then set the new index for the dragged map
			Strudel.winMain.fatController.draggedMap.index = insertionIndex +1;

			//now sort the maps in the selected genome by their new indices
			Collections.sort(selectedSet.gMaps);

			//repaint
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}

		//reset everything
		Strudel.winMain.fatController.draggedMap = null;
		Strudel.winMain.fatController.draggedMapYOffset = 0;
	}
}
