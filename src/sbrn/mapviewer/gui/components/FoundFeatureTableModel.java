package sbrn.mapviewer.gui.components;

import java.util.*;

import javax.swing.table.*;

import sbrn.mapviewer.data.*;



public class FoundFeatureTableModel extends AbstractTableModel
{
	LinkedList<Link> homologies = new LinkedList<Link>();
	
	public final static String targetNameColumnLabel =  "Target name";
	public final static String targetPositionColumnLabel = "Target position";
	public final static String targetChromosomeColumnLabel = "Target chromosome";
	public final static String homologColumnLabel = "Homolog";
	public final static String homologGenomeColumnLabel = "Homolog genome";
	public final static String homologChromosomeColumnLabel = "Homolog chromosome";
	public final static String homologPositionColumnLabel = "Homolog position";
	public final static String homologAnnotationColumnLabel = "Homolog annotation";
	
	private String[] columnNames =
	{targetNameColumnLabel, targetPositionColumnLabel, targetChromosomeColumnLabel, homologColumnLabel, 
	homologGenomeColumnLabel, homologChromosomeColumnLabel, homologPositionColumnLabel, homologAnnotationColumnLabel};
	
	public LinkedList<String> columnNameList = new LinkedList<String>();
	

	public FoundFeatureTableModel(LinkedList<Link> homologies)
	{
		super();
		this.homologies = homologies;
		for (int i = 0; i < columnNames.length; i++)
		{
			columnNameList.add(columnNames[i]);
		}
	}

	public int getColumnCount()
	{
		return columnNames.length;
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
		            case 2:  return link.getFeature1().getOwningMap().getName();
		            case 3:  return link.getFeature2().getName();
		            case 4:  return link.getFeature2().getOwningMap().getOwningMapSet().getName();
		            case 5:  return link.getFeature2().getOwningMap().getName();
		            case 6:  return (int)(link.getFeature2().getStart());
		            case 7:   return link.getFeature2().getAnnotation();

		            default: return null;
		  }

	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	
}
