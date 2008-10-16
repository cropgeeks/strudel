package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

public class FindFeaturesDialog extends JDialog implements ActionListener
{

	private JButton bFind, bCancel;
	private boolean isOK = false;

	public MTFindFeaturesPanel ffPanel = new MTFindFeaturesPanel();

	public FindFeaturesDialog()
	{
		super(MapViewer.winMain, "Find Features", true);

		add(ffPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(MapViewer.winMain);
		setResizable(false);
	}


	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bFind);
		p1.add(bCancel);

		return p1;
	}



	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bFind)
		{
			isOK = true;
			setVisible(false);
			MapViewer.winMain.fatController.highlightFeaturesByNames( ffPanel.getFFTextArea().getText());
		}

		else if (e.getSource() == bCancel)
			setVisible(false);
	}


}
