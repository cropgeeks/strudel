package sbrn.mapviewer.gui.tests;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.io.*;

public class MapSetGraphFrame extends JFrame
{
	private static File currentDir = new File(".");
	
	private MapSet mapset1;
	private MapSet mapset2;
	private LinkSet linkset;
	
	private MapSetList list1;
	private MapSetList list2;	
	private AllByAllGraphPanel graph;
	
	public static void main(String[] args)
	{
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) {}
		
//		File mapFile1 = getFile("Select MapSet #1");
//		File mapFile2 = getFile("Select MapSet #2");
//		File linkFile = getFile("Select Linkage Data File");

		File file1 = new File("..\\Data Files\\rice_pseudo4_os.maps");
		File file2 = new File("..\\Data Files\\new_owb_edited.maps");
		File file3 = new File("..\\Data Files\\barley_SNPS_vs_rice4_corr.data");
		
		MapSetGraphFrame frame = new MapSetGraphFrame(file1, file2, file3);
	}
	
	private static File getFile(String title)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		fc.setCurrentDirectory(currentDir);
		
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			currentDir = fc.getCurrentDirectory();
			return fc.getSelectedFile();
		}
		else		
			System.exit(0);
		
		return null;
	}
	
	public MapSetGraphFrame(File mapFile1, File mapFile2, File linkFile)
	{
		loadData(mapFile1, mapFile2, linkFile);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
				{ System.exit(0); }
		});
		
		add(createControls());
		
		
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("MapSetGraphFrame");
		setVisible(true);
	}
	
	private JPanel createControls()
	{
		list1 = new MapSetList(mapset1);
		list2 = new MapSetList(mapset2);
		
		graph = new AllByAllGraphPanel();
		graph.setLinkSet(linkset, mapset2);
		
		JPanel sidePanel = new JPanel(new GridLayout(2, 1, 5, 5));
		sidePanel.add(list1);
		sidePanel.add(list2);
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p1.add(sidePanel, BorderLayout.EAST);
		p1.add(graph);
		
		return p1;
	}
	
	void loadData(File mapFile1, File mapFile2, File linkFile)
	{
		try
		{
			CMapImporter mapImporter;
									
			// Load the first mapset
			mapImporter = new CMapImporter(mapFile1);			
			mapset1 = mapImporter.loadMapSet();
			
			// Load the second mapset
			mapImporter = new CMapImporter(mapFile2);			
			mapset2 = mapImporter.loadMapSet();
			
			CMapLinkImporter linkImporter = new CMapLinkImporter(linkFile);
			linkImporter.addMapSet(mapset1);
			linkImporter.addMapSet(mapset2);
			linkset = linkImporter.loadLinkSet();
			
			System.out.println("Loaded " + linkset.size() + " links");
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}
}