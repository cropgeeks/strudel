package sbrn.mapviewer.gui;

import java.util.*;
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
//	public static boolean userPrefAntialias = false;

	//do we links to be drawn whether their orginating feature is currently visible on the canvas or not?
	public static boolean drawOnlyLinksToVisibleFeatures = false;

	//do we want to show the distance markers
	public static boolean showDistanceMarkers = false;

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

}