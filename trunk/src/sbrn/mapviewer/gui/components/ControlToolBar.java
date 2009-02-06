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
	
	private JButton bOpen;
	private JButton bExport;
	private JToggleButton bOverview;
	private JButton bHelp;
	private JLabel blastLabel, blastScoreLabel;
	private JSlider eValueSlider;
	public JButton bFindFeatures;
	public JButton bFindFeaturesinRange;
	private JButton bResetAll;
	private JToggleButton bDistMarkers;
	public JButton bCurves;
	private JToggleButton bAntialias;
	private JToggleButton bLinkFilter;
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
		
		//this group of buttons is related to higher level tasks in the app such as data import and export
		add(bOpen);		
		add(bExport);
		
		addSeparator(true);
		
		add(bOverview);
		add(bDistMarkers);
		add(bCurves);
		add(bAntialias);
		add(bLinkFilter);
		
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
		
		bAntialias.setSelected(Prefs.userPrefAntialias);
		bLinkFilter.setSelected(Prefs.drawOnlyLinksToVisibleFeatures);
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
		
		//for a few of the buttons we want Ctrl based keyboard shortcuts
		//this requires some crazy configuration code, sadly
		
		//configure open file dialog button
		OpenFileDialogAction openFileDialogAction = new OpenFileDialogAction();
		bOpen = (JButton) getButton(false, "", "Load data into Mapviewer", Icons.getIcon("FILEOPEN"), openFileDialogAction);
		KeyStroke ctrlOKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		bOpen.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlOKeyStroke, "openFileDialog");
		bOpen.getActionMap().put("openFileDialog", openFileDialogAction);
	
		//configure export image button
		ExportImageAction exportImageAction = new ExportImageAction();
		bExport = (JButton) getButton(false, "", "Export the display as an image", Icons.getIcon("EXPORTIMAGE"), exportImageAction);
		KeyStroke ctrlEKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK);
		bExport.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlEKeyStroke, "exportImage");
		bExport.getActionMap().put("exportImage", exportImageAction);
		
		//configure find features button
		FindFeaturesAction findFeaturesAction = new FindFeaturesAction();
		bFindFeatures = (JButton) getButton(false, "", "Find features by name", Icons.getIcon("FIND"), findFeaturesAction);
		KeyStroke ctrlFKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
		bFindFeatures.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlFKeyStroke, "findFeatures");
		bFindFeatures.getActionMap().put("findFeatures", findFeaturesAction);
		
		//configure find features in range button
		FindFeaturesInRangeAction findFeaturesInRangeAction = new FindFeaturesInRangeAction();
		bFindFeaturesinRange = (JButton) getButton(false, "", "List features in range", Icons.getIcon("RANGE"), findFeaturesInRangeAction);
		KeyStroke ctrlRKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
		bFindFeaturesinRange.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(ctrlRKeyStroke, "findFeaturesInRange");
		bFindFeaturesinRange.getActionMap().put("findFeaturesInRange", findFeaturesInRangeAction);
		
		//these buttons have no keyboard shortcuts associated with them as yet -- straightforward config
		bOverview = (JToggleButton) getButton(true, "", "Toggle the overview dialog on or off", Icons.getIcon("OVERVIEW"), null);
		bOverview.setSelected(Prefs.guiOverviewVisible);
		bDistMarkers = (JToggleButton) getButton(true, "", "Toggle the distance markers on or off", Icons.getIcon("DISTANCEMARKERS"), null);
		bCurves = (JButton) getButton(false, "", "Cycle through straight, angled and curved links", Icons.getIcon("CURVES"), null);
		bAntialias = (JToggleButton) getButton(true, "", "Toggle between antialiased and plain drawing styles", Icons.getIcon("ANTIALIAS"), null);
		bLinkFilter = (JToggleButton) getButton(true, "", "Toggle between visibility-based filtering of links and no filtering", Icons.getIcon("LINKFILTER"), null);		
		bHelp =  (JButton) getButton(false, "", "Help", Icons.getIcon("HELP"), null);
		bResetAll =  (JButton) getButton(false, "", "Reset display", Icons.getIcon("RESET"), null);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOverview)
			toggleOverviewDialog();
		
		//toggles the distance markers on or off
		else if(e.getSource() == bDistMarkers)
		{
			if(bDistMarkers.isSelected())
			{
				MapViewer.winMain.mainCanvas.drawDistanceMarkers = true;
			}
			else
			{
				MapViewer.winMain.mainCanvas.drawDistanceMarkers = false;
			}
			MapViewer.winMain.mainCanvas.updateCanvas(true);
		}
		
		//toggle between antialias and none
		else if(e.getSource() == bAntialias)
		{
			Prefs.userPrefAntialias = bAntialias.isSelected();
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
	private AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon, Action action)
	{
		AbstractButton button = null;
		
		if (toggle)
			button = new JToggleButton(action);
		else
			button = new JButton(action);
		
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
		
}