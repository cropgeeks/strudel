package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class FindFeaturesInRangeDialog extends JDialog implements ActionListener
{

	private JButton bFind, bCancel;
	public MTFindFeaturesInRangePanel ffInRangePanel = new MTFindFeaturesInRangePanel();

	public FindFeaturesInRangeDialog()
	{
		super(Strudel.winMain, "List features in range", true);

		add(ffInRangePanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bFind);
		SwingUtils.addCloseHandler(this, bCancel);

		setLocationRelativeTo(Strudel.winMain);
		pack();
		setResizable(true);

	}


	private JPanel createButtons()
	{
		bFind = SwingUtils.getButton("Find");
		bFind.addActionListener(this);
		bFind.setMnemonic(KeyEvent.VK_F);

		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		bCancel.setMnemonic(KeyEvent.VK_C);

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
			FeatureSearchHandler.findFeaturesInRangeFromDialog(this);
		}

		else if (e.getSource() == bCancel)
		{
			//hide the find dialog
			setVisible(false);
			//clear the highlighted features
			Strudel.winMain.fatController.clearHighlightFeature();
		}
	}


}
