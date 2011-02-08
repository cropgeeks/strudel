package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class ZoomControlPanel extends JToolBar implements ChangeListener, ActionListener, MouseListener
{
	// ===============================================vars=======================================

	JLabel zoomIcon;
	public JSlider zoomSlider;
	JButton resetButton;
	public GMapSet selectedSet;
	public JToggleButton overrideMarkersAutoDisplayButton;
	JButton scrollUpButton, scrollDownButton;
	public JSpinner maxZoomSpinner;
	FormattedTextFieldVerifier maxZoomSpinnerInputVerifier;

	public int componentsTotalWidth = 0;
	public int maxComponentHeight = 0;
	public int maxComponentWidth = 0;
	
	public boolean programmaticZoomSpinnerChange = false;
	
	// ===================================================curve'tor====================================

	public ZoomControlPanel()
	{
		super();

		setFloatable(false);
//		setBorderPainted(true);

		setupComponents();
	}

	// ==============================================methods====================================

	private void setupComponents()
	{
		//settings for the slider
		int sliderMax = Constants.MAX_ZOOM_FACTOR;
		int sliderMin = 1;
		int sliderInitialVal = 1;

		//label
		zoomIcon = new JLabel(Icons.getIcon("ZOOM"));

		//zoom slider
		zoomSlider = new JSlider(sliderMin, sliderMax, sliderInitialVal);
		zoomSlider.setToolTipText("Zoom this genome in or out");
		zoomSlider.addChangeListener(this);
		//we need the mouse listener so we can have the canvas repainted with antialias on when the mouse button is released
		//this is the way it happens for all other cases where we need a pretty repaint
		zoomSlider.addMouseListener(this);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMinorTickSpacing(sliderMax/20);
		zoomSlider.setMajorTickSpacing(sliderMax/10);
		
		//this control allows users to choose their own max zoom value
		maxZoomSpinner = new JSpinner();
		maxZoomSpinner.setValue(Constants.MAX_ZOOM_FACTOR);
		maxZoomSpinner.setMaximumSize(new Dimension(100, 20));

		maxZoomSpinnerInputVerifier = new FormattedTextFieldVerifier("The value entered here must be positive.",0, false);
		((JSpinner.DefaultEditor) maxZoomSpinner.getEditor()).getTextField().setInputVerifier(maxZoomSpinnerInputVerifier);
		maxZoomSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				maxZoomSpinnerStateChanged(evt);
			}
		});

		//reset button
		resetButton = new JButton(Icons.getIcon("RESET"));
		resetButton.setToolTipText("Reset zoom");
		resetButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			resetButton.setMargin(new Insets(2, 1, 2, 1));

		//marker display button
		overrideMarkersAutoDisplayButton = (JToggleButton) Utils.getButton(true, "", "Always show all features", Icons.getIcon("SHOWMARKERS"), this, true);

		//scroll buttons
		scrollUpButton = new JButton(Icons.getIcon("UPARROW"));
		scrollUpButton.setToolTipText("Scroll up by one screen");
		scrollUpButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			scrollUpButton.setMargin(new Insets(2, 1, 2, 1));

		scrollDownButton = new JButton(Icons.getIcon("DOWNARROW"));
		scrollDownButton.setToolTipText("Scroll down by one screen");
		scrollDownButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			scrollDownButton.setMargin(new Insets(2, 1, 2, 1));
		
		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
//		add(Box.createHorizontalGlue());

		//add the components
		//all of these are zoom related
		add(zoomIcon);
		add(new JLabel("  "));
		add(zoomSlider);
		add(new JLabel("  "));
		add(new JLabel("Max. zoom factor:"));
		add(maxZoomSpinner);
		add(new JLabel("  "));
		add(resetButton);
		//the rest of the components
		add(new JLabel("  "));
		add(overrideMarkersAutoDisplayButton);
		add(new JLabel("  "));
		add(scrollUpButton);
		add(scrollDownButton);

		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
//		add(Box.createHorizontalGlue());
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//called when the zoom slider has been moved
	public void stateChanged(ChangeEvent e)
	{
		if(selectedSet == null)
		{
			if(zoomSlider.isFocusOwner())
				TaskDialog.info("Please select a genome", "Close");	
			return;
		}
			
		JSlider source = (JSlider) e.getSource();
		if (source.equals(zoomSlider) && !Strudel.winMain.mainCanvas.zoomHandler.isClickZoomRequest && !Strudel.winMain.mainCanvas.zoomHandler.isPanZoomRequest)
		{
			Strudel.winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, selectedSet, true);
			updateSlider();
		}	
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//updates the zoom controls to relfect the fact that we have selected a different mapset by clicking on a genome label
	public void updateControlsToMapsetSettings(GMapSet selectedSet)
	{
		this.selectedSet = selectedSet;
		//also update the max zoom factor spinner
		//we need to flag up the fact that this is done from within the code rather than the spinner itself
		programmaticZoomSpinnerChange = true;
		maxZoomSpinner.setValue(selectedSet.maxZoomFactor);
		programmaticZoomSpinnerChange = false;
		//need to update the zoom slider to reflect the current zoom factor of the now selected mapset
		updateSlider();
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//called when the zoom value spinner has been used
	private void maxZoomSpinnerStateChanged(javax.swing.event.ChangeEvent e)
	{
		if(!programmaticZoomSpinnerChange)
		{
			if(selectedSet == null)
			{
				TaskDialog.info("Please select a genome", "Close");	
				return;
			}
			
			JSpinner source = (JSpinner) e.getSource();
			selectedSet.maxZoomFactor = (Integer)source.getValue();
			zoomSlider.setMaximum(selectedSet.maxZoomFactor);
			updateSlider();
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void updateSlider()
	{
		if (selectedSet != null)
		{
			//update the slider
			zoomSlider.setValue(Math.round(selectedSet.zoomFactor));
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		
		if(selectedSet == null)
		{
			TaskDialog.info("Please select a genome", "Close");	
			return;
		}
		
		if(e.getSource()==resetButton)
		{
			Strudel.winMain.mainCanvas.zoomHandler.processZoomResetRequest(selectedSet);
		}
		else if(e.getSource() == overrideMarkersAutoDisplayButton)
		{
			if(overrideMarkersAutoDisplayButton.isSelected())
				selectedSet.overrideMarkersAutoDisplay = true;
			else
				selectedSet.overrideMarkersAutoDisplay = false;

			Strudel.winMain.mainCanvas.updateCanvas(true);
		}
		else if(e.getSource() == scrollUpButton)
		{
			//check whether we are fully zoomed out if yes, do not scroll
			if(selectedSet.zoomFactor > 1)
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				Strudel.winMain.mainCanvas.scroll(true, selectedSet, scrollIncrement);
			}
		}
		else if(e.getSource() == scrollDownButton)
		{
			//check whether we are fully zoomed out if yes, do not scroll
			if(selectedSet.zoomFactor > 1)
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				Strudel.winMain.mainCanvas.scroll(false, selectedSet, scrollIncrement);
			}
		}
	}



	public void mouseReleased(MouseEvent e)
	{
		Strudel.winMain.mainCanvas.updateCanvas(true);
	}

	//these are currently not needed
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
