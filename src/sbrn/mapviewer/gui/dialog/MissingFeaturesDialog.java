package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import scri.commons.gui.*;

public class MissingFeaturesDialog extends JDialog
{
	private JButton closeButton;
	
	public MissingFeaturesDialog(JFrame parent, boolean modal, String missingFeatures)
	{
		super(parent, true);
		
		setTitle("Missing features");
		
		setBackground(Color.white);
		
		String text = "<html>The following features were involved in homologies <br>but no entries for them were found in the file. <br>" +
		"The homologies concerned have therefore been omitted. <p>  </html>";
		
		int width = 150;
		int height = 80;
		
		JPanel textAreaPanel = new JPanel(new BorderLayout());
		textAreaPanel.setBackground(Color.white);
		textAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 35));
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText(missingFeatures);
		
		JLabel label = new JLabel(text);
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));	
		textAreaPanel.add(label, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPanel.add(scrollPane, BorderLayout.CENTER);
		
//		textAreaPanel.setPreferredSize(new Dimension(width, height));
//		label.setPreferredSize(new Dimension(width-10, 30));	
		scrollPane.setPreferredSize(new Dimension(width, height));
		
		JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		flowPanel.setBackground(new Color(238, 238, 238));
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){	
			public void actionPerformed(ActionEvent e)
			{			
				setVisible(false);
			}});
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		buttonPanel.setBackground(new Color(238, 238, 238));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.add(closeButton);
		flowPanel.add(buttonPanel);
		
		JLabel iconLabel = new JLabel((Icon) UIManager.get("OptionPane.warningIcon"));
		
		JPanel iconPanel = new JPanel(new FlowLayout());
		iconPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		iconPanel.setBackground(Color.white);
		iconPanel.add(iconLabel);
		
		JPanel layoutPanel = new JPanel();
		layoutPanel.setBackground(Color.white);
		
		DoeLayout layout = new DoeLayout(layoutPanel);
		
		layout.add(iconPanel, 0, 0, 0, 1, new Insets(0, 0, 0, 0));
		layout.add(textAreaPanel, 1, 0, 1, 1, new Insets(0, 0, 0, 0));
		layoutPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(-1, -1, 0, -1),
						BorderFactory.createLineBorder(new Color(219, 219, 219), 1)));
		
		add(layoutPanel);
		add(flowPanel, BorderLayout.SOUTH);
		pack();
		
		SwingUtils.addCloseHandler(this, iconLabel);
		getRootPane().setDefaultButton(closeButton);
		
		setResizable(false);
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
}
