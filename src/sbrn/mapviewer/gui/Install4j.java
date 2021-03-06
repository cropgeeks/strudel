package sbrn.mapviewer.gui;

import java.io.*;
import java.net.*;
import java.util.*;

import com.install4j.api.launcher.*;
import com.install4j.api.update.*;

import sbrn.mapviewer.*;
import scri.commons.gui.*;

public class Install4j
{
	private static String URL = Constants.strudelHomePage + "installers/updates.xml";

	public static String VERSION;
	public static boolean displayUpdate = false;

	/**
	 * install4j update check. This will only work when running under the full
	 * install4j environment, so expect exceptions everywhere else
	 */
	public static void doStartUpCheck()
	{
		getVersion();
		pingServer();

		try
		{
			UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);

			if (UpdateScheduleRegistry.checkAndReset() == false)
				return;

			UpdateDescriptor ud = UpdateChecker.getUpdateDescriptor(URL, ApplicationDisplayMode.GUI);

			if (ud.getPossibleUpdateEntry() != null)
				checkForUpdate(true);
		}
		catch (Exception e) {}
	}

	/**
	 * Shows the install4j updater app to check for updates and download/install
	 * any that are found.
	 */
	static void checkForUpdate(boolean block)
	{
		try
		{
			// 58 is the custom ID of the updater application in the associated
			// install4j project file for MapViewer
			ApplicationLauncher.launchApplication("58", null, block, null);
		}
		catch (IOException e) {}
	}

	private static void getVersion()
	{
		// Attempt to get the version string from the jar's manifest
		VERSION = Strudel.class.getPackage().getImplementationVersion();
		
		if(VERSION == null)
			VERSION = "x.xx.xx.xx";
	}

	static void pingServer()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					// Safely encode the URL's parameters
					String id = URLEncoder.encode(Prefs.ID, "UTF-8");
					String version = URLEncoder.encode(VERSION, "UTF-8");
					String locale = URLEncoder.encode("" + Locale.getDefault(), "UTF-8");
					String os = URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
					String user = URLEncoder.encode(System.getProperty("user.name"), "UTF-8");

					String addr = "http://bioinf.scri.ac.uk/cgi-bin/strudel/strudel.cgi"
						+ "?id=" + id
						+ "&version=" + version
						+ "&locale=" + locale
						+ "&os=" + os;

					// We DO NOT log usernames from non-SCRI addresses
					if (SystemUtils.isSCRIUser())
						addr += "&user=" + user;

					// Nudges the cgi script to log the run
					URL url = new URL(addr);
					try
					{
						HttpURLConnection c = (HttpURLConnection) url.openConnection();

						c.getResponseCode();
						c.disconnect();
					}
					catch (Exception ukhe)
					{
					}
				}
				catch (Exception e) { e.printStackTrace();}
			}
		};

		// We run this in a separate thread to avoid any waits due to lack of an
		// internet connection or the server being non-responsive
		new Thread(r).start();
	}
}