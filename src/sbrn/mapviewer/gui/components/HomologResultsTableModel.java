package sbrn.mapviewer.gui.components;

import java.util.*;
import javax.swing.table.*;
import sbrn.mapviewer.data.*;

/**
 * Table model to be used for results table when we are dealing with more than one genome
 * 
 */
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
	public final static String eValueColumnLabel = "BLAST e-value";
	public final static String homologAnnotationColumnLabel = "Homolog annotation";
	
	private String[] columnNames =
	{targetNameColumnLabel, targetPositionColumnLabel, targetChromosomeColumnLabel, homologColumnLabel, 
	homologGenomeColumnLabel, homologChromosomeColumnLabel, homologPositionColumnLabel, eValueColumnLabel, homologAnnotationColumnLabel};
	
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
		            case 7:  if(link.getFeature2() != null){return ((float)link.getBlastScore());}break;
		            case 8:  if(link.getFeature2() != null){return link.getFeature2().getAnnotation();}break;
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
			            case 7:  return Class.forName("java.lang.Float"); 
			            case 8:  return Class.forName("java.lang.String"); 
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
	
	public String getAllDataInTabFormat()
	{
		StringBuffer buf = new StringBuffer();
		
		//output column names first
		for (int i = 0; i < columnNames.length; i++)
		{
			buf.append(columnNames[i]);
			if(i == columnNames.length-1)
				buf.append("\n");
			else
				buf.append("\t");
		}
				
		//output the row data
		for (Link link : homologies)
		{
			buf.append(link.getFeature1().getName() + "\t");
			buf.append(link.getFeature1().getStart() + "\t");
			buf.append(link.getFeature1().getOwningMap().getName() + "\t");
			buf.append(link.getFeature2().getName() + "\t");
			buf.append(link.getFeature2().getOwningMap().getOwningMapSet().getName() + "\t");
			buf.append(link.getFeature2().getOwningMap().getName() + "\t");
			buf.append(link.getFeature2().getStart() + "\t");
			buf.append(link.getBlastScore() + "\t");
			buf.append(link.getAnnotation() + "\n");
		}
		return buf.toString();
	}
	
	
}
