package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import scri.commons.gui.*;
/*
 * This dialog allows the user to decide which genomes they want to see on screen and in what order they should be laid out.
 *
 */
public class GenomeLayoutDialog extends JDialog implements ActionListener
{

	private JButton okButton, cancelButton;
	public GenomeLayoutPanel genomeLayoutPanel = null;
	private final Dimension initialSize = new Dimension(350,200);


	public GenomeLayoutDialog()
	{
		super(Strudel.winMain, "Layout genomes", true);

		genomeLayoutPanel = new GenomeLayoutPanel(this);

		genomeLayoutPanel.setPreferredSize(initialSize);
		add(genomeLayoutPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		SwingUtils.addCloseHandler(this, cancelButton);

		//action listeners for the buttons in the genome layout panel
		genomeLayoutPanel.removeButton.addActionListener(this);
		genomeLayoutPanel.addButton.addActionListener(this);

		setResizable(false);

	}

	public void restoreInitialSize()
	{
		setSize(initialSize);
	}

	private JPanel createButtons()
	{
		okButton = SwingUtils.getButton("OK");
		okButton.addActionListener(this);
		okButton.setMnemonic(KeyEvent.VK_F);

		cancelButton = SwingUtils.getButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		p1.add(okButton);
		p1.add(cancelButton);

		return p1;
	}



	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == okButton)
		{
			configureGMapSets();
			setVisible(false);
		}
		else if (e.getSource() == cancelButton)
		{
			//hide the dialog
			setVisible(false);
		}
		else if (e.getSource() == genomeLayoutPanel.addButton)
		{
			genomeLayoutPanel.addComboBox(0);
		}
		else if (e.getSource() == genomeLayoutPanel.removeButton)
		{
			genomeLayoutPanel.removeComboBox(false);
		}
	}

	public void resizeDialog(int newHeight)
	{
		setSize(new Dimension(getWidth(), newHeight));
	}


	private void configureGMapSets()
	{
		//extract the names of the current list of gmapsets to be displayed
		LinkedList<String> gMapsetNames = new LinkedList<String>();
		for (JComboBox comboBox : genomeLayoutPanel.comboBoxes)
		{
			gMapsetNames.add((String)comboBox.getSelectedItem());
		}

		//reconfigure the mapsets and the GUI
		Strudel.winMain.dataSet.reconfigureGMapSets(gMapsetNames);
		Strudel.winMain.reinitialiseDependentComponents();

		//reinitialize the linkset lookup
//		Strudel.winMain.mainCanvas.linkDisplayManager.initLinkSetLookup();

		//reset and repaint
		Strudel.winMain.fatController.resetMainCanvasView();
	}



}
