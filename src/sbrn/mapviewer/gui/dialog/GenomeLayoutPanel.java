package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.*;

/*
 * A JPanel that allows the user to select which genomes they want to see on screen and in what order they should be laid out.
 *
 */
public class GenomeLayoutPanel extends JPanel
{

	// a list of comboboxes we have in this panel
	public LinkedList<JComboBox> comboBoxes = new LinkedList<JComboBox>();
	//the names of the datasets we can choose from
	String[] datasetNames = null;

	private JLabel infoLabel;

	//buttons for adding and removing combo boxes
	public JButton removeButton = null;
	public JButton addButton = null;

	//panels for the buttons and the combo boxes
	public JPanel buttonPanel = null;
	public JPanel comboPanel;

	//true if the combo boxes with the mapset names have been initialized
	boolean combosInited = false;

	public GenomeLayoutPanel()
	{
		initComponents();
		setupComboBoxes();

	}

	//inits the combo boxes to the number of genomes we initially have available
	public void setupComboBoxes()
	{
		//check first whether we already have comboboxes in place
		//if we are loading data on top of an existing dataset we need to reinitialize these
		if(combosInited)
		{
			for (int i = 0; i < comboBoxes.size(); i++)
			{
				removeComboBox();
			}
		}

		int numDatasets = Strudel.winMain.dataContainer.allMapSets.size();
		datasetNames = new String[numDatasets];
		for (int i = 0; i < datasetNames.length; i++)
		{
			datasetNames[i] = Strudel.winMain.dataContainer.allMapSets.get(i).getName();
		}
		for (int i = 0; i < numDatasets; i++)
		{
			addComboBox(i);
		}

		combosInited = true;
	}

	//adds a single combo box to the end of our list
	public void addComboBox(int selectedIndex)
	{
		JComboBox comboBox = new JComboBox(datasetNames);
		comboBox.setMaximumSize(new Dimension(150, 30));
		comboBox.setMinimumSize(new Dimension(150, 30));
		comboBox.setSelectedIndex(selectedIndex);
		comboPanel.add(comboBox);
		comboBoxes.add(comboBox);
		comboPanel.revalidate();
	}

	//removes a single combo box from the end of our list
	public void removeComboBox()
	{
		comboPanel.remove(comboBoxes.size()-1);
		comboBoxes.remove(comboBoxes.size()-1);
		comboPanel.validate();
	}

	private void initComponents()
	{
		//this label has instructions in it
		infoLabel = new javax.swing.JLabel();
		infoLabel.setText("<html>You can change the order in which your datasets are displayed on screen left to right. You can also add multiple instances of a dataset.</html>");
		infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		//the panel with the combo boxes
		comboPanel = new javax.swing.JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.PAGE_AXIS));
		comboPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		//configure the buttons
		removeButton = new javax.swing.JButton("Remove");
		addButton = new javax.swing.JButton("Add");
		removeButton.setPreferredSize(new java.awt.Dimension(79, 25));
		addButton.setPreferredSize(new java.awt.Dimension(79, 25));

		//add them to their panel
		buttonPanel = new javax.swing.JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);

		//assemble everything
		setLayout(new BorderLayout());
		add(infoLabel, BorderLayout.NORTH);
		add(comboPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setBorder(javax.swing.BorderFactory.createTitledBorder("Configure datasets:"));
	}
}
