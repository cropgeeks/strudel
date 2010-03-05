package sbrn.mapviewer.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import sbrn.mapviewer.gui.*;
import sbrn.mapviewer.gui.components.PreviewCanvas;

/**
 * GUI component containing a list of colours as well as a preview panel showing
 * how those colours effect the rendering of a typical strudel scene.
 */
public class NBColourPanel extends JPanel implements ActionListener
{
	private DefaultListModel schemeModel;
	private DefaultComboBoxModel comboModel;
	private final PreviewCanvas preview;
	private final ColorSchemeChooserDialog parent;
	private final DefaultColourScheme defaultScheme;
	private final DefaultColourScheme printScheme;
	private JDialog chooser;
	private JColorChooser colourChooser;
	private Color newColour;
	private final ActionListener dialogListener;

    /** Creates new form NBColourPanel */
    public NBColourPanel(PreviewCanvas preview, ColorSchemeChooserDialog parent)
	{
        initComponents();

		this.preview = preview;
		this.parent = parent;
		jSplitPane1.setRightComponent(preview);

		schemeList.setCellRenderer(new ColorListRenderer());

		addMouseListener(schemeList);

		defaultScheme = new DefaultColourScheme();
		printScheme = new PrintColourScheme();

		setupComboBox();

		actionPerformed(null);

		// ActionListener for the colour chooser dialog
		dialogListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newColour = colourChooser.getColor();
			}
		};
    }

	/**
	 * Populates the combobox with the correct options and sets up an actionlister
	 * so combobox event can be handled.
	 */
	private void setupComboBox()
	{
		// Add the various colour schemes to the combo box
		comboModel = new DefaultComboBoxModel();
		comboModel.addElement(defaultScheme);
		comboModel.addElement(printScheme);
		schemeCombo.setModel(comboModel);
		schemeCombo.addActionListener(this);
		for (int i = 0; i < comboModel.getSize(); i++)
		{
			if (comboModel.getElementAt(i).toString().equals(Prefs.selectedColourScheme))
			{
				schemeCombo.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Changes the colours displayed in the listbox dependant on the item selected
	 * in the combobox.
	 */
	public void actionPerformed(ActionEvent e)
	{
		DefaultColourScheme cs = (DefaultColourScheme) schemeCombo.getSelectedItem();
		preview.setColourScheme(cs);
		parent.repaint();

		schemeModel = new DefaultListModel();

		for (DefaultColourScheme.ColourInfo info: cs.getColours())
			schemeModel.addElement(info);
		schemeList.setModel(schemeModel);
	}

	// Add mouse listeners to the lists so that a double click fires an event
	private void addMouseListener(final JList list)
	{
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					selectColor(list);
			}
		});
	}

	// Pop up a colour chooser and apply the new colour to the selected scheme
	private void selectColor(JList list)
	{
		DefaultColourScheme.ColourInfo c = (DefaultColourScheme.ColourInfo) list.getSelectedValue();
		if (c == null)
			return;

		// fiddly stuff needed for remembering recent colours in colour chooser
		JDialog d = getColourChooser();
		colourChooser.setColor(c.colour);
		d.setVisible(true);

		if(newColour == null)
			return;

		c.colour = newColour;

		// Determine which colour scheme needs to be updated
		DefaultColourScheme cs = ((DefaultColourScheme)schemeCombo.getSelectedItem());
		cs.setSchemeColours(getArrayList(schemeModel));

		// Refresh screen
		parent.repaint();
	}

	/**
	 * Convert the listbox model into an ArrayList such that it can be used to
	 * set the colours in the colour scheme classes.
	 *
	 * @param model The ListModel from the colour list.
	 * @return An ArrayList of ColourInfo objects
	 */
	private ArrayList<DefaultColourScheme.ColourInfo> getArrayList(DefaultListModel model)
	{
		ArrayList<DefaultColourScheme.ColourInfo> colors = new ArrayList<DefaultColourScheme.ColourInfo>();
		for (int i = 0; i < model.size(); i++)
			colors.add((DefaultColourScheme.ColourInfo)model.get(i));

		return colors;
	}

	static class ColorListRenderer extends DefaultListCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getListCellRendererComponent(JList list, Object o,
				int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);

			DefaultColourScheme.ColourInfo info = (DefaultColourScheme.ColourInfo ) o;

			// Set the text
			setText(info.name);

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

			g.setColor(info.colour);
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();

			setIcon(new ImageIcon(image));

			return this;
		}

		@Override
		public Insets getInsets(Insets i)
			{ return new Insets(0, 3, 0, 0); }
	}

	/**
	 * Update the XML preferences with the new colours for this scheme.
	 */
	public void updatePreferences()
	{
		DefaultColourScheme cs = (DefaultColourScheme) schemeCombo.getSelectedItem();
		cs.setCustomColourPreferences();

		Prefs.selectedColourScheme = schemeCombo.getSelectedItem().toString();
	}

	/**
	 * Set the colours for the selected scheme.
	 */
	public void setColourScheme()
	{
		DefaultColourScheme scheme = (DefaultColourScheme) schemeCombo.getSelectedItem();
		scheme.setColours();
	}

	/**
	 * Reset a scheme to its default colours.
	 */
	public void resetColours()
	{
		DefaultColourScheme scheme = (DefaultColourScheme) schemeCombo.getSelectedItem();
		scheme.resetToDefault();
		parent.repaint();
		actionPerformed(null);
	}

	// Colour chooser that will remember the recently picked colours
	public JDialog getColourChooser()
	{
		if(chooser == null)
		{
			colourChooser = new JColorChooser();
			chooser = JColorChooser.createDialog(this, "Select New Colour", true, colourChooser, dialogListener, dialogListener);
		}
		return chooser;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        schemeCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        schemeList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();

        jLabel1.setText("Select colour scheme: ");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Customise (double click a colour to change it):"));

        jScrollPane1.setViewportView(schemeList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(schemeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(schemeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 226, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    javax.swing.JComboBox schemeCombo;
    private javax.swing.JList schemeList;
    // End of variables declaration//GEN-END:variables

}
