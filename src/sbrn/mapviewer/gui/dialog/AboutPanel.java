/*
 * AboutPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;

/**
 *
 * @author  __USER__
 */
public class AboutPanel extends javax.swing.JPanel
{
	
	/** Creates new form AboutPanel */
	public AboutPanel()
	{
		initComponents();
		
		initWebStuff();
		
		scriIcon.setText("");
		scriIcon.setIcon(Icons.getIcon("SCRI"));
		
		versionLabel.setText("Strudel " + Install4j.VERSION);
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
		
		versionLabel = new javax.swing.JLabel();
		webLabel = new javax.swing.JLabel();
		copyrightLabel = new javax.swing.JLabel();
		nameLabel = new javax.swing.JLabel();
		scriIcon = new javax.swing.JLabel();
		
		versionLabel.setFont(new java.awt.Font("Tahoma", 1, 18));
		versionLabel.setText("Strudel x.xx.xx.xx");
		
		webLabel.setText(Constants.strudelHomePage);
		
		copyrightLabel.setText("Copyright (C) 2009, Plant Bioinformatics Group, SCRI");
		
		nameLabel.setText("Micha Bayer, Iain Milne and David Marshall");
		
		scriIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		scriIcon.setText("SCRI LOGO");
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(nameLabel).addComponent(copyrightLabel).addComponent(versionLabel).addComponent(webLabel).addComponent(scriIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(versionLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(webLabel).addGap(14, 14, 14).addComponent(copyrightLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(nameLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(scriIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel copyrightLabel;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JLabel scriIcon;
	private javax.swing.JLabel versionLabel;
	private javax.swing.JLabel webLabel;
	
	// End of variables declaration//GEN-END:variables
	
	private void initWebStuff()
	{
		Utils.labelToHyperlink(webLabel, Constants.strudelHomePage);		
		Utils.labelToHyperlink(scriIcon, Constants.scriHTML);
	}
	
}
