package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class ControlToolBar extends JToolBar implements ActionListener
{
	private final WinMain winMain;

	public JButton bOpen;
	public JButton bExport;
	public JToggleButton bOverview;
	public JButton bHelp;
	public JLabel scoreLabel;
	public JSpinner scoreSpinner;
	FormattedTextFieldVerifier eValueSpinnerInputVerifier;
	public JButton bShowTable;
	public JButton bFindFeaturesinRange;
	public JButton bResetAll;
	//	public JToggleButton bDistMarkers;
	//	public JButton bCurves;
	//	public JToggleButton bLinkFilter;
	public JButton bInfo;
	public JButton bSave;
	public JLabel memLabel = new JLabel();
	public JButton bConfigureGenomes;
	//	public JToggleButton bAntialias;
	private JButton bColours;
	public JButton bConfigureView;

	public int currentLinkShapeType = 1;
	public boolean linkShapeOrderAscending = true;


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
		add(bShowTable);
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
		add(scoreLabel);
		add(scoreSpinner);

		addSeparator(true);
		add(bHelp);
		add(bInfo);
		addSeparator(true);
		add(memLabel);
		add(new JLabel("  "));

	}

	private void createControls()
	{
		//the score spinner
		scoreLabel = new JLabel("Score Cut-off: ");
		scoreSpinner = new JSpinner();
		scoreSpinner.setValue(LinkDisplayManager.getScoreThresholdExponent());
		scoreSpinner.setMaximumSize(new Dimension(100, 20));
//		//Tweak the spinner's formatted text field.
//		JFormattedTextField ftf = getTextField(scoreSpinner);
//		if (ftf != null)
//		{
//			ftf.setColumns(8); // specify the number of columns we need
//		}

		//add the change listener to the score spinner
		scoreSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				eValueSpinnerStateChanged(evt);
			}
		});

		scoreSpinner.addMouseListener(new MouseAdapter() {
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
		scoreLabel.setEnabled(false);
		scoreSpinner.setEnabled(false);

		//configure open file dialog button
		bOpen = (JButton) Utils.getButton(false, "Load Data", "Load data into Strudel", Icons.getIcon("FILEOPEN"), Actions.loadData);
		
		//configure export image button
		bExport = (JButton) Utils.getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"), Actions.exportImage);

		//configure save table data button
		bSave = (JButton) Utils.getButton(false, "", "Save results table to file", Icons.getIcon("SAVE"), Actions.saveResults);

		//configure find features button
		bShowTable = (JButton) Utils.getButton(false, "Search Feature Table", "Filterable table of features", Icons.getIcon("FIND"), Actions.showTable);

		//configure find features in range button
		bFindFeaturesinRange = (JButton) Utils.getButton(false, "Explore Range", "List features in range", Icons.getIcon("RANGE"), Actions.exploreRange);

		//these buttons have no keyboard shortcuts associated with them as yet -- straightforward config
		bConfigureGenomes = (JButton) Utils.getButton(false, "Configure Datasets", "Configure ordering and visibility of datasets", Icons.getIcon("CONFIGURE"), Actions.configureDatasets);
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



	void toggleOverviewDialog()
	{
		// Toggle the state
		Prefs.guiOverviewVisible = !Prefs.guiOverviewVisible;

		// Then set the toolbar button and dialog to match
		bOverview.setSelected(Prefs.guiOverviewVisible);
		winMain.overviewDialog.setVisible(Prefs.guiOverviewVisible);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------

	public void initScoreSpinner()
	{
		DataSet dataSet = Strudel.winMain.dataSet;
		
		//set the value of the score spinner to the worst value for the current dataset so no link filtering happens
		if(dataSet.dataFormat == Constants.FILEFORMAT_STRUDEL)
		{
			scoreLabel.setText("E-value Cut-off: 1.00E");
		
			//make a new datamodel with default settings
			scoreSpinner.setModel(new SpinnerNumberModel());
			
			//init the spinner value
			initScoreSpinnerForEValues();
		}
		else if(dataSet.dataFormat == Constants.FILEFORMAT_MAF)
		{
			scoreLabel.setText("Score Cut-off: ");
			
			//make a new datamodel based on the max and min values we have in this dataset
			int stepsize = 10000;
			SpinnerNumberModel model =  new SpinnerNumberModel(
							dataSet.minimumScore - stepsize, //initial value
							dataSet.minimumScore - stepsize, //min
							dataSet.maximumScore + stepsize, //max
							stepsize);                //step
			scoreSpinner.setModel(model);
			
			//init the spinner value
			initScoreSpinnerForIntegerScores();
		}		
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void initScoreSpinnerForIntegerScores()
	{
		//for this type of score, smallest are worst, and we want these because then no links get filtered out initially
		LinkDisplayManager.homologyScoreThreshold = Strudel.winMain.dataSet.minimumScore;
		scoreSpinner.setValue(((SpinnerNumberModel)scoreSpinner.getModel()).getMinimum());
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void initScoreSpinnerForEValues()
	{
		//init the spinner value		
		int adjustedExponent = 0;
		if(Strudel.winMain.dataSet.worstScoreExponent > 0)
			adjustedExponent = Strudel.winMain.dataSet.worstScoreExponent + 1;
		else if(Strudel.winMain.dataSet.worstScoreExponent < 0)
			adjustedExponent = Strudel.winMain.dataSet.worstScoreExponent - 1;
		
		LinkDisplayManager.setScoreThresholdWithExponent(adjustedExponent);
		Strudel.winMain.toolbar.scoreSpinner.setValue(adjustedExponent);
	}
	
	//	--------------------------------------------------------------------------------------------------------------------------------------------------------
	


	private void eValueSpinnerStateChanged(javax.swing.event.ChangeEvent e)
	{
		JSpinner source = (JSpinner) e.getSource();
		Number spinnerValue = (Number)source.getValue();

		if(Strudel.winMain.dataSet.dataFormat == Constants.FILEFORMAT_STRUDEL)
		{
			//convert the value selected to the exponent of a small decimal and set this as
			//the new BLAST threshold (which is a double)			
			LinkDisplayManager.setScoreThresholdWithExponent(spinnerValue.intValue());
		}
		else if(Strudel.winMain.dataSet.dataFormat == Constants.FILEFORMAT_MAF)
		{
			LinkDisplayManager.homologyScoreThreshold = spinnerValue.doubleValue();
		}
		
		if(winMain.mainCanvas != null)
			winMain.mainCanvas.updateCanvas(true);
	}

	public void enableControls(boolean singleGenomeMode)
	{
		try
		{
			if(singleGenomeMode)
			{
				scoreLabel.setEnabled(false);
				scoreSpinner.setEnabled(false);
				bResetAll.setEnabled(true);
				bConfigureGenomes.setEnabled(false);
				Actions.openedData();
			}
			else
			{
				scoreLabel.setEnabled(true);
				scoreSpinner.setEnabled(true);
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
}