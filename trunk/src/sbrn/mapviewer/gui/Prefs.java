package sbrn.mapviewer.gui;

import java.awt.Color;
import java.util.*;
import sbrn.mapviewer.*;
import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Unique user ID for this user
	public static String ID = SystemUtils.createGUID(32);

	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 900;
	public static int guiWinMainHeight = 650;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// The width, height, location and visibility of the overview dialog
	public static int guiOverviewWidth = 300;
	public static int guiOverviewHeight = 275;
	public static int guiOverviewX = 0;
	public static int guiOverviewY = 0;
	public static boolean guiOverviewVisible = true;

	//a boolean indicating whether we need to show the "max zoom level reached " taskdialog when user has pan zoomed to the max
	public static boolean showMaxZoomLevelMessage = true;

	//this boolean is set by the user through a button and indicates a global preference for antialiased or plain darwing styles
	public static boolean userPrefAntialias = false;

	//do we want links to be drawn whether their orginating feature is currently visible on the canvas or not?
	public static boolean drawOnlyLinksToVisibleFeatures = false;

	//do we want to show the distance markers
	public static boolean showDistanceMarkers = false;

	//this determines whether we display all features or just those that have links associated with them
	public static boolean hideUnlinkedFeatures = false;

	//show the hints panel
	public static boolean showHintPanel = true;

	//the shape of the links -- curved, straight or angled
	public static int linkShape = Constants.LINKTYPE_CURVED;

	//the number of the last version released
	public static String lastVersion = null;

	// A list of previously accessed documents
	public static String[] guiRecentDocs = new String[8];

	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentDocument(String filePath)
	{
		LinkedList<String> list = new LinkedList<String>();
		for (String file: guiRecentDocs)
			list.add(file);

		if (list.contains(filePath))
			list.remove(filePath);

		list.addFirst(filePath);

		for (int i = 0; i < guiRecentDocs.length; i++)
			guiRecentDocs[i] = list.get(i);
	}

	//Custom colours for the main colour scheme
	public static Color strudelGenomeColour = new Color(0, 50, 155);//blue
	public static Color strudelOutlineColour = Color.white;
	public static Color strudelInvertedChromosomeColour = new Color(0,36,18);//green
	public static Color strudelInvertedChromosomeHighlightColour = new Color(0,59,43);//light green
	public static Color strudelChromosomeHighlightColour = new Color(80,0,0); // red
	public static Color strudelLinkColour = new Color(120,120,120);
	public static Color strudelStrongEmphasisLinkColour = new Color(130,0, 0);
	public static Color strudelMildEmphasisLinkColour =  Color.WHITE;
	public static Color strudelChromosomeIndexColour = Color.white;
	public static Color strudelFeatureColour = new Color(180,180,180);
	public static Color strudelHighlightedFeatureColour = strudelStrongEmphasisLinkColour;
	public static Color strudelFeatureLabelColour = Color.BLACK;
	public static Color strudelHighlightedFeatureLabelColour = Color.WHITE;
	public static Color strudelHighlightedFeatureLabelBackgroundColour = strudelStrongEmphasisLinkColour;
	public static Color strudelBackgroundGradientStartColour = Color.black;
	public static Color strudelBackgroundGradientEndColour = new Color(110, 110, 110);

	//Custome colours for the print colour scheme
	public static Color printGenomeColour = new Color(0, 50, 155);//blue
	public static Color printOutlineColour = Color.white;
	public static Color printInvertedChromosomeColour = new Color(0,36,18);//green
	public static Color printInvertedChromosomeHighlightColour = new Color(0,59,43);//light green
	public static Color printChromosomeHighlightColour = new Color(80,0,0); // red
	public static Color printLinkColour = new Color(120,120,120);
	public static Color printStrongEmphasisLinkColour = new Color(130,0, 0);
	public static Color printMildEmphasisLinkColour =  Color.gray;
	public static Color printChromosomeIndexColour = Color.black;
	public static Color printFeatureColour = new Color(180,180,180);
	public static Color printHighlightedFeatureColour = strudelStrongEmphasisLinkColour;
	public static Color printFeatureLabelColour = Color.BLACK;
	public static Color printHighlightedFeatureLabelColour = Color.gray;
	public static Color printHighlightedFeatureLabelBackgroundColour = strudelStrongEmphasisLinkColour;
	public static Color printBackgroundGradientStartColour = Color.white;
	public static Color printBackgroundGradientEndColour = Color.white;

	//Stores the currently selected colour scheme
	public static String selectedColourScheme = "Strudel";
}