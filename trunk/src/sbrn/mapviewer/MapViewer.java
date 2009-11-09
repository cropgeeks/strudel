package sbrn.mapviewer;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.io.*;
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
	private static Level logLevel = Level.WARNING;
	public static Logger logger = Logger.getLogger("sbrn.strudel");
	
	private static File prefsFile = new File(System.getProperty("user.home"), ".strudel.xml");
	private static Prefs prefs = new Prefs();
	public static WinMain winMain;	
	
	// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
	public static int ctrlMenuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	//true when data is loaded
	public static boolean dataLoaded = false;
	
	// Optional path to a file to be loaded when app opens
	public static String initialFile = null;


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
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Strudel");

		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);
		Install4j.doStartUpCheck();
		
		if (args.length == 1)
		{
			initialFile = args[0].trim();
			System.out.println("initialFile = " + initialFile);
		}

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
		}
		catch (Exception e) {}

		try
		{
			winMain = new WinMain();

			TaskDialog.initialize(winMain, "Strudel");

			winMain.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					prefs.isFirstRun = false;
					prefs.savePreferences(prefsFile, Prefs.class);

					System.exit(0);
				}
				
				public void windowOpened(WindowEvent e)
				{
					// Do we want to open an initial project?
					if (initialFile != null)
					{
						winMain.fatController.loadOwnData = true;
						DataLoadUtils.loadDataInThread(initialFile, true);
					}
					
					//if the version has been updated, go to the website and get the update info
					if (Install4j.displayUpdate)
						Utils.visitURL(Constants.strudelHomePage + "whatsnew.shtml");
				}

			});
			
			
			winMain.addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e) 
				{
					if (dataLoaded)
					{
						winMain.mainCanvas.updateCanvas(true);
						winMain.fatController.initialisePositionArrays();
					}					
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