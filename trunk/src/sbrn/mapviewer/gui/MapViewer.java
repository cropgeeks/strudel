package sbrn.mapviewer.gui;

import java.awt.event.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;

import sbrn.mapviewer.gui.components.*;
import scri.commons.gui.*;
import scri.commons.*;

import apple.dts.samplecode.osxadapter.*;

public class MapViewer
{
	
	/*set logging level to one of     
	    * SEVERE (highest value)
	    * WARNING
	    * INFO
	    * CONFIG
	    * FINE
	    * FINER
	    * FINEST (lowest value) 
	    * ALL
	    */
	private static Level logLevel = Level.FINEST;
	public static Logger logger = Logger.getLogger("sbrn.mapviewer");
	
	private static File prefsFile = new File(System.getProperty("user.home"), ".mapviewer.xml");
	private static Prefs prefs = new Prefs();
	public static WinMain winMain;	

	public static void main(String[] args)
	{
		//logging stuff		
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(FormatterFactory.getBasicFormatter());
		consoleHandler.setLevel(logLevel);
		logger.setLevel(logLevel);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
		
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Map Viewer");

		prefs.loadPreferences(prefsFile, Prefs.class);
		Install4j.doStartUpCheck();

		new MapViewer();

	}

	MapViewer()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));

			// Use the office look for Windows (but not for Vista)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista())
				UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");

			// TODO: Check whether this is needed on all OSs or not
			UIManager.put("Slider.focus", new java.awt.Color(0, 0, 0, 0));

			// Keep Apple happy...
			if (SystemUtils.isMacOS())
				handleOSXStupidities();

//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) {}

		try
		{
			winMain = new WinMain();

			TaskDialog.initialize(winMain, "MapViewer");

			winMain.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					prefs.isFirstRun = false;
					prefs.savePreferences(prefsFile, Prefs.class);

					System.exit(0);
				}
			});

			winMain.setVisible(true);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// --------------------------------------------------
	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		try
		{
			// Register handlers to deal with the System menu about/quit options

//			OSXAdapter.setPreferencesHandler(this,
//				getClass().getDeclaredMethod("osxPreferences", (Class[])null));

//			OSXAdapter.setAboutHandler(this,
//				getClass().getDeclaredMethod("osxAbout", (Class[])null));

			OSXAdapter.setQuitHandler(this,
				getClass().getDeclaredMethod("osxShutdown", (Class[])null));

			// Dock the menu bar at the top of the screen
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (Exception e) {}
	}

	/** "Preferences" on the OS X system menu. */
	public void osxPreferences()
	{
//		winMain.mHelp.helpPrefs();
	}

	/** "About Map Viewer" on the OS X system menu. */
	public void osxAbout()
	{
//		winMain.mHelp.helpAbout();
	}

	/** "Quit Map Viewer" on the OS X system menu. */
	public boolean osxShutdown()
	{
		// Put any additional quit handling code here (if need be)

		return true;
	}
}