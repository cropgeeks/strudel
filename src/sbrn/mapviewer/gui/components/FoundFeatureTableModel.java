package sbrn.mapviewer.gui.components;

import java.util.*;

import javax.swing.table.*;

import sbrn.mapviewer.data.*;



public class FoundFeatureTableModel extends AbstractTableModel
{
	LinkedList<Link> homologies = new LinkedList<Link>();
	
	private String[] columnNames =
	{ "Target name", "Target position", "Homolog", "Homolog genome", "Homolog chromosome",
					"Homolog position", "Homolog annotation"};
	

	public FoundFeatureTableModel(LinkedList<Link> homologies)
	{
		super();
		this.homologies = homologies;
	}

	public int getColumnCount()
	{
		return 7;
	}

	public int getRowCount()
	{
		return homologies.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Link link = homologies.get(rowIndex);
		
		switch (columnIndex) 
		{
		            case 0:  return link.getFeature1().getName();
		            case 1:  return link.getFeature1().getStart();
		            case 2:  return link.getFeature2().getName();
		            case 3:  return link.getFeature2().getOwningMap().getOwningMapSet().getName();
		            case 4:  return link.getFeature2().getOwningMap().getName();
		            case 5:  return (int)(link.getFeature2().getStart());
		            case 6:   return link.getFeature2().getAnnotation();

		            default: return null;
		  }

	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
}
