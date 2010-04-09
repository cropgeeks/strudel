package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;
import scri.commons.gui.*;

public class ZoomControlPanel extends JToolBar implements ChangeListener, ActionListener, MouseListener
{
	// ===============================================vars=======================================

	WinMain winMain;
	JLabel label;
	public JSlider zoomSlider;
	JButton resetButton;
	GMapSet gMapSet;
	public JToggleButton showAllMarkersButton;
	JButton scrollUpButton, scrollDownButton;

	boolean scrollContinuously = false;


	// ===================================================curve'tor====================================

	public ZoomControlPanel(WinMain winMain,GMapSet gMapSet, boolean addFiller)
	{
		super();

		this.winMain = winMain;
		this.gMapSet = gMapSet;
		gMapSet.zoomControlPanel = this;

		setFloatable(false);
		setBorderPainted(false);

		setupComponents(addFiller);
	}

	// ==============================================methods====================================

	private void setupComponents(boolean addFiller)
	{
		//settings for the slider
		int sliderMax = Constants.MAX_ZOOM_FACTOR;
		int sliderMin = 1;
		int sliderInitialVal = 1;

		//label
		label = new JLabel(Icons.getIcon("ZOOM"));

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

		//reset button
		resetButton = new JButton(Icons.getIcon("RESET"));
		resetButton.setToolTipText("Reset zoom");
		resetButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			resetButton.setMargin(new Insets(2, 1, 2, 1));

		//marker display button
		showAllMarkersButton = (JToggleButton) Utils.getButton(true, "", "Always show all markers", Icons.getIcon("SHOWMARKERS"), this, true);

		//scroll buttons
		scrollUpButton = new JButton(Icons.getIcon("UPARROW"));
//		scrollUpButton.addMouseListener(new ScrollButtonMouseListener(true));
		scrollUpButton.setToolTipText("Scroll up by one screen or hold for continuous fast scrolling");
		scrollUpButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			scrollUpButton.setMargin(new Insets(2, 1, 2, 1));

		scrollDownButton = new JButton(Icons.getIcon("DOWNARROW"));
//		scrollDownButton.addMouseListener(new ScrollButtonMouseListener(false));
		scrollDownButton.setToolTipText("Scroll down by one screen or hold for continuous fast scrolling");
		scrollDownButton.addActionListener(this);
		if (scri.commons.gui.SystemUtils.isMacOS() == false)
			scrollDownButton.setMargin(new Insets(2, 1, 2, 1));

		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
		if(addFiller)
			add(Box.createHorizontalGlue());

		//add the components
		add(new JLabel("   "));
		add(label);
		add(new JLabel("   "));
		add(zoomSlider);
		add(resetButton);
		add(showAllMarkersButton);
		add(scrollUpButton);
		add(scrollDownButton);
		add(new JLabel("   "));

		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
		if(addFiller)
			add(Box.createHorizontalGlue());

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();
		if (source.equals(zoomSlider) && !winMain.mainCanvas.zoomHandler.isClickZoomRequest && !winMain.mainCanvas.zoomHandler.isPanZoomRequest)
		{
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, gMapSet, true);
			updateSlider();
		}
	}


	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void updateSlider()
	{
		//update the slider
		zoomSlider.setValue(Math.round(gMapSet.zoomFactor));
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==resetButton)
		{
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
		}
		else if(e.getSource() == showAllMarkersButton)
		{
			if(showAllMarkersButton.isSelected())
				gMapSet.paintAllMarkers = true;
			else
				gMapSet.paintAllMarkers = false;

			Strudel.winMain.mainCanvas.updateCanvas(true);
		}

		else if(e.getSource() == scrollUpButton)
		{
			//check whether all maps in the mapset are visible -- if yes, do not scroll
			if(gMapSet.getVisibleMaps().size() < gMapSet.gMaps.size())
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				Strudel.winMain.mainCanvas.scroll(true, gMapSet, scrollIncrement);
			}
		}
		else if(e.getSource() == scrollDownButton)
		{
			//check whether all maps in the mapset are visible -- if yes, do not scroll
			if(gMapSet.getVisibleMaps().size() < gMapSet.gMaps.size())
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				Strudel.winMain.mainCanvas.scroll(false, gMapSet, scrollIncrement);
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

	class ScrollButtonMouseListener implements MouseListener
	{
		boolean up = false;

		ScrollButtonMouseListener(boolean up)
		{
			this.up = up;
		}

		public void mousePressed(MouseEvent e)
		{
			scrollContinuously = true;
			scrollContinuously(up);
		}


		public void mouseReleased(MouseEvent e)
		{
			scrollContinuously = false;
		}

		public void mouseClicked(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------


	private void scrollContinuously(boolean up)
	{
		while(scrollContinuously)
		{
			if(up)
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				int newCenterPoint = gMapSet.centerPoint  - scrollIncrement;
				Strudel.winMain.mainCanvas.scroll(true, gMapSet, scrollIncrement);
			}
			else
			{
				int scrollIncrement = Strudel.winMain.mainCanvas.getHeight();
				int newCenterPoint = gMapSet.centerPoint + scrollIncrement;
				Strudel.winMain.mainCanvas.scroll(false, gMapSet, scrollIncrement);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
