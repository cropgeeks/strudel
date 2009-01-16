package sbrn.mapviewer.gui;

import java.awt.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	// Unique user ID for this user
	public static String ID = SystemUtils.createGUID(32);

	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 800;
	public static int guiWinMainHeight = 600;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// The width, height, location and visibility of the overview dialog
	public static int guiOverviewWidth = 300;
	public static int guiOverviewHeight = 275;
	public static int guiOverviewX = 0;
	public static int guiOverviewY = 0;
	public static boolean guiOverviewVisible = true;
		
}