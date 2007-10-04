package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.tests.syntenyviewer2d.*;
import sbrn.mapviewer.gui.tests.syntenyviewer3d.*;
import sbrn.mapviewer.io.*;

public class MapViewerFrame extends JFrame
{
	// ===================================================vars=================================================
	// data files
	private static File referenceData;
	private static File targetData;
	private static File compData;
	private static File[] otherMapFiles;

	// this Mapset holds all the data we wanst to compare against
	private static MapSet referenceMapset = null;
	// this Mapset holds the data we want to find out about
	private static MapSet targetMapset = null;
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	private static LinkSet links = null;

	// ============================================methods====================================================

	public static void main(String[] args)
	{
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// get the GUI set up
			MapViewerFrame frame = new MapViewerFrame();
			setupComponents(frame);
			frame.setVisible(true);
			frame.setTitle("Map Viewer");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private static void setupComponents(MapViewerFrame frame)
	{
		loadData();

		// make a tabbed pane and add the 2D and 3D panels to it
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		SyntenyViewer3DCanvas canvas = new SyntenyViewer3DCanvas(referenceData, targetData, compData, otherMapFiles, referenceMapset, targetMapset, 0, links);
		Canvas2D canvas2D = new Canvas2D(links);
		tabbedPane.addTab("   2D   ", canvas2D);
		tabbedPane.addTab("   3D   ", canvas);

		// side panel
		ControlPanel controlPanel = new ControlPanel(tabbedPane);

		// Create a split pane with the two components in it
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, tabbedPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.0);
		tabbedPane.setPreferredSize(new Dimension(600, 600));
		controlPanel.setPreferredSize(new Dimension(150, 600));
		frame.getContentPane().add(splitPane);

		// menu bar
		frame.setJMenuBar(new MapViewerMenuBar(frame));
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */
	private static void loadData()
	{
		try
		{
			// data files - hard coded for now
			//System.out.println("working dir = " + System.getProperty("user.dir"));
			String dataDir = "trunk/data/";
			referenceData = new File(dataDir + "rice_pseudo4_os.maps");
			targetData = new File(dataDir + "new_sxm_edited.maps");
			compData = new File(dataDir + "barley_SNPS_vs_rice4_corr.data");
			otherMapFiles = new File[]
			{ new File(dataDir + "new_owb_edited.maps"), new File(dataDir + "new_mxb_edited.maps") };

			// load data
			DataLoader dLoader = new DataLoader();
			referenceMapset = dLoader.loadMapData(referenceData);
			targetMapset = dLoader.loadMapData(targetData);
			
			//need to set the names of the mapsets
			//for now, extract them from the name of the first map in the set
			//the first token in the map name will be the taxon name
			//TODO: check that CMAP data always comes in this same format i.e. the map name starts with the taxon name
			//might want to change this later -- not a very good solution for now
			String targetSetName = targetMapset.getMap(0).getName();
			//chop off everything after the first space
			targetSetName = targetSetName.substring(0, targetSetName.indexOf(","));
			targetMapset.setName(targetSetName);
			String referenceSetName = referenceMapset.getMap(0).getName();
			//chop off everything after the first space
			referenceSetName = referenceSetName.substring(0, referenceSetName.indexOf(","));
			referenceMapset.setName(referenceSetName);
			
			//load links
			CMapLinkImporter limp = new CMapLinkImporter(compData);
			limp.addMapSet(referenceMapset);
			limp.addMapSet(targetMapset);
			MapSet[] otherMapSets = dLoader.loadOtherMapSets(otherMapFiles);
			for (int i = 0; i < otherMapSets.length; i++)
			{
				limp.addMapSet(otherMapSets[i]);
			}

			try
			{
				links = limp.loadLinkSet();
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
