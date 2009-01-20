package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.imageio.*;
import javax.swing.*;

import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.dialog.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class ControlToolBar extends JToolBar implements ActionListener
{
	private WinMain winMain;

	private JButton bOpen;
	private JButton bClose;
	private JButton bExport;
	private JToggleButton bOverview;
	private JButton bHelp;
	private JLabel blastLabel, blastScoreLabel;
	private JSlider eValueSlider;
	public JButton bFindFeatures;
	public JButton bFindFeaturesinRange;
	private JButton bResetAll;
	
	
	ControlToolBar(WinMain winMain)
	{
		this.winMain = winMain;

		setFloatable(false);
		setBorderPainted(false);

		createControls();

		if (SystemUtils.isMacOS() == false)
			add(new JLabel("  "));

		add(bOpen);
		add(bClose);

		addSeparator(true);

		add(bOverview);
		add(bExport);

		addSeparator(true);

		//maybe keep this group of controls on a theme of "does something with the data"
		add(bFindFeatures);
		add(bFindFeaturesinRange);
		add(bResetAll);

		addSeparator(true);

		add(blastLabel);
		add(eValueSlider);

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
		bOpen.setMnemonic(KeyEvent.VK_O);

		bClose = (JButton) getButton(false, "", "Close currrent dataset", Icons.getIcon("FILECLOSE"));
		bClose.setMnemonic(KeyEvent.VK_W);
		
		bExport = (JButton) getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"));
		bOverview = (JToggleButton) getButton(true, "", "Toggle the overview dialog on or off", Icons.getIcon("OVERVIEW"));
		bOverview.setSelected(Prefs.guiOverviewVisible);
		bHelp =  (JButton) getButton(false, "", "Help", Icons.getIcon("HELP"));
		
		bFindFeatures = (JButton) getButton(false, "", "Find features by name", Icons.getIcon("FIND"));
		bFindFeatures.setMnemonic(KeyEvent.VK_F);
		
		bFindFeaturesinRange = (JButton) getButton(false, "", "List features in range", Icons.getIcon("RANGE"));
		bFindFeaturesinRange.setMnemonic(KeyEvent.VK_R);
		
		bResetAll =  (JButton) getButton(false, "", "Reset display", Icons.getIcon("RESET"));
}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOpen)
		{
			OpenFileDialog openFileDialog = MapViewer.winMain.openFileDialog;
			openFileDialog.setLocationRelativeTo(MapViewer.winMain);
			openFileDialog.setVisible(true);
			//clear the text fields, in case they had text showing previously
			openFileDialog.openFilesPanel.getTargetfeatFileTF().setText("");
			openFileDialog.openFilesPanel.getRefGen1FeatFileTF().setText("");
			openFileDialog.openFilesPanel.getRefGen1HomFileTF().setText("");
			openFileDialog.openFilesPanel.getRefGen2FeatFileTF().setText("");
			openFileDialog.openFilesPanel.getRefGen2HomFileTF().setText("");
			
		}
		
		if (e.getSource() == bClose)
		{
			MapViewer.winMain.showStartPanel(true);
			MapViewer.winMain.overviewDialog.setVisible(false);
		}
		
		if (e.getSource() == bExport)
			exportImage();

		else if (e.getSource() == bOverview)
			toggleOverviewDialog();

		//show the find features dialog
		else if (e.getSource() == bFindFeatures)
		{
			//reset the main canvas view to all its defaults 
			MapViewer.winMain.fatController.resetMainCanvasView();

			//clear the find dialog
			MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().setText("");
				
			//show the find dialog
			MapViewer.winMain.ffDialog.setLocationRelativeTo(winMain);
			
			//////////////////////////////FOR TESTING ONLY////////////////////////////////
			MapViewer.winMain.ffDialog.ffPanel.getFFTextArea().setText("11_10223");
			
			MapViewer.winMain.ffDialog.setVisible(true);
			

		}
		
		//show the features in range dialog
		else if (e.getSource() == bFindFeaturesinRange)
		{
			//reset the main canvas view to all its defaults 
			MapViewer.winMain.fatController.resetMainCanvasView();

			//clear the dialog
			FindFeaturesInRangeDialog featuresInRangeDialog = MapViewer.winMain.ffInRangeDialog; 
			
			featuresInRangeDialog.ffInRangePanel.getIntervalStartTextField().setText("");
			featuresInRangeDialog.ffInRangePanel.getIntervalEndTextField().setText("");
			featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(0);
			featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(0);
						
			////////////////////////////////THIS BIT JUST FOR EASE OF TESTING////////////////////////////////
			//TODO: remove code used for testing only
			featuresInRangeDialog.ffInRangePanel.getGenomeCombo().setSelectedIndex(1);
			featuresInRangeDialog.ffInRangePanel.getChromoCombo().setSelectedIndex(2);
			featuresInRangeDialog.ffInRangePanel.getIntervalStartTextField().setText("12");
			featuresInRangeDialog.ffInRangePanel.getIntervalEndTextField().setText("23");
			
			//show the dialog
			featuresInRangeDialog.setLocationRelativeTo(winMain);
			featuresInRangeDialog.setVisible(true);
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