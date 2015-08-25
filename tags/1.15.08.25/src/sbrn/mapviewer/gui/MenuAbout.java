package sbrn.mapviewer.gui;

import sbrn.mapviewer.Constants;
import sbrn.mapviewer.Strudel;

/**
 * Stores the methods which carry out the actions which are fired whenever the
 * About menu is interacted with.
 */
public class MenuAbout
{
	/**
	 * Opens the help webpage.
	 */
	public void help()
	{
		String url = Constants.strudelManualPage;

		Utils.visitURL(url);
	}

	/**
	 * Opens the about box for Strudel.
	 */
	public void about()
	{
		Strudel.winMain.aboutDialog.setLocationRelativeTo(Strudel.winMain);
		Strudel.winMain.aboutDialog.setVisible(true);
	}
}
