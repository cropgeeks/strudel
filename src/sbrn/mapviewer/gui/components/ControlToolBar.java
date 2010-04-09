package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class ControlToolBar extends JToolBar implements ActionListener, ItemListener
{
	private final WinMain winMain;

	public JButton bOpen;
	public JButton bExport;
	public JToggleButton bOverview;
	public JButton bHelp;
	public JLabel blastLabel;
	public JSpinner eValueSpinner;
	FormattedTextFieldVerifier eValueSpinnerInputVerifier;
	public JButton bFindFeatures;
	public JButton bFindFeaturesinRange;
	public JButton bResetAll;
	public JButton bInfo;
	public JButton bSave;
	public JLabel memLabel = new JLabel();
	public JButton bConfigureGenomes;
	private JButton bColours;
	public JButton bConfigureView;

	public int currentLinkShapeType = 1;
	public boolean linkShapeOrderAscending = true;

	//a checkbox for asking the user whether they want to be reminded each time they have reached the max zoom level through pan zooming
	public JCheckBox renderAsOneChromoCheckBox = new JCheckBox("Don't show this dialog again");


	ControlToolBar(WinMain winMain)
	{
		this.winMain = winMain;

		setFloatable(false);
		setBorderPainted(false);

		createControls();

		if (SystemUtils.isMacOS() == false)
			add(new JLabel("  "));

		//buttons
		add(bOpen);
		add(bConfigureGenomes);

		addSeparator(true);
		add(bFindFeatures);
		add(bFindFeaturesinRange);

		addSeparator(true);
		add(bResetAll);

		addSeparator(true);
		add(bExport);
		add(bSave);

		addSeparator(true);
		add(bOverview);
		add(bColours);
		add(bConfigureView);

		addSeparator(true);
		add(blastLabel);
		add(eValueSpinner);

		addSeparator(true);
		add(bHelp);
		add(bInfo);
		addSeparator(true);
		add(memLabel);
		add(new JLabel("  "));

		//configure the maxZoomMessageCheckBox
		renderAsOneChromoCheckBox.addItemListener(this);
		if(Prefs.showRenderAsOneMessage)
			renderAsOneChromoCheckBox.setSelected(false);
		else
			renderAsOneChromoCheckBox.setSelected(true);

	}

	private void createControls()
	{
		blastLabel = new JLabel("BLAST Cut-off: 1.00E");
		eValueSpinner = new JSpinner();
		eValueSpinner.setValue(LinkDisplayManager.getBlastThresholdExponent());
		eValueSpinner.setMaximumSize(new Dimension(60, 20));

		eValueSpinnerInputVerifier = new FormattedTextFieldVerifier("The e-value exponent must be 0 or less.",0, true);
		((JSpinner.DefaultEditor) eValueSpinner.getEditor()).getTextField().setInputVerifier(eValueSpinnerInputVerifier);
		eValueSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				eValueSpinnerStateChanged(evt);
			}
		});

		eValueSpinner.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(winMain.mainCanvas != null)
				{
					winMain.mainCanvas.updateCanvas(true);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(winMain.mainCanvas != null)
				{
					winMain.mainCanvas.updateCanvas(true);
				}
			}
		});

		//disable the BLAST slider and label on startup initially
		blastLabel.setEnabled(false);
		eValueSpinner.setEnabled(false);

		//for a few of the buttons we want Ctrl based keyboard shortcuts

		//configure open file dialog button
		bOpen = (JButton) Utils.getButton(false, "Load Data", "Load data into Strudel", Icons.getIcon("FILEOPEN"), Actions.loadData);

		//configure export image button
		bExport = (JButton) Utils.getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"), Actions.exportImage);

		//configure save table data button
		bSave = (JButton) Utils.getButton(false, "", "Save results table to file", Icons.getIcon("SAVE"), Actions.saveResults);

		//configure find features button
		bFindFeatures = (JButton) Utils.getButton(false, "Find", "Find features by name", Icons.getIcon("FIND"), Actions.findFeature);

		//configure find features in range button
		bFindFeaturesinRange = (JButton) Utils.getButton(false, "Explore Range", "List features in range", Icons.getIcon("RANGE"), Actions.exploreRange);

		//these buttons have no keyboard shortcuts associated with them as yet -- straightforward config
		bConfigureGenomes = (JButton) Utils.getButton(false, "Configure datasets", "Configure ordering and visibility of datasets", Icons.getIcon("CONFIGURE"), Actions.configureDatasets);
		bOverview = (JToggleButton) Utils.getButton(true, "", "Toggle the overview dialog on or off", Icons.getIcon("OVERVIEW"), Actions.showOverview);
		bOverview.setSelected(Prefs.guiOverviewVisible);
		bHelp =  (JButton) Utils.getButton(false, "", "Help", Icons.getIcon("HELP"), Actions.help);
		bInfo =  (JButton) Utils.getButton(false, "", "About Strudel", Icons.getIcon("INFO"), Actions.about);
		bResetAll =  (JButton) Utils.getButton(false, "Reset", "Reset display", Icons.getIcon("RESET"), Actions.reset);
		bColours = (JButton) Utils.getButton(false, "", "Pick between, and customise, two colour schemes", Icons.getIcon("COLOURS"), Actions.customiseColours);
		bConfigureView = (JButton) Utils.getButton(false, "", "Configure view settings", Icons.getIcon("CONFIGUREVIEW"), this, false);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == bConfigureView)
		{
			Strudel.winMain.configureViewSettingsDialog.setLocationRelativeTo(Strudel.winMain);
			Strudel.winMain.configureViewSettingsDialog.setVisible(true);
		}
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

	private void eValueSpinnerStateChanged(javax.swing.event.ChangeEvent e)
	{
		JSpinner source = (JSpinner) e.getSource();

		//convert the value selected to the exponent of a small decimal and set this as
		//the new BLAST threshold (which is a double)
		int exponent = (Integer)source.getValue();

		eValueSpinnerInputVerifier.verify(source);

		if(exponent > 0)
		{
			TaskDialog.error("BLAST threshold exponent must be 0 or less.", "Close");
			source.setValue(0);
			return;
		}

		LinkDisplayManager.setBlastThresholdWithExponent(exponent);
		winMain.mainCanvas.updateCanvas(true);

	}

	public void enableControls(boolean singleGenomeMode)
	{
		try
		{
			if(singleGenomeMode)
			{
				blastLabel.setEnabled(false);
				eValueSpinner.setEnabled(false);
				bResetAll.setEnabled(true);
				bConfigureGenomes.setEnabled(false);
				Actions.openedData();
			}
			else
			{
				blastLabel.setEnabled(true);
				eValueSpinner.setEnabled(true);
				bResetAll.setEnabled(true);
				//this button we want to be always disabled at the lowest zoom level as we don't want markers displayed then
				//it becomes enabled when a chromosome is fitted on screen
				bConfigureGenomes.setEnabled(true);
				bConfigureView.setEnabled(true);
				Actions.openedData();
			}

		}
		catch (Exception e)	{}
	}

	//here we handle user actions for the checkbox that decides whether we show the warning message re overview rendering each time
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource().equals(renderAsOneChromoCheckBox))
		{
			if(renderAsOneChromoCheckBox.isSelected())
				Prefs.showRenderAsOneMessage = false;
			else
				Prefs.showRenderAsOneMessage = true;
		}

	}
}