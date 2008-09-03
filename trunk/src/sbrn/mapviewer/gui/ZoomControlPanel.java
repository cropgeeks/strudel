package sbrn.mapviewer.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

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
		int sliderMax = 1000;
		int sliderInitialVal = 1;
		int minorTickSpacing = 100;
		int majorTickSpacing = 250;

		// left hand control
		JPanel leftPanel = new JPanel();
		//label
		leftLabel = new JLabel();
		leftLabel.setText("Zoom:");
		leftPanel.add(leftLabel);
		leftZoomSlider = new JSlider(sliderMin, sliderMax, sliderInitialVal);
		// tick marks
		leftZoomSlider.setMajorTickSpacing(majorTickSpacing);
		leftZoomSlider.setMinorTickSpacing(minorTickSpacing);
		leftZoomSlider.setPaintTicks(true);
		// labels
		Hashtable leftLabels = leftZoomSlider.createStandardLabels(majorTickSpacing, majorTickSpacing);
		leftZoomSlider.setLabelTable(leftLabels);
		leftZoomSlider.setPaintLabels(true);
		// add it
		leftZoomSlider.addChangeListener(this);
		leftPanel.add(leftZoomSlider);
		// add another label to print out the current zoom factor
		leftZoomFactorLabel = new JLabel(" x 1");
		leftZoomFactorLabel.setPreferredSize(new Dimension(60,10));
		leftPanel.add(leftZoomFactorLabel);
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
		// tick marks
		rightZoomSlider.setMajorTickSpacing(majorTickSpacing);
		rightZoomSlider.setMinorTickSpacing(minorTickSpacing);
		rightZoomSlider.setPaintTicks(true);
		// labels
		Hashtable rightLabels = rightZoomSlider.createStandardLabels(majorTickSpacing, majorTickSpacing);
		rightZoomSlider.setLabelTable(rightLabels);
		rightZoomSlider.setPaintLabels(true);
		// add it
		rightZoomSlider.addChangeListener(this);
		rightPanel.add(rightZoomSlider);
		// add another label to print out the current zoom factor
		rightZoomFactorLabel = new JLabel(" x 1");
		rightZoomFactorLabel.setPreferredSize(new Dimension(60,10));
		rightPanel.add(rightZoomFactorLabel);
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
			updateZoomInfo();
//		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void updateZoomInfo()
	{
		//update the sliders
		leftZoomSlider.setValue((int) winMain.mainCanvas.targetGMapSet.zoomFactor);
		rightZoomSlider.setValue((int) winMain.mainCanvas.referenceGMapSet.zoomFactor);

		//update the labels
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		leftZoomFactorLabel.setText(" x " + nf.format(winMain.mainCanvas.targetGMapSet.zoomFactor));
		rightZoomFactorLabel.setText(" x " +nf.format(winMain.mainCanvas.referenceGMapSet.zoomFactor));
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==resetLeftButton)
		{
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(1, 0, 0, true);
		}
		if(e.getSource()==resetRightButton)
		{
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(1, 0, 1, true);
		}
		updateZoomInfo();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
