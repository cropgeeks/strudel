package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import scri.commons.gui.*;

public class ConfigureViewSettingsDialog extends JDialog implements ActionListener
{

	private JButton bClose, bCancel;
	public ConfigureViewSettingsPanel viewSettingsPanel;

	public ConfigureViewSettingsDialog()
	{
		super(Strudel.winMain, "Configure View Settings", true);

		viewSettingsPanel = new ConfigureViewSettingsPanel();

		add(viewSettingsPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bCancel);

		setLocationRelativeTo(Strudel.winMain);
		pack();
		setResizable(false);

	}


	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton("Close");
		bClose.addActionListener(this);
		bClose.setMnemonic(KeyEvent.VK_F);

		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);
		bCancel.setMnemonic(KeyEvent.VK_C);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));
		p1.add(bClose);
		p1.add(bCancel);

		return p1;
	}



	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
		{
			//hide the find dialog
			setVisible(false);
			Strudel.winMain.mainCanvas.updateCanvas(true);
		}

		else if (e.getSource() == bCancel)
		{
			//hide the find dialog
			setVisible(false);
		}
	}


}
