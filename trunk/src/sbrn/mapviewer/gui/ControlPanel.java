/*
 * ControlPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.*;

import javax.swing.*;

/**
 *
 * @author  __USER__
 */
public class ControlPanel extends javax.swing.JPanel implements ItemListener
{
	
	WinMain winMain;
	public double blastThreshold = 1;
	
	/** Creates new form ControlPanel */
	public ControlPanel(WinMain winMain)
	{
		this.winMain = winMain;
		initComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jSeparator2 = new javax.swing.JSeparator();
		jPanel1 = new javax.swing.JPanel();
		resetLeftButton = new javax.swing.JButton();
		resetRightButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		antialiasCheckbox = new javax.swing.JCheckBox();
		jLabel2 = new javax.swing.JLabel();
		eValueSlider = new javax.swing.JSlider();
		jSeparator1 = new javax.swing.JSeparator();
		jSeparator3 = new javax.swing.JSeparator();
		jSeparator4 = new javax.swing.JSeparator();
		jLabel3 = new javax.swing.JLabel();
		backgroundCombo = new javax.swing.JComboBox();
		blastScoreLabel = new javax.swing.JLabel();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls:"));
		
		resetLeftButton.setText("Reset left");
		resetLeftButton.setPreferredSize(new java.awt.Dimension(85, 23));
		resetLeftButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				ControlPanel.this.actionPerformed(evt);
			}
		});
		
		resetRightButton.setText("Reset right");
		resetRightButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				ControlPanel.this.actionPerformed(evt);
			}
		});
		
		jLabel1.setText("Zoom reset:");
		
		antialiasCheckbox.setText("Line smoothing");
		antialiasCheckbox.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				ControlPanel.this.itemStateChanged(evt);
			}
		});
		
		jLabel2.setText("BLAST e-value cut-off (1.00E-):");
		
		eValueSlider.setMajorTickSpacing(100);
		eValueSlider.setMaximum(300);
		eValueSlider.setMinorTickSpacing(50);
		eValueSlider.setPaintLabels(true);
		eValueSlider.setPaintTicks(true);
		eValueSlider.setValue(0);
		eValueSlider.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				eValueSliderStateChanged(evt);
			}
		});
		
		jLabel3.setText("Background colour:");
		
		backgroundCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "black", "dark grey", "light grey", "white" }));
		backgroundCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				ControlPanel.this.actionPerformed(evt);
			}
		});
		
		blastScoreLabel.setText("Current value:");
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel1Layout.createSequentialGroup().addContainerGap().add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														jPanel1Layout.createSequentialGroup().add(
																		jSeparator3,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		205,
																		Short.MAX_VALUE).addContainerGap()).add(
														jPanel1Layout.createSequentialGroup().add(
																		antialiasCheckbox).addContainerGap()).add(
														jPanel1Layout.createSequentialGroup().add(
																		jPanel1Layout.createParallelGroup(
																						org.jdesktop.layout.GroupLayout.LEADING).add(
																						jLabel1).add(
																						jPanel1Layout.createSequentialGroup().add(
																										resetLeftButton,
																										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																										81,
																										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
																										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
																										resetRightButton))).addContainerGap()).add(
														org.jdesktop.layout.GroupLayout.TRAILING,
														jPanel1Layout.createSequentialGroup().add(
																		jSeparator1,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		205,
																		Short.MAX_VALUE).addContainerGap()).add(
														jPanel1Layout.createSequentialGroup().add(
																		jLabel2).addContainerGap(
																		63,
																		Short.MAX_VALUE)).add(
														jPanel1Layout.createSequentialGroup().add(
																		eValueSlider,
																		0,
																		0,
																		Short.MAX_VALUE).add(
																		14,
																		14,
																		14)).add(
														org.jdesktop.layout.GroupLayout.TRAILING,
														jPanel1Layout.createSequentialGroup().add(
																		backgroundCombo,
																		0,
																		205,
																		Short.MAX_VALUE).addContainerGap()).add(
														jPanel1Layout.createSequentialGroup().add(
																		jLabel3).addContainerGap(
																		123,
																		Short.MAX_VALUE)).add(
														jPanel1Layout.createSequentialGroup().add(
																		blastScoreLabel).addContainerGap(
																		145,
																		Short.MAX_VALUE)).add(
														jPanel1Layout.createSequentialGroup().add(
																		jSeparator4,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		205,
																		Short.MAX_VALUE).addContainerGap()))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel1Layout.createSequentialGroup().add(jLabel1).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														resetRightButton).add(
														resetLeftButton,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										jSeparator1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										10,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										antialiasCheckbox).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										jSeparator3,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										10,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jLabel2).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										eValueSlider,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										blastScoreLabel).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										jSeparator4,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										10,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
										1, 1, 1).add(jLabel3).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										backgroundCombo,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(
										40, Short.MAX_VALUE)));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										jPanel1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										jPanel1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents
	
	private void eValueSliderStateChanged(javax.swing.event.ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();
		
		//convert the value selected to the exponent of a small decimal and set this as
		//the new BLAST threshold (which is a double)
		int exponent = source.getValue();
		System.out.println("new exponent value from e-Value slider: " + exponent);
		DecimalFormat df = new DecimalFormat("0.##E0");
		try
		{
			Number score = df.parse("1.00E-" + exponent);
			System.out.println("setting BLAST threshold to new value of " + score.toString());
			blastThreshold = score.doubleValue();
			blastScoreLabel.setText("Current value: " + blastThreshold);
			winMain.mainCanvas.repaint();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
		
	}
	
	private void actionPerformed(java.awt.event.ActionEvent e)
	{
		//these two actions reset the left and right zoom respectively
		if (e.getSource() == resetLeftButton)
		{
			winMain.mainCanvas.processSliderZoomRequest(1, 0);
		}
		if (e.getSource() == resetRightButton)
		{
			winMain.mainCanvas.processSliderZoomRequest(1, 1);
		}
		
		//change the background colour to the value selected by the user from the combo
		if (e.getSource() == backgroundCombo)
		{
			winMain.fatController.changeBackgroundColour((String) backgroundCombo.getSelectedItem());
		}
		
		//update overviews
		winMain.fatController.updateOverviewCanvases();
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getItemSelectable() == antialiasCheckbox)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				winMain.mainCanvas.antiAlias = true;
			}
			else
			{
				winMain.mainCanvas.antiAlias = false;
			}
			winMain.mainCanvas.repaint();
		}
	}
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JCheckBox antialiasCheckbox;
	private javax.swing.JComboBox backgroundCombo;
	private javax.swing.JLabel blastScoreLabel;
	private javax.swing.JSlider eValueSlider;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JButton resetLeftButton;
	private javax.swing.JButton resetRightButton;
	// End of variables declaration//GEN-END:variables
	
}
