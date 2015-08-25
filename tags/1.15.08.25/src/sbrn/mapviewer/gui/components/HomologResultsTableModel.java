package sbrn.mapviewer.gui.components;

import java.util.*;
import javax.swing.table.*;
import sbrn.mapviewer.*;

/**
 * Table model to be used for results table when we are dealing with more than one genome
 *
 */
public class HomologResultsTableModel extends AbstractTableModel
{
	public ArrayList<ResultsTableEntry> tableEntries;

	public final static String targetNameColumnLabel =  "Target name";
	public final static String targetPositionColumnLabel = "Target position";
	public final static String targetChromosomeColumnLabel = "Target chromosome";
	public final static String homologColumnLabel = "Homolog";
	public final static String homologGenomeColumnLabel = "Homolog genome";
	public final static String homologChromosomeColumnLabel = "Homolog chromosome";
	public final static String homologPositionColumnLabel = "Homolog position";
	public static String eValueColumnLabel = "Homology score";
	public final static String homologAnnotationColumnLabel = "Homolog annotation";

	private final String[] columnNames =
	{targetNameColumnLabel, targetPositionColumnLabel, targetChromosomeColumnLabel, homologColumnLabel,
					homologGenomeColumnLabel, homologChromosomeColumnLabel, homologPositionColumnLabel, eValueColumnLabel, homologAnnotationColumnLabel};

	public LinkedList<String> columnNameList = new LinkedList<String>();

	public HomologResultsTableModel(ArrayList<ResultsTableEntry> tableEntries)
	{
		super();
		this.tableEntries = tableEntries;
		
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
		return tableEntries.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{

		switch (columnIndex)
		{
			case 0: return tableEntries.get(rowIndex).getTargetFeatureName();
			case 1: return tableEntries.get(rowIndex).getTargetFeatureStart();
			case 2: return tableEntries.get(rowIndex).getTargetFeatureMap();
			case 3: return tableEntries.get(rowIndex).getHomologFeatureName();
			case 4: return tableEntries.get(rowIndex).getHomologFeatureMapset();
			case 5: return tableEntries.get(rowIndex).getHomologFeatureMap();
			case 6: return tableEntries.get(rowIndex).getHomologFeatureStart();
			case 7: return tableEntries.get(rowIndex).getLinkEValue();
			case 8: return tableEntries.get(rowIndex).getHomologFeatureAnnotation();
		}

		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		try
		{
			switch (columnIndex)
			{
				case 0:  return Class.forName("java.lang.String");
				case 1:  return Class.forName("java.lang.String");
				case 2:  return Class.forName("java.lang.String");
				case 3:  return Class.forName("java.lang.String");
				case 4:  return Class.forName("java.lang.String");
				case 5:  return Class.forName("java.lang.String");
				case 6:  return Class.forName("java.lang.String");
				case 7:  return Class.forName("java.lang.String");
				case 8:  return Class.forName("java.lang.String");
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
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
		for (ResultsTableEntry tableEntry : tableEntries)
		{
			buf.append(tableEntry.getTargetFeatureName() + "\t");
			buf.append(tableEntry.getTargetFeatureStart() + "\t");
			buf.append(tableEntry.getTargetFeatureMap() + "\t");
			buf.append(tableEntry.getHomologFeatureName()+ "\t");
			buf.append(tableEntry.getHomologFeatureMapset() + "\t");
			buf.append(tableEntry.getHomologFeatureMap() + "\t");
			buf.append(tableEntry.getHomologFeatureStart() + "\t");
			buf.append(tableEntry.getLinkEValue() + "\t");
			if(tableEntry.getHomologFeatureAnnotation() != null)
				buf.append(tableEntry.getHomologFeatureAnnotation());
			buf.append("\n");
		}
		return buf.toString();
	}
}
