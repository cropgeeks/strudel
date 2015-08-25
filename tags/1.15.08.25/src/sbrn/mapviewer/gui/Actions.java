package sbrn.mapviewer.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import sbrn.mapviewer.Constants;
import sbrn.mapviewer.gui.components.WinMain;
import scri.commons.gui.Icons;
import scri.commons.gui.SystemUtils;
import static javax.swing.Action.*;

public class Actions
{
	private final WinMain winMain;

	// File menu actions
	public static AbstractAction loadData;
	public static AbstractAction loadExample;
	public static AbstractAction exportImage;
	public static AbstractAction saveResults;
	public static AbstractAction exit;

	// Explore menu actions
	public static AbstractAction showTable;
	public static AbstractAction exploreRange;

	// View menu actions
	public static AbstractAction showOverview;
	public static AbstractAction customiseColours;
	public static AbstractAction configureDatasets;
	public static AbstractAction reset;
	// View Settings menu actions
	public static AbstractAction viewSettings;
	public static AbstractAction showHint;
	public static AbstractAction antialiasedDraw;
	public static AbstractAction filterLinks;
	public static AbstractAction showDistanceMarkers;
	public static AbstractAction showFullFeatureInfo;
	// Link shape menu actions
	public static AbstractAction linkShape;
	public static AbstractAction linkCurved;
	public static AbstractAction linkStraight;
	public static AbstractAction linkAngled;

	// About menu actions
	public static AbstractAction help;
	public static AbstractAction about;

	public Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();
		setInitialStates();

		openedNoData();
	}

	/**
	 * Retrieves an icon from the resources folder which has the specified file
	 * name.
	 */
	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = Icons.getIcon(name);

		if (SystemUtils.isMacOS())
			return null;
		else
			return icon;
	}

	/**
	 * Creates the action objects for each action in the application, associating
	 * each action with a method which carries out that action.
	 */
	private void createActions()
	{
		loadData = new AbstractAction("Load Data", getIcon("FILEOPEN")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.loadData();
			}
		};

		loadExample = new AbstractAction("Load Example") {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.loadExample();
			}
		};

		exportImage = new AbstractAction("Export Image", getIcon("EXPORTIMAGE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.exportImage();
			}
		};

		saveResults = new AbstractAction("Save Results Table", getIcon("SAVE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.saveResults();
			}
		};

		exit = new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.exit();
			}
		};

		showTable = new AbstractAction("Show Filterable Table", getIcon("FIND")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mExplore.showTable();
			}
		};

		exploreRange = new AbstractAction("Explore Range", getIcon("RANGE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mExplore.exploreRange();
			}
		};

		showOverview = new AbstractAction("Toggle overview dialog", getIcon("OVERVIEW")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.showOverview();
			}
		};

		customiseColours = new AbstractAction("Customise colours", getIcon("COLOURS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.customiseColours();
			}
		};

		showHint = new AbstractAction("Show hint panel") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.showHint();
			}
		};

		antialiasedDraw = new AbstractAction("Use antialiased drawing") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.antialiasedDraw();
			}
		};

		filterLinks = new AbstractAction("Filter out links to offscreen features") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.filterLinks();
			}
		};

		showDistanceMarkers = new AbstractAction("Show distance markers") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.showDistanceMarkers();
			}
		};
		
		showFullFeatureInfo = new AbstractAction("Show full feature info on mouseover") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.showFullFeatureInfoOnMouseover();
			}
		};

		linkCurved = new AbstractAction("Curved") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.linkShape(Constants.LINKTYPE_CURVED);
			}
		};

		linkStraight = new AbstractAction("Straight") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.linkShape(Constants.LINKTYPE_STRAIGHT);
			}
		};

		linkAngled = new AbstractAction("Angled") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.linkShape(Constants.LINKTYPE_ANGLED);
			}
		};

		help = new AbstractAction("Help") {
			public void actionPerformed(ActionEvent e) {
				winMain.mAbout.help();
			}
		};

		about = new AbstractAction("About Strudel") {
			public void actionPerformed(ActionEvent e) {
				winMain.mAbout.about();
			}
		};

		viewSettings = new AbstractAction("View Settings") {
			public void actionPerformed(ActionEvent e) {}
		};

		linkShape = new AbstractAction("Link Shape") {
			public void actionPerformed(ActionEvent e) {}
		};

		reset = new AbstractAction("Reset", getIcon("RESET")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.reset();
			}
		};

		configureDatasets = new AbstractAction("Configure datasets", getIcon("CONFIGURE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.configureDatasets();
			}
		};
	}

	/**
	 * Initialise the states of various check box menu items.
	 */
	private static void setInitialStates()
	{
		showHint.putValue(SELECTED_KEY, Prefs.showHintPanel);
		antialiasedDraw.putValue(SELECTED_KEY, Prefs.userPrefAntialias);
		showDistanceMarkers.putValue(SELECTED_KEY, Prefs.showDistanceMarkers);
		filterLinks.putValue(SELECTED_KEY, Prefs.drawOnlyLinksToVisibleFeatures);
		showFullFeatureInfo.putValue(SELECTED_KEY, Prefs.showFullFeatureInfoOnMouseOver);

		linkCurved.putValue(SELECTED_KEY, Prefs.linkShape == Constants.LINKTYPE_CURVED);
		linkStraight.putValue(SELECTED_KEY, Prefs.linkShape == Constants.LINKTYPE_STRAIGHT);
		linkAngled.putValue(SELECTED_KEY, Prefs.linkShape == Constants.LINKTYPE_ANGLED);
	}

	/**
	 * The states actions should be in upon application load.
	 */
	public static void openedNoData()
	{
		loadData.setEnabled(true);
		loadExample.setEnabled(true);
		exportImage.setEnabled(false);
		saveResults.setEnabled(false);

		showTable.setEnabled(false);
		exploreRange.setEnabled(false);

		showOverview.setEnabled(false);
		customiseColours.setEnabled(false);
		configureDatasets.setEnabled(false);
		reset.setEnabled(false);
		showHint.setEnabled(false);
		antialiasedDraw.setEnabled(false);
		filterLinks.setEnabled(false);
		showFullFeatureInfo.setEnabled(false);
		showDistanceMarkers.setEnabled(false);
		linkCurved.setEnabled(false);
		linkStraight.setEnabled(false);
		linkAngled.setEnabled(false);

		help.setEnabled(true);
		about.setEnabled(true);

		viewSettings.setEnabled(false);
		linkShape.setEnabled(false);
	}

	/**
	 * The states actions should be in after data has been loaded.
	 */
	public static void openedData()
	{
		loadData.setEnabled(true);
		loadExample.setEnabled(true);
		exportImage.setEnabled(true);

		showTable.setEnabled(true);
		exploreRange.setEnabled(true);

		showOverview.setEnabled(true);
		customiseColours.setEnabled(true);
		configureDatasets.setEnabled(true);
		reset.setEnabled(true);
		showHint.setEnabled(true);
		antialiasedDraw.setEnabled(true);
		filterLinks.setEnabled(true);
		showFullFeatureInfo.setEnabled(true);
		showDistanceMarkers.setEnabled(true);
		linkCurved.setEnabled(true);
		linkStraight.setEnabled(true);
		linkAngled.setEnabled(true);

		help.setEnabled(true);
		about.setEnabled(true);

		viewSettings.setEnabled(true);
		linkShape.setEnabled(true);
	}
}
