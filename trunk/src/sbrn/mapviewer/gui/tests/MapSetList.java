package sbrn.mapviewer.gui.tests;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import sbrn.mapviewer.data.*;

class MapSetList extends JPanel
{
	private JList list;
	private DefaultListModel model;
	private JScrollPane sp;
	
	private MapSet mapset;
	
	MapSetList(MapSet mapset)
	{
		this.mapset = mapset;
		
		createControls();
	}
	
	private void createControls()
	{
		setLayout(new BorderLayout());
		
		model = new DefaultListModel();
		
		System.out.println("Mapset " + mapset.getName() + " has size " + mapset.size());
		
		for (ChromoMap map: mapset)
		{
			model.addElement(map);
			System.out.println("Adding " + map);
		}
		
		list = new JList(model);
		sp = new JScrollPane(list);
		
		add(new JLabel("MapSet: " + mapset.getName()), BorderLayout.NORTH);
		add(sp);
	}
}