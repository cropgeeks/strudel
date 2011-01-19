package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.data.*;

/*
 * A JPanel that allows the user to select which genomes they want to see on screen and in what order they should be laid out.
 *
 */
public class GenomeLayoutPanel extends JPanel
{

//=======================================vars=============================================

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

	GenomeLayoutDialog genomeLayoutDialog;

	//sizes for the combo boxes
	public int comboHeight = 20;
	public int comboWidth = 150;
	public Dimension comboSize = null;

	//=======================================c'tor=============================================

	public GenomeLayoutPanel(GenomeLayoutDialog genomeLayoutDialog)
	{
		this.genomeLayoutDialog = genomeLayoutDialog;
		initComponents();
		setupComboBoxes();
	}

	//=======================================methods=============================================

	//inits the combo boxes to the number of genomes we initially have available
	public void setupComboBoxes()
	{
		calcComboWidth();

		//check first whether we already have comboboxes in place
		//if we are loading data on top of an existing dataset we need to reinitialize these
		if(combosInited)
		{
			int numCombos = comboBoxes.size();
			for (int i = 0; i < numCombos; i++)
			{
				removeComboBox(true);
			}
		}

		//how many datasets do we have now
//		int numDatasets = Strudel.winMain.dataContainer.gMapSets.size();
//		datasetNames = new String[numDatasets];
//		for (int i = 0; i < datasetNames.length; i++)
//		{
//			datasetNames[i] = Strudel.winMain.dataContainer.gMapSets.get(i).name;
//		}
		int numDatasets = Strudel.winMain.dataContainer.allMapSets.size();
		datasetNames = new String[numDatasets];
		for (int i = 0; i < datasetNames.length; i++)
		{
			datasetNames[i] = Strudel.winMain.dataContainer.allMapSets.get(i).getName();
		}

		//restore the dialog box to its initial size before we start adding boxes
		genomeLayoutDialog.restoreInitialSize();

		//add as many comboboxes as we have gmapsets
		for (int i = 0; i < Strudel.winMain.dataContainer.gMapSets.size(); i++)
		{
			addComboBox(i);
		}
		combosInited = true;
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

	//searches through all the mapsets available and returns the width
	public void calcComboWidth()
	{
		int maxWidth = 0;

		JComboBox jCombo = new JComboBox();
		FontMetrics fm = jCombo.getFontMetrics(jCombo.getFont());

		for(MapSet mapset : Strudel.winMain.dataContainer.allMapSets)
		{
			int stringWidth = fm.stringWidth(mapset.getName());
			if(stringWidth > maxWidth)
				maxWidth = stringWidth;
		}

		//work out the correct width for the combos
		comboWidth = maxWidth + 40;
		comboSize = new Dimension(comboWidth, comboHeight);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

	//adds a single combo box to the end of our list and resizes the dialog box accordingly
	public void addComboBox(int selectedIndex)
	{
		JComboBox comboBox = new JComboBox(datasetNames);
		comboBox.setMaximumSize(comboSize);
		comboBox.setMinimumSize(comboSize);
		if(selectedIndex < Strudel.winMain.dataContainer.allMapSets.size())
			comboBox.setSelectedIndex(selectedIndex);
		comboPanel.add(comboBox);
		comboBoxes.add(comboBox);
		comboPanel.validate();
		this.validate();

		int newHeight = (genomeLayoutDialog.getHeight() + comboHeight);
		genomeLayoutDialog.resizeDialog(newHeight);
		genomeLayoutDialog.validate();
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

	//removes a single combo box from the end of our list and resizes the dialog box accordingly
	public void removeComboBox(boolean removeAll)
	{
		//we only want to remove the last combobox when we are reinitializing after loading a new dataset
		//otherwise we need to keep at least one because Strudel needs at least one dataset to function
		if((comboBoxes.size() > 1 && !removeAll) || (comboBoxes.size() > 0 && removeAll))
		{
			comboPanel.remove(comboBoxes.size()-1);
			comboBoxes.remove(comboBoxes.size()-1);
			comboPanel.validate();
			this.validate();

			int newHeight = (genomeLayoutDialog.getHeight() - comboHeight);
			genomeLayoutDialog.resizeDialog(newHeight);
			genomeLayoutDialog.validate();
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

	private void initComponents()
	{
		//this label has instructions in it
		infoLabel = new javax.swing.JLabel();
		infoLabel.setText("<html>You can change the order in which your datasets are displayed on screen left to right." +
						" You can also add or remove additional instances of a dataset.</html>");
		infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		//the panel with the combo boxes
		comboPanel = new javax.swing.JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.PAGE_AXIS));
		comboPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		//configure the buttons
		removeButton = new javax.swing.JButton("Remove");
		addButton = new javax.swing.JButton("Add");
		removeButton.setPreferredSize(new java.awt.Dimension(79, 25));
		addButton.setPreferredSize(new java.awt.Dimension(79, 25));

		//add them to their panel
		buttonPanel = new javax.swing.JPanel();
//		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);

		//assemble everything
		setLayout(new BorderLayout());
		add(infoLabel, BorderLayout.NORTH);
		add(comboPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setBorder(javax.swing.BorderFactory.createTitledBorder("Configure datasets:"));
	}

	//------------------------------------------------------------------------------------------------------------------------------------------

}//end class
