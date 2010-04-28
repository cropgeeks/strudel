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
	public static AbstractAction saveTableData;
	public static AbstractAction saveMapOrder;

	// Explore menu actions
	public static AbstractAction findFeature;
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
	public static AbstractAction hideUnlinkedFeatures;
	public static AbstractAction configureViewSettings;
	public static AbstractAction scaleChromosomes;

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

		saveTableData = new AbstractAction("Save Results Table", getIcon("SAVE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.saveTableData();
			}
		};

		saveMapOrder = new AbstractAction("Save Map Order", getIcon("SAVE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mFile.saveMapOrder();
			}
		};

		findFeature = new AbstractAction("Find Feature By Name", getIcon("FIND")) {
			public void actionPerformed(ActionEvent e) {
				winMain.mExplore.findFeature();
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

		hideUnlinkedFeatures = new AbstractAction("Hide unlinked features") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.hideUnlinkedFeatures();
			}
		};

		configureViewSettings = new AbstractAction("Configure view settings") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.configureViewSettings();
			}
		};

		scaleChromosomes = new AbstractAction("Scale chromosomes") {
			public void actionPerformed(ActionEvent e) {
				winMain.mView.scaleChromosomes();
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
		hideUnlinkedFeatures.putValue(SELECTED_KEY, Prefs.hideUnlinkedFeatures);
		scaleChromosomes.putValue(SELECTED_KEY, Prefs.scaleChromosByRelativeSize);

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
		saveTableData.setEnabled(false);
		saveMapOrder.setEnabled(false);

		findFeature.setEnabled(false);
		exploreRange.setEnabled(false);

		showOverview.setEnabled(false);
		customiseColours.setEnabled(false);
		configureDatasets.setEnabled(false);
		reset.setEnabled(false);
		showHint.setEnabled(false);
		antialiasedDraw.setEnabled(false);
		filterLinks.setEnabled(false);
		hideUnlinkedFeatures.setEnabled(false);
		showDistanceMarkers.setEnabled(false);
		scaleChromosomes.setEnabled(false);
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
		saveMapOrder.setEnabled(true);

		findFeature.setEnabled(true);
		exploreRange.setEnabled(true);

		showOverview.setEnabled(true);
		customiseColours.setEnabled(true);
		configureDatasets.setEnabled(true);
		reset.setEnabled(true);
		showHint.setEnabled(true);
		antialiasedDraw.setEnabled(true);
		filterLinks.setEnabled(true);
		hideUnlinkedFeatures.setEnabled(true);
		showDistanceMarkers.setEnabled(true);
		scaleChromosomes.setEnabled(true);
		linkCurved.setEnabled(true);
		linkStraight.setEnabled(true);
		linkAngled.setEnabled(true);

		help.setEnabled(true);
		about.setEnabled(true);

		viewSettings.setEnabled(true);
		linkShape.setEnabled(true);
	}
}
