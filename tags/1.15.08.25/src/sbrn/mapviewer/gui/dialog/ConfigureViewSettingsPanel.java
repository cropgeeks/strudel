/*
 * ConfigureViewSettingsPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import javax.swing.event.ChangeEvent;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

/**
 *
 * @author  __USER__
 */
public class ConfigureViewSettingsPanel extends javax.swing.JPanel
{
	
	/** Creates new form ConfigureViewSettingsPanel */
	public ConfigureViewSettingsPanel()
	{
		initComponents();
		
		//these controls have states stored in the prefs
		antialiasCheckbox.setSelected(Prefs.userPrefAntialias);
		linkFilterCheckbox.setSelected(Prefs.drawOnlyLinksToVisibleFeatures);
		distanceMarkerCheckbox.setSelected(Prefs.showDistanceMarkers);
		hintPanelCheckBox.setSelected(Prefs.showHintPanel);
		fullFeatureInfoCheckbox.setSelected(Prefs.showFullFeatureInfoOnMouseOver);
		
		// a bug in Matisse apparently prevents me from setting the correct action in Matisse itself
		//instead I have to do this here after the components have been inited
		fullFeatureInfoCheckbox.setAction(Actions.showFullFeatureInfo);
		
		//init the link shape radion buttons appropriately
		switch (Prefs.linkShape)
		{
			case Constants.LINKTYPE_CURVED:
				linkShapeCurvedRButton.setSelected(true);
				break;
			case Constants.LINKTYPE_STRAIGHT:
				linkShapeStraightRButton.setSelected(true);
				break;
			case Constants.LINKTYPE_ANGLED:
				linkShapeAngledRButton.setSelected(true);
				break;
		}
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		linkShapeButtonGroup = new javax.swing.ButtonGroup();
		jPanel1 = new javax.swing.JPanel();
		hintPanelCheckBox = new javax.swing.JCheckBox(Actions.showHint);
		antialiasCheckbox = new javax.swing.JCheckBox(Actions.antialiasedDraw);
		linkFilterCheckbox = new javax.swing.JCheckBox(Actions.filterLinks);
		distanceMarkerCheckbox = new javax.swing.JCheckBox(Actions.showDistanceMarkers);
		linkShapeLabel = new javax.swing.JLabel();
		linkShapeCurvedRButton = new javax.swing.JRadioButton(Actions.linkCurved);
		linkShapeStraightRButton = new javax.swing.JRadioButton(Actions.linkStraight);
		linkShapeAngledRButton = new javax.swing.JRadioButton(Actions.linkAngled);
		fullFeatureInfoCheckbox = new javax.swing.JCheckBox(Actions.showDistanceMarkers);
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("View Settings:"));
		
		hintPanelCheckBox.setText("Show hint panel");
		hintPanelCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		antialiasCheckbox.setText("Use antialiased drawing");
		antialiasCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		linkFilterCheckbox.setText("Filter out links to off screen features ");
		linkFilterCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		distanceMarkerCheckbox.setText("Show distance markers (higher zoom levels only)");
		distanceMarkerCheckbox.setActionCommand("Show distance markers");
		distanceMarkerCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		linkShapeLabel.setText("Link shape:");
		
		linkShapeButtonGroup.add(linkShapeCurvedRButton);
		linkShapeCurvedRButton.setText("curved");
		linkShapeCurvedRButton.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		linkShapeButtonGroup.add(linkShapeStraightRButton);
		linkShapeStraightRButton.setText("straight");
		linkShapeStraightRButton.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		linkShapeButtonGroup.add(linkShapeAngledRButton);
		linkShapeAngledRButton.setText("angled");
		linkShapeAngledRButton.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				ConfigureViewSettingsPanel.this.stateChanged(evt);
			}
		});
		
		fullFeatureInfoCheckbox.setText("Show full feature info on mouseover");
		fullFeatureInfoCheckbox.setActionCommand("Show full feature info on mouseover");
		fullFeatureInfoCheckbox.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				fullFeatureInfoCheckboxstateChanged(evt);
			}
		});
		
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(linkShapeCurvedRButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(linkShapeStraightRButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(linkShapeAngledRButton)).addComponent(linkShapeLabel)).addContainerGap(160, Short.MAX_VALUE)).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(hintPanelCheckBox).addComponent(antialiasCheckbox).addComponent(linkFilterCheckbox).addComponent(distanceMarkerCheckbox).addComponent(fullFeatureInfoCheckbox)).addContainerGap(64, Short.MAX_VALUE))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(143, Short.MAX_VALUE).addComponent(linkShapeLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(linkShapeCurvedRButton).addComponent(linkShapeStraightRButton).addComponent(linkShapeAngledRButton)).addGap(40, 40, 40)).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(hintPanelCheckBox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(antialiasCheckbox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(linkFilterCheckbox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(distanceMarkerCheckbox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(fullFeatureInfoCheckbox).addContainerGap(93, Short.MAX_VALUE))));
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 389, Short.MAX_VALUE).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(0, 9, Short.MAX_VALUE).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 9, Short.MAX_VALUE))));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 256, Short.MAX_VALUE).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(0, 7, Short.MAX_VALUE).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 8, Short.MAX_VALUE))));
	}// </editor-fold>
	//GEN-END:initComponents
	
	private void fullFeatureInfoCheckboxstateChanged(javax.swing.event.ChangeEvent evt)
	{
		// TODO add your handling code here:
	}
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JCheckBox antialiasCheckbox;
	private javax.swing.JCheckBox distanceMarkerCheckbox;
	private javax.swing.JCheckBox fullFeatureInfoCheckbox;
	private javax.swing.JCheckBox hintPanelCheckBox;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JCheckBox linkFilterCheckbox;
	private javax.swing.JRadioButton linkShapeAngledRButton;
	private javax.swing.ButtonGroup linkShapeButtonGroup;
	private javax.swing.JRadioButton linkShapeCurvedRButton;
	private javax.swing.JLabel linkShapeLabel;
	private javax.swing.JRadioButton linkShapeStraightRButton;
	
	// End of variables declaration//GEN-END:variables
	public javax.swing.JCheckBox getAntialiasCheckbox()
	{
		return antialiasCheckbox;
	}
	
	public javax.swing.JCheckBox getDistanceMarkerCheckbox()
	{
		return distanceMarkerCheckbox;
	}
	
	public javax.swing.JCheckBox getHintPanelCheckBox()
	{
		return hintPanelCheckBox;
	}
	
	public javax.swing.JCheckBox getLinkFilterCheckbox()
	{
		return linkFilterCheckbox;
	}
	
	public javax.swing.JRadioButton getLinkShapeAngledRButton()
	{
		return linkShapeAngledRButton;
	}
	
	public javax.swing.ButtonGroup getLinkShapeButtonGroup()
	{
		return linkShapeButtonGroup;
	}
	
	public javax.swing.JRadioButton getLinkShapeCurvedRButton()
	{
		return linkShapeCurvedRButton;
	}
	
	public javax.swing.JLabel getLinkShapeLabel()
	{
		return linkShapeLabel;
	}
	
	public javax.swing.JRadioButton getLinkShapeStraightRButton()
	{
		return linkShapeStraightRButton;
	}
	
	private void stateChanged(ChangeEvent evt)
	{
	}
	
}
