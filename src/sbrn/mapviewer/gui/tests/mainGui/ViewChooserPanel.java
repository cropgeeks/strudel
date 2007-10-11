package sbrn.mapviewer.gui.tests.mainGui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ViewChooserPanel extends JPanel implements ActionListener
{

	JTabbedPane tabbedPane;
	
	public ViewChooserPanel(MapViewerFrame frame)
	{
		init();
		this.tabbedPane = frame.getTabbedPane();
	}

	private void init()
	{
		String workingDir = System.getProperty("user.dir");
		System.out.println(" ViewChooserPanel working dir = " + workingDir);
		
		//put everything into a  gridlayout
		GridLayout gridLayout = new GridLayout(2,2);
		gridLayout.setVgap(10);
		this.setLayout(gridLayout);

		// add labels and buttons for choosing a view:
		
		//labels
		ImageIcon icon2D = new ImageIcon(workingDir + "/trunk/images/2dview_icon_48px.gif");
		JLabel label2d = new JLabel("2D",JLabel.CENTER);
		label2d.setVerticalTextPosition(JLabel.BOTTOM);
		label2d.setHorizontalTextPosition(JLabel.CENTER);
		ImageIcon icon3D = new ImageIcon(workingDir + "/trunk/images/3dview_icon_48px.gif");
		JLabel label3d = new JLabel("3D",JLabel.CENTER);
		label3d.setVerticalTextPosition(JLabel.BOTTOM);
		label3d.setHorizontalTextPosition(JLabel.CENTER);
		
		//buttons
		JButton button2D = new JButton("", icon2D) ;
		JButton button3D = new JButton("", icon3D);
		button2D.setBorder(BorderFactory.createRaisedBevelBorder());
		button3D.setBorder(BorderFactory.createRaisedBevelBorder());
		button2D.setPreferredSize(new Dimension(48,48));
		button3D.setPreferredSize(new Dimension(48,48));
		button2D.setActionCommand("show2DView");
		button3D.setActionCommand("show3DView");
		button2D.addActionListener(this);
		button3D.addActionListener(this);

		//assemble it all
		this.add(new JPanel().add(button2D));
		this.add(label2d);
		this.add(new JPanel().add(button3D));
		this.add(label3d);
		
		//make a titled border around it all
		this.setBorder(BorderFactory.createTitledBorder("Choose a view: "));
	}

	protected ImageIcon createImageIcon(String path, String description)
	{
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL, description);
		}
		else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if ("show2DView".equals(e.getActionCommand()))
		{
			//System.out.println("2d selected");
			tabbedPane.setSelectedIndex(0);
		}
		if ("show3DView".equals(e.getActionCommand()))
		{
			//System.out.println("3d selected");
			tabbedPane.setSelectedIndex(1);
		}		
	}
}
