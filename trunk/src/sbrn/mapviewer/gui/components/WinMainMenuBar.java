package sbrn.mapviewer.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.*;

import sbrn.mapviewer.Strudel;


import sbrn.mapviewer.gui.*;

public class WinMainMenuBar extends JMenuBar
{
	private WinMain winMain;

	// The components of the file menu
	private JMenu mFile;
	private JMenu mRecentFiles;
	private JMenuItem mLoadData;
	private JMenuItem mLoadExample;
	private JMenuItem mExportImage;
	private JMenuItem mSaveResults;

	// The components of the explore menu
	private JMenu mExplore;
	private JMenuItem mfindFeature;
	private JMenuItem mExploreRange;

	// The components of the view menu
	private JMenu mView;
	private JMenuItem mShowOverview;
	private JMenuItem mColourChooser;
	private JMenuItem mConfigureDatasets;
	private JMenuItem mReset;
	// The components of the view settings sub-menu
	private JMenu mViewSettings;
	private JMenuItem mShowHint;
	private JMenuItem mAntialiasedDraw;
	private JMenuItem mFilterLinks;
	private JMenuItem mShowDistanceMarkers;
	// The components of the link shape sub-menu
	private JMenu mLinkShape;
	private JMenuItem mCurved;
	private JMenuItem mStraight;
	private JMenuItem mAngled;

	// The componenets of the about menu
	private JMenu mAbout;
	private JMenuItem mHelp;
	private JMenuItem mAboutStrudel;

	WinMainMenuBar(WinMain winMain)
	{
		this.winMain = winMain;

		new Actions(winMain);

		createFileMenu();
		createViewMenu();
		createExploreMenu();
		createAboutMenu();
	}

	/**
	 * Create the File menu, then adds the appropriate menu items to it - linked
	 * to the appropriate action.
	 */
	private void createFileMenu()
	{
		mFile = new JMenu("File");
		mFile.setMnemonic('F');

		mLoadData = getItem(Actions.loadData, "Load Data", KeyEvent.VK_O, Strudel.ctrlMenuShortcut);
		mLoadExample = getItem(Actions.loadExample, "Load Example", KeyEvent.VK_L, Strudel.ctrlMenuShortcut);
		mExportImage = getItem(Actions.exportImage, "Export Image", KeyEvent.VK_E, Strudel.ctrlMenuShortcut);
		mSaveResults = getItem(Actions.saveResults, "Save Results", KeyEvent.VK_S, Strudel.ctrlMenuShortcut);

		mRecentFiles = new JMenu("Recent Files");
		createRecentMenu();

		mFile.add(mLoadData);
		mFile.add(mLoadExample);
		mFile.addSeparator();
		mFile.add(mSaveResults);
		mFile.add(mExportImage);
		mFile.addSeparator();
		mFile.add(mRecentFiles);

		add(mFile);
	}

	/**
	 * Create the explore menu, then adds the appropriate menu items to it - linked
	 * to the appropriate action.
	 */
	private void createExploreMenu()
	{
		mExplore = new JMenu("Explore");
		mExplore.setMnemonic('E');
		
		mfindFeature = getItem(Actions.findFeature, "Find features by name", KeyEvent.VK_F, Strudel.ctrlMenuShortcut);
		mExploreRange = getItem(Actions.exploreRange, "Explore range", KeyEvent.VK_R, Strudel.ctrlMenuShortcut);

		mExplore.add(mfindFeature);
		mExplore.add(mExploreRange);
		add(mExplore);
	}

	/**
	 * Create the view menu, then adds the appropriate menu items to it - linked
	 * to the appropriate action.
	 */
	private void createViewMenu()
	{
		mView = new JMenu("View");
		mView.setMnemonic('V');

		mShowOverview = getItem(Actions.showOverview, "Show overview", KeyEvent.VK_F7, 0);
		mColourChooser = getItem(Actions.customiseColours, "Customise colours", KeyEvent.VK_C, Strudel.ctrlMenuShortcut);
		mConfigureDatasets = getItem(Actions.configureDatasets, "Configure datasets", 0, 0);
		mReset = getItem(Actions.reset, "Reset", 0, 0);

		mViewSettings = new JMenu(Actions.viewSettings);
		mViewSettings.setMnemonic('S');

		mShowHint = getCheckedItem(Actions.showHint, "Show hint", KeyEvent.VK_H, Strudel.ctrlMenuShortcut);
		mAntialiasedDraw = getCheckedItem(Actions.antialiasedDraw, "Draw antialiased", KeyEvent.VK_A, Strudel.ctrlMenuShortcut);
		mFilterLinks = getCheckedItem(Actions.filterLinks, "Filter links", 0, 0);
		mShowDistanceMarkers = getCheckedItem(Actions.showDistanceMarkers, "Show distance markers", KeyEvent.VK_D, Strudel.ctrlMenuShortcut);

		mLinkShape = new JMenu(Actions.linkShape);
		mLinkShape.setMnemonic('L');
		
		mCurved = getCheckedItem(Actions.linkCurved, "Curved", 0, Strudel.ctrlMenuShortcut);
		mStraight = getCheckedItem(Actions.linkStraight, "Straight", 0, Strudel.ctrlMenuShortcut);
		mAngled = getCheckedItem(Actions.linkAngled, "Angled", 0, Strudel.ctrlMenuShortcut);

		mView.add(mShowOverview);
		mView.addSeparator();
		mView.add(mColourChooser);
		mView.add(mConfigureDatasets);
		mView.addSeparator();
		mView.add(mReset);
		mView.addSeparator();
		mView.add(mViewSettings);
		mViewSettings.add(mShowHint);
		mViewSettings.add(mAntialiasedDraw);
		mViewSettings.add(mFilterLinks);
		mViewSettings.add(mShowDistanceMarkers);
		mView.add(mLinkShape);

		ButtonGroup grp = new ButtonGroup();
		grp.add(mCurved);
		grp.add(mStraight);
		grp.add(mAngled);

		mLinkShape.add(mCurved);
		mLinkShape.add(mStraight);
		mLinkShape.add(mAngled);
		add(mView);
	}

	/**
	 * Create the about menu, then adds the appropriate menu items to it - linked
	 * to the appropriate action.
	 */
	private void createAboutMenu()
	{
		mAbout = new JMenu("Help");
		mAbout.setMnemonic('H');

		mHelp = getItem(Actions.help, "Online help", KeyEvent.VK_F1, 0);
		mAboutStrudel = getItem(Actions.about, "About Strudel", 0, 0);

		mAbout.add(mHelp);
		mAbout.add(mAboutStrudel);
		add(mAbout);
	}

	/**
	 * Creates a new menu item using the given action, with the given text and
	 * keyboad shortcut.
	 */
	public static JMenuItem getItem(Action action, String text, int keymask, int modifiers)
	{
		JMenuItem item = new JMenuItem(action);
		item.setText(text);
		item.setMnemonic(keymask);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	/**
	 * Creates a new checkbox menu item using the given action, with the given text and
	 * keyboad shortcut.
	 */
	public static JCheckBoxMenuItem getCheckedItem(Action action, String key, int keymask, int modifiers)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		item.setText(key);

		if (keymask != 0)
			item.setAccelerator(KeyStroke.getKeyStroke(keymask, modifiers));

		return item;
	}

	// Maintains and creates the Recent Projects file menu, adding new entries
	// as previously unseen projects are opened or saved, and ensuring that:
	//   a) the most recently accessed file is always at the start of the list
	//   b) the list never grows bigger than four entries
	void createRecentMenu()
	{
		// The menu can then be built up, one item per entry
		mRecentFiles.removeAll();

		int vk = 0;
		for (final String entry: Prefs.guiRecentDocs)
		{
			if (entry != null && !entry.equals(" "))
			{
				JMenuItem item = new JMenuItem((++vk) + " " + entry);
				item.setMnemonic(KeyEvent.VK_0 + vk);
				item.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						winMain.mFile.openFile(new File(entry));
					}
				});

				mRecentFiles.add(item);
			}
		}

		mRecentFiles.setEnabled(mRecentFiles.getItemCount() > 0);
	}

	public JMenuItem getMShowHint()
		{	return mShowHint;	}

	public JMenuItem getMAntialiasedDraw()
		{	return mAntialiasedDraw;	}
}
