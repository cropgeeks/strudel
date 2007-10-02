package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.*;


import sbrn.mapviewer.gui.tests.syntenyviewer2d.Canvas;
import sbrn.mapviewer.gui.tests.syntenyviewer3d.SyntenyViewer3DCanvas;

public class MapViewerFrame extends JFrame
{
	public static void main(String[] args)
	{
		try
		{
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// get the GUI set up
			MapViewerFrame frame = new MapViewerFrame();
			setupComponents(frame);
			//frame.setSize(800, 600);			
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

	private static void setupComponents(MapViewerFrame frame)
	{

		// data files - hard coded for now
		System.out.println("working dir = " + System.getProperty("user.dir"));
		String dataDir = "trunk/data/";
		File referenceData = new File(dataDir + "rice_pseudo4_os.maps");
		File targetData = new File(dataDir + "new_sxm_edited.maps");
		File compData = new File(dataDir + "barley_SNPS_vs_rice4_corr.data");
		File[] otherMapFiles = new File[]
		{ new File(dataDir + "new_owb_edited.maps"), new File(dataDir + "new_mxb_edited.maps") };

		// make a tabbed pane and add the 2D and 3D panels to it
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);		
		SyntenyViewer3DCanvas canvas = new SyntenyViewer3DCanvas(referenceData, targetData, compData, otherMapFiles);
		Canvas canvas2D = new Canvas();
		tabbedPane.addTab("   2D   ", canvas2D);
		tabbedPane.addTab("   3D   ", canvas);

		//side panel
		ControlPanel controlPanel = new ControlPanel(tabbedPane);
				
		//Create a split pane with the two components in it
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						controlPanel,tabbedPane);
		splitPane.setOneTouchExpandable(true);		
		splitPane.setResizeWeight(0.0);
		tabbedPane.setPreferredSize(new Dimension(600,600));
		controlPanel.setPreferredSize(new Dimension(150,600));		
		frame.getContentPane().add(splitPane);	
		
		//menu bar
		frame.setJMenuBar(new MapViewerMenuBar(frame));
	}

}
