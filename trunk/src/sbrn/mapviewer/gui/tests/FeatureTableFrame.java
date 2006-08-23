package sbrn.mapviewer.gui.tests;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sbrn.mapviewer.data.*;
import sbrn.mapviewer.io.*;

public class FeatureTableFrame extends JFrame implements ListSelectionListener
{
	// Data currently being displayed
	private MapSet mapset;
	
	private JList mapList;
	private DefaultListModel mapModel;
	private JTable featureTable;
	
	public static void main(String[] args)
		throws Exception
	{ 
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) {}
		
		MapSet mapset = new JoinMapImporter(new File(args[0])).loadMapSet();
						
		FeatureTableFrame frame = new FeatureTableFrame(mapset);
	}
	
	public FeatureTableFrame(MapSet mapset)
	{
		this.mapset = mapset;
		
		add(createControls());
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0); 
			}
		});
		
		setSize(600, 500);
		setLocationRelativeTo(null);
		setTitle("Feature Test");
		setVisible(true);
	}
	
	private JPanel createControls()
	{
		mapModel = new DefaultListModel();
		for (ChromoMap map: mapset)
			mapModel.addElement(map);
		mapList = new JList(mapModel);
		mapList.setPreferredSize(new Dimension(100, 50));
		mapList.addListSelectionListener(this);
		mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp1 = new JScrollPane(mapList);
		
		featureTable = new JTable();
		JScrollPane sp2 = new JScrollPane(featureTable);
		
		JPanel p1 = new JPanel(new BorderLayout(5, 5));
		p1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p1.add(sp1, BorderLayout.WEST);
		p1.add(sp2);
		
		return p1;
	}
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		
		if (e.getSource() == mapList)
		{
			ChromoMap map = (ChromoMap) mapList.getSelectedValue();
						
			if (map != null)
			{
				featureTable.setModel(new FeatureTableModel(map));
			}
		}
	}
	
	private static class FeatureTableModel extends AbstractTableModel
	{
		private ChromoMap map;
		
		FeatureTableModel(ChromoMap map)
			{ this.map = map;	}
		
		public String getColumnName(int col)
		{
			switch (col)
			{
				case 0: return "Feature Name";
				case 1: return "Start";
				case 2: return "Stop";
			}
			
			return null;
		}
		
		public int getColumnCount()
			{ return 3; }
		
		public int getRowCount()
			{ return map.countFeatures(); }
		
		public Object getValueAt(int row, int col)
		{
			Feature feature = map.getFeature(row);
			
			switch (col)
			{
				case 0: return feature;
				case 1: return feature.getStart();
				case 2: return feature.getStop();
			}
			
			return null;
		}
	}
}