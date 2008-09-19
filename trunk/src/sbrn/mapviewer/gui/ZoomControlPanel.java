package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import sbrn.mapviewer.gui.entities.*;

public class ZoomControlPanel extends JPanel implements ChangeListener, ActionListener
{
	// ===============================================vars=======================================

	WinMain winMain;
	JLabel label;
	JSlider zoomSlider;
	JButton resetButton;
	GMapSet gMapSet;


	// ===================================================c'tor====================================

	public ZoomControlPanel(WinMain winMain,GMapSet gMapSet)
	{
		super();
		this.winMain = winMain;
		this.gMapSet = gMapSet;
		setupComponents();
	}

	// ==============================================methods====================================

	private void setupComponents()
	{
		this.setLayout(new GridLayout(1, 2));

		int sliderMin = 1;
		int sliderMax = 500;
		int sliderInitialVal = 1;

		// left hand control
		JPanel leftPanel = new JPanel();
		//label
		label = new JLabel();
		label.setText("Zoom:");
		leftPanel.add(label);
		zoomSlider = new JSlider(sliderMin, sliderMax, sliderInitialVal);
		// add it
		zoomSlider.addChangeListener(this);
		leftPanel.add(zoomSlider);
		//reset button
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		leftPanel.add(resetButton);
		// add the panel
		this.add(leftPanel);

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();
		if (source.equals(zoomSlider))
		{
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, gMapSet, true);
		}
		updateSliders();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void updateSliders()
	{
		//update the sliders
		zoomSlider.setValue((int) gMapSet.zoomFactor);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==resetButton)
		{
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
		}

		updateSliders();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
