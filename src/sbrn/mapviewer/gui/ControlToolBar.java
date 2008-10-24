package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.imageio.*;
import javax.swing.*;

import sbrn.mapviewer.gui.dialog.*;
import scri.commons.gui.*;

class ControlToolBar extends JToolBar implements ActionListener
{
	private WinMain winMain;

	private JButton bOpen;
	private JButton bExport;
	private JToggleButton bOverview;
	private JButton bHelp;
	private JLabel blastLabel, blastScoreLabel;
	private JSlider eValueSlider;
	private JButton bFindFeatures;
	private JButton bResetAll;
	public FindFeaturesDialog ffDialog = new FindFeaturesDialog();


	ControlToolBar(WinMain winMain)
	{
		this.winMain = winMain;

		setFloatable(false);
		setBorderPainted(false);

		createControls();

		if (SystemUtils.isMacOS() == false)
			add(new JLabel("  "));

		add(bOpen);

		addSeparator(true);

		add(bOverview);
		add(bExport);

		addSeparator(true);

		//maybe keep this group of controls on a theme of "does something with the data"
		add(bFindFeatures);
		add(bResetAll);

		addSeparator(true);

		add(blastLabel);
		add(eValueSlider);
//		add(blastScoreLabel);

		addSeparator(true);

		add(bHelp);

		add(new JLabel("  "));
	}

	private void createControls()
	{
		blastLabel = new JLabel("BLAST Cut-off:");
		blastScoreLabel = new JLabel("                   ");
		eValueSlider = new JSlider();
		eValueSlider.setMaximumSize(new Dimension(125, 50));
		eValueSlider.setMajorTickSpacing(100);
		eValueSlider.setMaximum(300);
		eValueSlider.setMinorTickSpacing(50);
//		eValueSlider.setPaintLabels(true);
		eValueSlider.setPaintTicks(true);
		eValueSlider.setValue(0);
        eValueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                eValueSliderStateChanged(evt);
            }
        });

        eValueSlider.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e)
        	{
        		winMain.mainCanvas.drawBlastScore = true;
        		winMain.mainCanvas.updateCanvas(true);
        	}

        	public void mouseReleased(MouseEvent e)
        	{
        		winMain.mainCanvas.drawBlastScore = false;
        		winMain.mainCanvas.updateCanvas(true);
        	}
        });

		bOpen = (JButton) getButton(false, "", "Load data into Mapviewer", Icons.getIcon("FILEOPEN"));
		bExport = (JButton) getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"));
		bOverview = (JToggleButton) getButton(true, "", "Toggle the overview dialog on or off", Icons.getIcon("OVERVIEW"));
		bOverview.setSelected(Prefs.guiOverviewVisible);
		bHelp =  (JButton) getButton(false, "", "Help", Icons.getIcon("HELP"));
		bFindFeatures = (JButton) getButton(false, "", "Find Features", Icons.getIcon("FIND"));
		bResetAll =  (JButton) getButton(false, "", "Reset Display", Icons.getIcon("RESET"));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bExport)
			exportImage();

		else if (e.getSource() == bOverview)
			toggleOverviewDialog();

		//show the find features dialog
		else if (e.getSource() == bFindFeatures)
		{
			ffDialog.ffPanel.getFFTextArea().setText("");
			ffDialog.setVisible(true);
		}

		//reset the main canvas view and deselect all features
		else if (e.getSource() == bResetAll)
			MapViewer.winMain.fatController.resetMainCanvasView();
	}

	private void addSeparator(boolean separator)
	{
		if (SystemUtils.isMacOS())
		{
			add(new JLabel(" "));
			if (separator)
				add(new JLabel(" "));
		}
		else if (separator)
			addSeparator();
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	private AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton();
		else
			button = new JButton();

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.addActionListener(this);
		button.setMargin(new Insets(2, 1, 2, 1));

		if (SystemUtils.isMacOS())
		{
			button.putClientProperty("JButton.buttonType", "bevel");
			button.setMargin(new Insets(-2, -1, -2, -1));
		}

		return button;
	}

	void toggleOverviewDialog()
	{
		// Toggle the state
		Prefs.guiOverviewVisible = !Prefs.guiOverviewVisible;

		// Then set the toolbar button and dialog to match
		bOverview.setSelected(Prefs.guiOverviewVisible);
		winMain.overviewDialog.setVisible(Prefs.guiOverviewVisible);
	}

	private void eValueSliderStateChanged(javax.swing.event.ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();

		//convert the value selected to the exponent of a small decimal and set this as
		//the new BLAST threshold (which is a double)
		int exponent = source.getValue();
		DecimalFormat df = new DecimalFormat("0.##E0");
		try
		{
			Number score = df.parse("1.00E-" + exponent);
			LinkDisplayManager.blastThreshold = score.doubleValue();
			blastScoreLabel.setText("" + LinkDisplayManager.blastThreshold);

			winMain.mainCanvas.updateCanvas(true);
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
	}

	// Queries the user for the location on disk to save a PNG image containing
	// the contents of the main canvas (via its back buffer).
	private void exportImage()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save Image As");
		fc.setAcceptAllFileFilterUsed(false);
		// TODO: track current directories and offer a suitable filename
		fc.setSelectedFile(new File("mapviewer.png"));

		while (fc.showSaveDialog(winMain) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			// Confirm overwrite
			if (file.exists())
			{
				String msg = file + " already exists.\nContinuing will "
					+ "overwrite this file with your new image.";
				String[] options = new String[] { "Overwrite", "Rename", "Cancel" };

				int response = TaskDialog.show(msg, MsgBox.WAR, 0, options);

				if (response == 1)
					continue;
				else if (response == -1 || response == 2)
					return;
			}

			try
			{
				ImageIO.write(winMain.mainCanvas.getImageBuffer(), "png", file);
				TaskDialog.info("The exported image was successfully saved "
					+ " to " + file, "Close");
			}
			catch (Exception exception)
			{
				TaskDialog.error("An internal error has prevented the image "
					+ "from being exported correctly.\n\nError details: "
					+ exception.getMessage(), "Close");
			}

			return;
		}
	}



}