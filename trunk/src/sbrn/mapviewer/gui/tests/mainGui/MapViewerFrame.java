package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.gui.tests.syntenyviewer2d.*;
import sbrn.mapviewer.gui.tests.syntenyviewer3d.*;
import sbrn.mapviewer.io.*;

public class MapViewerFrame extends JFrame
{
	// ===================================================vars=================================================
	// data files
	private File referenceData;
	private File targetData;
	private File compData;
	private File[] otherMapFiles;

	// this Mapset holds all the data we want to compare against
	private MapSet referenceMapset = null;
	// this Mapset holds the data we want to find out about
	private MapSet targetMapset = null;
	// this link set holds the all the possible links between all chromos in the target set and all chromos in the reference set
	private LinkSet links = null;
	//the index of the currently selected chromosome in the target mapset
	int selectedChromoIndex = 0;
	//this tabbed pane holds the views
	public JPanel mainPanel;
	//the canvas for drawing 2D overviews of the genomes
	public Canvas2D canvas2D;

	
	
	public MapViewerFrame()
	{
		setupComponents();
	}

	// ============================================methods====================================================

	public static void main(String[] args)
	{
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// get the GUI set up
			MapViewerFrame frame = new MapViewerFrame();
			frame.setVisible(true);
			frame.setTitle("Map Viewer");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			//frame.setExtendedState(frame.MAXIMIZED_BOTH);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void setupComponents()
	{
		loadData();

		canvas2D = new Canvas2D(this,links);
		canvas2D.setPreferredSize(new Dimension(800, 700));

		// side panel
		ControlPanel controlPanel = new ControlPanel(this);
		controlPanel.setPreferredSize(new Dimension(180, 700));

		add(controlPanel,BorderLayout.EAST);
		add(canvas2D,BorderLayout.CENTER);

		// menu bar
		this.setJMenuBar(new MapViewerMenuBar(this));
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Loads data from file using the object data model; this will populate all the relevant MapSet and LinkSet objects.
	 */
	private void loadData()
	{
		try
		{
			// data files - hard coded for now
			System.out.println("loading data");
			System.out.println("working dir = " + System.getProperty("user.dir"));
			referenceData = new File( (this.getClass().getResource( "/data/rice_pseudo4_os.maps")).toURI());
			targetData = new File((this.getClass().getResource( "/data/new_sxm_edited.maps")).toURI());
			compData = new File((this.getClass().getResource( "/data/barley_SNPS_vs_rice4_corr.data")).toURI());
			otherMapFiles = new File[]
			                         	{ 
								new File((this.getClass().getResource( "/data/new_owb_edited.maps")).toURI()),
								new File((this.getClass().getResource( "/data/new_mxb_edited.maps")).toURI())
			                         	};
			
			System.out.println("referenceData = " + referenceData);

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

	public int getSelectedChromoIndex()
	{
		return selectedChromoIndex;
	}

	public void setSelectedChromoIndex(int selectedChromoIndex)
	{
		this.selectedChromoIndex = selectedChromoIndex;
	}


	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
