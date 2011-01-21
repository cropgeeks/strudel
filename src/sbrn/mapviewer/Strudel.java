package sbrn.mapviewer;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;
import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.*;
import sbrn.mapviewer.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;
import scri.commons.*;

import apple.dts.samplecode.osxadapter.*;

public class Strudel
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

	private static File prefsFile = getPrefsFile();
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

		Icons.initialize("/res/icons", ".png");

		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Strudel");

		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);

		if (args.length > 0)
			initialFile = args[0];

		Install4j.doStartUpCheck();

		new Strudel();

	}

	Strudel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("TextArea.font", UIManager.get("TextField.font"));

			// Use the office look for Windows (but not for Vista or 7)
			if (SystemUtils.isWindows() && !SystemUtils.isWindowsVista() && !SystemUtils.isWindows7())
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
				@Override
				public void windowClosing(WindowEvent e)
				{
					exit();
				}

				@Override
				public void windowOpened(WindowEvent e)
				{
					// Do we want to open an initial project?
					if (initialFile != null)
					{
						winMain.fatController.loadOwnData = true;
						DataLoadUtils.loadDataInThread(initialFile, true);
					}
				}

			});


			winMain.addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					if (dataLoaded)
					{
						winMain.mainCanvas.updateCanvas(true);
						winMain.fatController.initialisePositionArrays();
					}
				}

			});

			//set up the colour scheme the user had last selected
			setColours();

			winMain.setVisible(true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void exit()
	{
		prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);

		System.exit(0);
	}

	private void setColours()
	{
		DefaultColourScheme defScheme = new DefaultColourScheme();
		DefaultColourScheme printScheme = new PrintColourScheme();

		if (Prefs.selectedColourScheme.equals(defScheme.toString()))
		{
			defScheme.setColours();
		}
		else if (Prefs.selectedColourScheme.equals(printScheme.toString()))
		{
			printScheme.setColours();
		}
	}

	private static File getPrefsFile()
	{
		// Ensure the .scri-bioinf folder exists
		File fldr = new File(System.getProperty("user.home"), ".scri-bioinf");
		fldr.mkdirs();

		// This is the file we really want
		File file = new File(fldr, "strudel.xml");
		// So if it exists, just use it
		if (file.exists())
			return file;

		// If not, see if the "old" (pre 21/06/2010) file is available
		File old = new File(System.getProperty("user.home"), ".strudel.xml");
		if (old.exists())
			try { FileUtils.copyFile(old, file, true); }
			catch (IOException e) {}

		return file;
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