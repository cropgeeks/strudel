package sbrn.mapviewer.gui;

import java.io.*;
import java.net.*;
import java.util.*;

import scri.commons.gui.*;

class Install4j
{
	public static String VERSION = "0.0";
	public static String ID = SystemUtils.createGUID(32);

	static void pingServer()
	{
		Runnable r = new Runnable() {
			public void run()
			{
				try
				{
					// Safely encode the URL's parameters
					String id = URLEncoder.encode(ID, "UTF-8");
					String version = URLEncoder.encode(VERSION, "UTF-8");
					String locale = URLEncoder.encode("" + Locale.getDefault(), "UTF-8");
					String os = URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
					String user = URLEncoder.encode(System.getProperty("user.name"), "UTF-8");

					String addr = "http://bioinf.scri.ac.uk/cgi-bin/mapviewer/mapviewer.cgi"
						+ "?id=" + id
						+ "&version=" + version
						+ "&locale=" + locale
						+ "&os=" + os;

					// We DO NOT log usernames from non-SCRI addresses
					if (SystemUtils.isSCRIUser())
						addr += "&user=" + user;

					// Nudges the cgi script to log the run
					URL url = new URL(addr);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					c.getResponseCode();
					c.disconnect();
				}
				catch (Exception e) { System.out.println(e);}
			}
		};

		// We run this in a separate thread to avoid any waits due to lack of an
		// internet connection or the server being non-responsive
		new Thread(r).start();
	}
}