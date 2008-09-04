package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import sbrn.mapviewer.gui.entities.*;

public class ZoomControlPanel extends JPanel implements ChangeListener, ActionListener
{
	// ===============================================vars=======================================

	WinMain winMain;
	JLabel leftLabel;
	JLabel rightLabel;
	public JLabel leftZoomFactorLabel;
	public JLabel rightZoomFactorLabel;
	JSlider leftZoomSlider;
	JSlider rightZoomSlider;
	JButton resetLeftButton;
	JButton resetRightButton;

	// ===================================================c'tor====================================

	public ZoomControlPanel(WinMain winMain)
	{
		super();
		this.winMain = winMain;
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
		leftLabel = new JLabel();
		leftLabel.setText("Zoom:");
		leftPanel.add(leftLabel);
		leftZoomSlider = new JSlider(sliderMin, sliderMax, sliderInitialVal);
		// add it
		leftZoomSlider.addChangeListener(this);
		leftPanel.add(leftZoomSlider);
		//reset button
		resetLeftButton = new JButton("Reset");
		resetLeftButton.addActionListener(this);
		leftPanel.add(resetLeftButton);
		// add the panel
		this.add(leftPanel);

		// right hand control
		JPanel rightPanel = new JPanel();
		//label
		rightLabel = new JLabel();
		rightLabel.setText("Zoom:");
		rightPanel.add(rightLabel);
		rightZoomSlider = new JSlider(sliderMin, sliderMax, sliderInitialVal);
		// add it
		rightZoomSlider.addChangeListener(this);
		rightPanel.add(rightZoomSlider);
		//reset button
		resetRightButton = new JButton("Reset");
		resetRightButton.addActionListener(this);
		rightPanel.add(resetRightButton);
		// add the panel
		this.add(rightPanel);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();
			if (source.equals(leftZoomSlider))
			{
				winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, 0, true);
			}
			else
				if (source.equals(rightZoomSlider))
				{
					winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, 1, true);

				}
			updateSliders();
//		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void updateSliders()
	{
		//update the sliders
		leftZoomSlider.setValue((int) winMain.mainCanvas.targetGMapSet.zoomFactor);
		rightZoomSlider.setValue((int) winMain.mainCanvas.referenceGMapSet.zoomFactor);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==resetLeftButton)
		{
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(winMain.mainCanvas.targetGMapSet);
		}
		if(e.getSource()==resetRightButton)
		{
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(winMain.mainCanvas.referenceGMapSet);
		}
		updateSliders();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
