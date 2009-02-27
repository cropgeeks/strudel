package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.entities.*;

public class ZoomControlPanel extends JToolBar implements ChangeListener, ActionListener, MouseListener
{
	// ===============================================vars=======================================
	
	WinMain winMain;
	JLabel label;
	public JSlider zoomSlider;
	JButton resetButton;
	GMapSet gMapSet;
	
	
	// ===================================================c'tor====================================
	
	public ZoomControlPanel(WinMain winMain,GMapSet gMapSet, boolean addFiller)
	{
		super();
		
		this.winMain = winMain;
		this.gMapSet = gMapSet;
		
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
		
		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
		if(addFiller)
			add(Box.createHorizontalGlue());
		
		//add the components
		add(label);
		add(new JLabel(" "));
		add(zoomSlider);
		add(resetButton);
		
		//we need the filler when this toolbar is the only one
		//this is to stop it from filling the whole width of the frame
		if(addFiller)
			add(Box.createHorizontalGlue());
		
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void stateChanged(ChangeEvent e)
	{
		MapViewer.logger.fine("zoom slider state changed");
//		MapViewer.logger.fine("isClickZoomRequest = " + winMain.mainCanvas.zoomHandler.isClickZoomRequest);
//		MapViewer.logger.fine("isPanZoomRequest = " + winMain.mainCanvas.zoomHandler.isPanZoomRequest);
		
		JSlider source = (JSlider) e.getSource();
		if (source.equals(zoomSlider) && !winMain.mainCanvas.zoomHandler.isClickZoomRequest && !winMain.mainCanvas.zoomHandler.isPanZoomRequest)
		{
			MapViewer.logger.finest("source is slider");
			winMain.mainCanvas.zoomHandler.processContinuousZoomRequest(source.getValue(), 0, gMapSet, true);
			updateSlider();
		}	
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void updateSlider()
	{
		MapViewer.logger.fine("updating slider");
		//update the slider
		zoomSlider.setValue(Math.round(gMapSet.zoomFactor));
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==resetButton)
		{
			winMain.mainCanvas.zoomHandler.processZoomResetRequest(gMapSet);
			updateSlider();
		}
	}
	
	
	
	public void mouseReleased(MouseEvent e)
	{
		MapViewer.winMain.mainCanvas.antiAlias = true;
		MapViewer.winMain.mainCanvas.updateCanvas(true);		
	}
	
	//these are currently not needed
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
