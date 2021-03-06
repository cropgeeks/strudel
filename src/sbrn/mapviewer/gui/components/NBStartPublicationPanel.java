// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package sbrn.mapviewer.gui.components;

import java.awt.event.*;

import sbrn.mapviewer.*;
import sbrn.mapviewer.gui.*;
import scri.commons.gui.*;

class NBStartPublicationPanel extends javax.swing.JPanel implements ActionListener
{
	NBStartPublicationPanel()
	{
        initComponents();
		setOpaque(false);

		linkLabel.setText("Coming soon");

		linkLabel.setIcon(Icons.getIcon("WEB"));
		linkLabel.addActionListener(this);
    }

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == linkLabel)
		{
			Utils.visitURL(Constants.strudelPublicationsPage);
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

        linkLabel = new scri.commons.gui.matisse.HyperLinkLabel();

        linkLabel.setForeground(new java.awt.Color(68, 106, 156));
        linkLabel.setText("Send feedback");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(linkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(298, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(linkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scri.commons.gui.matisse.HyperLinkLabel linkLabel;
    // End of variables declaration//GEN-END:variables

}