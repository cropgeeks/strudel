package sbrn.mapviewer.gui.components;

import java.util.*;
import javax.swing.table.*;
import sbrn.mapviewer.data.*;


/**
 * Table model to be used for results table when we are dealing with only one genome
 * Does not contain links (as opposed to the equivalent HomologResultsTableModel, which is used in a 
 * comparative context)
 * 
 */
public class LinklessFeatureTableModel extends AbstractTableModel
{
	Vector<Feature> features = new Vector<Feature>();
	
	public final static String featureNameColumnLabel =  "Feature name";
	public final static String featurePositionColumnLabel = "Position";
	public final static String featureChromosomeColumnLabel = "Chromosome";
	public final static String featureAnnotationColumnLabel = "Annotation";
	
	private String[] columnNames =
	{featureNameColumnLabel, featurePositionColumnLabel, featureChromosomeColumnLabel, featureAnnotationColumnLabel};
	
	public LinkedList<String> columnNameList = new LinkedList<String>();
	
	public LinklessFeatureTableModel()
	{		
	}
	

	public LinklessFeatureTableModel(Vector<Feature> features)
	{
		super();
		this.features = features;
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
		return features.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Feature feature = features.get(rowIndex);
		
		switch (columnIndex) 
		{
		            case 0:  return feature.getName();
		            case 1:  return feature.getStart();
		            case 2:  return feature.getOwningMap().getName();
		            case 3:  return feature.getAnnotation();
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
		
		//output table row data
		for (Feature feature : features)
		{
			buf.append(feature.getName() + "\t");
			buf.append(feature.getStart() + "\t");
			buf.append(feature.getOwningMap().getName() + "\t");
			buf.append(feature.getAnnotation() + "\n");
			
		}
		return buf.toString();
	}
	
}
