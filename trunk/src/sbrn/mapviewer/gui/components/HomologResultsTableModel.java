package sbrn.mapviewer.gui.components;

import java.util.*;
import javax.swing.table.*;
import sbrn.mapviewer.data.*;



public class HomologResultsTableModel extends AbstractTableModel
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
	
	public HomologResultsTableModel()
	{		
	}
	

	public HomologResultsTableModel(LinkedList<Link> homologies)
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
		            case 0:  if(link.getFeature1() != null){return link.getFeature1().getName();}break;
		            case 1:  if(link.getFeature1() != null){return link.getFeature1().getStart();}break;
		            case 2: if(link.getFeature1() != null){ return link.getFeature1().getOwningMap().getName();}break;
		            case 3:  if(link.getFeature2() != null){return link.getFeature2().getName();}break;
		            case 4:  if(link.getFeature2() != null){return link.getFeature2().getOwningMap().getOwningMapSet().getName();}break;
		            case 5:  if(link.getFeature2() != null){return link.getFeature2().getOwningMap().getName();}break;
		            case 6:  if(link.getFeature2() != null){return (link.getFeature2().getStart());}break;
		            case 7:  if(link.getFeature2() != null){return link.getFeature2().getAnnotation();}break;
		  }

		return null;
	}
	
	public Class<?> getColumnClass(int columnIndex)
	{
		try
		{
			switch (columnIndex) 
			{
			            case 0:  return Class.forName("java.lang.String"); 
			            case 1:  return Class.forName("java.lang.Float"); 
			            case 2:  return Class.forName("java.lang.String"); 
			            case 3:  return Class.forName("java.lang.String"); 
			            case 4:  return Class.forName("java.lang.String"); 
			            case 5:  return Class.forName("java.lang.String"); 
			            case 6:  return Class.forName("java.lang.Float"); 
			            case 7:  return Class.forName("java.lang.String"); 
			  }
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	
}
