package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.gui.components.*;
import scri.commons.gui.SwingUtils;

/**
 * Dialog for allowing the user to choose a colour scheme and also to customise
 * the two available colour schemes.
 */
public class ColorSchemeChooserDialog extends JDialog implements ActionListener
{
	private NBColourPanel colorPanel;
	private PreviewCanvas preview;
	private WinMain winMain;
	private JButton bApply, bReset, bCancel;

	/**
	 * Constructor for the dialog. It accepts an instance of WinMain as its only
	 * parameter.
	 */
	public ColorSchemeChooserDialog(WinMain winMain)
	{
		super(winMain, "Colour Scheme Customisation");

		this.winMain = winMain;

		preview = new PreviewCanvas();
		colorPanel = new NBColourPanel(preview, this);

		add(colorPanel);
		add(createButtons(), BorderLayout.SOUTH);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setResizable(false);
	}

	public WinMain getWinMain()
	{
		return winMain;
	}

	private JPanel createButtons()
	{
		bApply = SwingUtils.getButton("Apply");
		bApply.addActionListener(this);
		bReset = SwingUtils.getButton("Reset colours");
		bReset.addActionListener(this);
		bCancel = SwingUtils.getButton("Cancel");
		bCancel.addActionListener(this);

		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		p.add(bApply);
		p.add(bReset);
		p.add(bCancel);

		return p;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == bApply)
		{
			colorPanel.setColourScheme();
			colorPanel.updatePreferences();
			setVisible(false);
			WinMain.mainCanvas.setRedraw(true);
			winMain.repaint();
		}
		else if(e.getSource() == bReset)
		{
			colorPanel.resetColours();
		}
		else if(e.getSource() == bCancel)
		{
			setVisible(false);
		}
	}
}
