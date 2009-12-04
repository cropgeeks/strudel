/*
 * ExampleDataInfoPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package sbrn.mapviewer.gui.dialog;

import sbrn.mapviewer.*;

/**
 *
 * @author  __USER__
 */
public class ExampleDataInfoPanel extends javax.swing.JPanel
{
	
	/** Creates new form ExampleDataInfoPanel */
	public ExampleDataInfoPanel()
	{
		initComponents();
		
		targetDataInfoLabel.setText("<html><b>Target genome (middle):</b><br/>" + Constants.exampleTargetDataDescription + "</html>");
		refGen1InfoLabel.setText("<html><b>Reference genome 1 (left):</b><br/>" + Constants.exampleRefGenome1Description + "</html>");
		refGen2InfoLabel.setText("<html><b>Reference genome 2 (right):</b><br/>" + Constants.exampleRefGenome2Description + "</html>");
		
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
		
		targetDataInfoLabel = new javax.swing.JLabel();
		refGen1InfoLabel = new javax.swing.JLabel();
		refGen2InfoLabel = new javax.swing.JLabel();
		
		targetDataInfoLabel.setText("jLabel1");
		
		refGen1InfoLabel.setText("jLabel1");
		
		refGen2InfoLabel.setText("jLabel1");
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(targetDataInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE).addComponent(refGen1InfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE).addComponent(refGen2InfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(targetDataInfoLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(refGen1InfoLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(refGen2InfoLabel).addContainerGap(49, Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel refGen1InfoLabel;
	private javax.swing.JLabel refGen2InfoLabel;
	private javax.swing.JLabel targetDataInfoLabel;
	// End of variables declaration//GEN-END:variables
	
}