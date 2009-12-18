// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;
import scri.commons.gui.Icons;

public class NBStartWelcomePanel extends javax.swing.JPanel implements ActionListener
{

    /** Creates new form NBStartWelcomePanel */
    public NBStartWelcomePanel()
	{
        initComponents();
		setOpaque(false);

		versionLabel.setText("<html>Version " + Install4j.VERSION);
		feedbackLabel.setText("Email feedback ");
		feedbackLabel.setIcon(Icons.getIcon("FEEDBACK"));
		feedbackLabel.addActionListener(this);
    }

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == feedbackLabel)
		{
			Utils.sendFeedback();
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        versionLabel = new javax.swing.JLabel();
        feedbackLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        versionLabel.setText("<html>Tablet x.xx.xx.xx - &copy; Plant Bioinformatics Group, SCRI.");

        feedbackLabel.setForeground(new java.awt.Color(68, 106, 156));
        feedbackLabel.setText("Send feedback");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(feedbackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel feedbackLabel;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables


}