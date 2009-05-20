package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.actions.*;
import sbrn.mapviewer.gui.animators.*;
import sbrn.mapviewer.gui.handlers.*;
import scri.commons.gui.*;

public class ControlToolBar extends JToolBar implements ActionListener
{
	private WinMain winMain;
	
	public JButton bOpen;
	public JButton bExport;
	public JToggleButton bOverview;
	public JButton bHelp;
	public JLabel blastLabel, blastScoreLabel;
	public JSlider eValueSlider;
	public JButton bFindFeatures;
	public JButton bFindFeaturesinRange;
	public JButton bResetAll;
	public JToggleButton bDistMarkers;
	public JButton bCurves;
	public JToggleButton bAntialias;
	public JToggleButton bLinkFilter;
	public JButton bInfo;
	public JButton bSave;
	
	
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
		add(bDistMarkers);
		add(bCurves);
		add(bAntialias);
		add(bLinkFilter);
		
		addSeparator(true);	
		add(blastLabel);
		add(eValueSlider);

		addSeparator(true);		
		add(bHelp);
		add(bInfo);
		add(new JLabel("  "));
		
		bAntialias.setSelected(Prefs.userPrefAntialias);
		bLinkFilter.setSelected(Prefs.drawOnlyLinksToVisibleFeatures);
		bDistMarkers.setSelected(Prefs.showDistanceMarkers);
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
				if(winMain.mainCanvas != null)
				{
					winMain.mainCanvas.drawBlastScore = true;
					winMain.mainCanvas.updateCanvas(true);
				}
			}
			
			public void mouseReleased(MouseEvent e)
			{
				if(winMain.mainCanvas != null)
				{
					winMain.mainCanvas.drawBlastScore = false;
					winMain.mainCanvas.updateCanvas(true);
				}
			}
		});
		
		//disable the BLAST slider and label on startup initially
		blastLabel.setEnabled(false);
		eValueSlider.setEnabled(false);
		
		//for a few of the buttons we want Ctrl based keyboard shortcuts
		//this requires some crazy configuration code, sadly
		
		//configure open file dialog button
		OpenFileDialogAction openFileDialogAction = new OpenFileDialogAction();
		bOpen = (JButton) Utils.getButton(false, "Load Data", "Load data into Mapviewer", Icons.getIcon("FILEOPEN"), openFileDialogAction, this, true);
		KeyStroke ctrlOKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		bOpen.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlOKeyStroke, "openFileDialog");
		bOpen.getActionMap().put("openFileDialog", openFileDialogAction);
		
		//configure export image button
		ExportImageAction exportImageAction = new ExportImageAction();
		bExport = (JButton) Utils.getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"), exportImageAction, this, false);
		KeyStroke ctrlEKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK);
		bExport.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlEKeyStroke, "exportImage");
		bExport.getActionMap().put("exportImage", exportImageAction);
		
		//configure save table data button
		SaveTableDataAction saveTableDataAction = new SaveTableDataAction();
		bSave =  (JButton) Utils.getButton(false, "", "Save results table to file", Icons.getIcon("SAVE"), saveTableDataAction, this, false);
		KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
		bSave.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlSKeyStroke, "saveTableData");
		bSave.getActionMap().put("saveTableData", exportImageAction);
		
		//configure find features button
		FindFeaturesAction findFeaturesAction = new FindFeaturesAction();
		bFindFeatures = (JButton) Utils.getButton(false, "Find", "Find features by name", Icons.getIcon("FIND"), findFeaturesAction, this, false);
		KeyStroke ctrlFKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
		bFindFeatures.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlFKeyStroke, "findFeatures");
		bFindFeatures.getActionMap().put("findFeatures", findFeaturesAction);
		
		//configure find features in range button
		FindFeaturesInRangeAction findFeaturesInRangeAction = new FindFeaturesInRangeAction();
		bFindFeaturesinRange = (JButton) Utils.getButton(false, "Explore Range", "List features in range", Icons.getIcon("RANGE"), findFeaturesInRangeAction, this, false);
		KeyStroke ctrlRKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
		bFindFeaturesinRange.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlRKeyStroke, "findFeaturesInRange");
		bFindFeaturesinRange.getActionMap().put("findFeaturesInRange", findFeaturesInRangeAction);
		
		//these buttons have no keyboard shortcuts associated with them as yet -- straightforward config
		bOverview = (JToggleButton) Utils.getButton(true, "", "Toggle the overview dialog on or off", Icons.getIcon("OVERVIEW"), null, this, false);
		bOverview.setSelected(Prefs.guiOverviewVisible);
		bDistMarkers = (JToggleButton) Utils.getButton(true, "", "Toggle the distance markers on or off", Icons.getIcon("DISTANCEMARKERS"), null, this, false);
		bCurves = (JButton) Utils.getButton(false, "", "Cycle through straight, angled and curved links", Icons.getIcon("CURVES"), null, this, false);
		bAntialias = (JToggleButton) Utils.getButton(true, "", "Toggle between higher quality and plain drawing styles", Icons.getIcon("ANTIALIAS"), null, this, false);
		bLinkFilter = (JToggleButton) Utils.getButton(true, "", "Toggle between visibility-based filtering of links and no filtering", Icons.getIcon("LINKFILTER"), null, this, false);		
		bHelp =  (JButton) Utils.getButton(false, "", "Help", Icons.getIcon("HELP"), null, this, true);
		bInfo =  (JButton) Utils.getButton(false, "", "About Strudel", Icons.getIcon("INFO"), null, this, true);
		bResetAll =  (JButton) Utils.getButton(false, "Reset", "Reset display", Icons.getIcon("RESET"), null, this, false);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOverview)
			toggleOverviewDialog();
		
		//toggles the distance markers on or off
		else if(e.getSource() == bDistMarkers)
		{
			Prefs.showDistanceMarkers = bDistMarkers.isSelected();
			MapViewer.winMain.mainCanvas.updateCanvas(true);
		}
		
		//toggle between antialias and none
		else if(e.getSource() == bAntialias)
		{
			Prefs.userPrefAntialias = bAntialias.isSelected();
			MapViewer.winMain.mainCanvas.antiAlias = bAntialias.isSelected();
			MapViewer.winMain.mainCanvas.updateCanvas(true);
		}
		
		//toggle between antialias and none
		else if(e.getSource() == bLinkFilter)
		{
			Prefs.drawOnlyLinksToVisibleFeatures = bLinkFilter.isSelected();
			MapViewer.winMain.mainCanvas.updateCanvas(true);
		}
		
		//toggle the link shape between straight, angled and curved
		else if(e.getSource() == bCurves)
		{		
			MapViewer.logger.fine("\n\n++++++++++MapViewer.winMain.toolbar.currentLinkShapeType before = " + MapViewer.winMain.toolbar.currentLinkShapeType);
			
			//increment the currentLinkShapeType held by the tool bar or decrement as appropriate
			if(linkShapeOrderAscending)
				MapViewer.winMain.toolbar.currentLinkShapeType ++;
			else
				MapViewer.winMain.toolbar.currentLinkShapeType --;
			
			//reset the index of the current link shape type back to 1 if it is greater than the max number so we can keep cycling through the options
			if(currentLinkShapeType >= Constants.NUM_LINKSHAPE_TYPES)
				linkShapeOrderAscending = false;
			if(currentLinkShapeType == 1)
				linkShapeOrderAscending = true;
			
			MapViewer.logger.fine("MapViewer.winMain.toolbar.currentLinkShapeType after = " + MapViewer.winMain.toolbar.currentLinkShapeType);
			
			LinkShapeAnimator linkShapeAnimator = new LinkShapeAnimator(currentLinkShapeType);
			linkShapeAnimator.start();	
		}
		
		//reset the main canvas view and deselect all features
		else if (e.getSource() == bResetAll)
			MapViewer.winMain.fatController.resetMainCanvasView();
		
		//help menu
		else if (e.getSource() == bHelp)
		{
			String url = Constants.strudelHomePage + "strudelManual.pdf";
			
			Utils.visitURL(url);
		}
		
		//"about" dialog
		else if(e.getSource() == bInfo)
		{
			MapViewer.winMain.aboutDialog.setLocationRelativeTo(MapViewer.winMain);
			MapViewer.winMain.aboutDialog.setVisible(true);
		}
		
		else if(e.getSource() == bSave)
		{
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
	
	public void enableControls(boolean singleGenomeMode)
	{
		try
		{
			if(singleGenomeMode)
			{
				blastLabel.setEnabled(false);
				eValueSlider.setEnabled(false);
				bExport.setEnabled(true);
				bOverview.setEnabled(true);
				bFindFeatures.setEnabled(true);
				bFindFeaturesinRange.setEnabled(true);
				bResetAll.setEnabled(true);
				bDistMarkers.setEnabled(true);
				bCurves.setEnabled(false);
				bAntialias.setEnabled(true);
				bLinkFilter.setEnabled(false);
			}
			else
			{
				blastLabel.setEnabled(true);
				eValueSlider.setEnabled(true);
				bExport.setEnabled(true);
				bOverview.setEnabled(true);
				bFindFeatures.setEnabled(true);
				bFindFeaturesinRange.setEnabled(true);
				bResetAll.setEnabled(true);
				bDistMarkers.setEnabled(true);
				bCurves.setEnabled(true);
				bAntialias.setEnabled(true);
				bLinkFilter.setEnabled(true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}