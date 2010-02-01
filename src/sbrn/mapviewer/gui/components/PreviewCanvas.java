package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import sbrn.mapviewer.gui.*;

/**
 * Draws a typical strudel scene with all the elements required to make informed
 * choices about changes to the colour scheme.
 */
public class PreviewCanvas extends JPanel
{
	private float chromoSpacing, availableSpaceVertically, allSpacers;
	private int chromoWidth, chromoHeight, leftXPos, rightXPos;
	private DefaultColourScheme scheme;

	public PreviewCanvas()
	{
		setBackground(Colors.overviewCanvasBackgroundColour);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		// need to clear the canvas before we draw
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		paintBackground(g2);
		
		calculatePositioningVariables();

		drawChromoMaps(g2);
		drawLinks(g2);
		drawFeatures(g2);
		drawLabels(g2);
	}

	private void paintBackground(Graphics2D g2)
	{
		Color b1 = scheme.getBackgroundGradientStartColour();
		Color b2 = scheme.getBackgroundGradientEndColour();
		g2.setPaint(new GradientPaint(getWidth() / 2, 0, b1, getWidth() / 2, getHeight(), b2));
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	private void calculatePositioningVariables()
	{
		chromoWidth = getWidth() / 20;
		chromoSpacing = Math.round((getHeight() / 2) * 0.20f);
		// the total amount of space we have for drawing on vertically, in pixels
		availableSpaceVertically = getHeight() - (chromoSpacing * 2);
		// the combined height of all the vertical spaces between chromosomes
		allSpacers = chromoSpacing;
		// the height of a chromosome
		chromoHeight = Math.round((availableSpaceVertically - allSpacers) / 2);
		leftXPos = getWidth()/4;
		rightXPos = (getWidth()/4)*3;
	}

	private void drawChromoMaps(Graphics2D g2)
	{
		int topChromosomesY = (int) chromoSpacing;
		int bottomChromosomesY = (int) chromoSpacing + chromoHeight + (int) chromoSpacing;
		
		drawChromoMap(scheme.getGenomeColour(), g2, leftXPos, topChromosomesY, "1");
		drawChromoMap(scheme.getChromosomeHighlightColour(), g2, leftXPos, bottomChromosomesY, "2");
		drawChromoMap(scheme.getInvertedChromosomeColour(), g2, rightXPos, topChromosomesY, "3");
		drawChromoMap(scheme.getInvertedChromosomeHighlightColour(), g2, rightXPos, bottomChromosomesY, "4");
	}

	private void drawChromoMap(Color colour, Graphics2D g2, int x, int y, String name)
	{
		Color centreColour = colour.brighter().brighter().brighter().brighter();

		g2.translate(x, y);

		// construct cyclic gradient paint
		GradientPaint gradient = new GradientPaint(0, 0, colour, chromoWidth/2, 0, centreColour, true);
		g2.setPaint(gradient);
		// fill whole chromosome with the graident paint it will cycle and give the desired effect
		g2.fillRect(0, 0, chromoWidth, chromoHeight);

		// draw the index of the map in the genome
		int smallFontSize = 9;
		Font overviewLabelFont = new Font("Arial", Font.BOLD, smallFontSize);
		g2.setFont(overviewLabelFont);
		g2.setColor(scheme.getChromosomeIndexColour());
		g2.drawString(name, chromoWidth * 2, Math.round(chromoHeight/2.0f) + Math.round(smallFontSize/2.0f));

		g2.translate(-x, -y);
	}

	private void drawLinks(Graphics2D g)
	{
		int lineStartX = leftXPos+chromoWidth;

		drawNormalLinks(g, lineStartX);

		drawStrongLink(g, lineStartX, (int)chromoSpacing*2+chromoHeight, rightXPos, (chromoHeight*2)+(int)chromoSpacing);

		drawMildLink(g, lineStartX, (chromoHeight*2)+(int)chromoSpacing, rightXPos, (chromoHeight*2)+(int)(chromoSpacing*1.5));
	}

	private void drawNormalLinks(Graphics2D g, int lineStartX)
	{
		g.setColor(scheme.getLinkColour());
		g.drawLine(lineStartX, (int)chromoSpacing, rightXPos, (int)chromoSpacing+chromoHeight-1);
		g.drawLine(lineStartX, (int)chromoSpacing+chromoHeight-1, rightXPos, (int)chromoSpacing);
		g.drawLine(lineStartX, (int)chromoSpacing+(chromoHeight/2), rightXPos, (int)chromoSpacing*2);
	}

	private void drawStrongLink(Graphics2D g, int x1, int y1, int x2, int y2)
	{
		g.setColor(scheme.getStrongEmphasisLinkColour());
		g.drawLine(x1, y1, x2, y2);
	}

	private void drawMildLink(Graphics2D g, int x1, int y1, int x2, int y2)
	{
		g.setColor(scheme.getMildEmphasisLinkColour());
		g.drawLine(x1, y1, x2, y2);
	}

	private void drawFeatures(Graphics2D g)
	{
		drawNormalFeatures(g);
		drawHighlightedFeatures(g);
	}

	private void drawNormalFeatures(Graphics2D g)
	{
		g.setColor(scheme.getFeatureColour());
		g.drawLine(leftXPos, (int) chromoSpacing, leftXPos + chromoWidth-1, (int) chromoSpacing);
		g.drawLine(leftXPos, (int) chromoSpacing + chromoHeight - 1, leftXPos + chromoWidth-1, (int) chromoSpacing + chromoHeight - 1);
		g.drawLine(leftXPos, (int) chromoSpacing + (chromoHeight / 2), leftXPos + chromoWidth-1, (int) chromoSpacing + (chromoHeight / 2));
	}

	private void drawHighlightedFeatures(Graphics2D g)
	{
		g.setColor(scheme.getHighlightedFeatureColour());
		g.drawLine(rightXPos, (chromoHeight*2)+(int)chromoSpacing, rightXPos+chromoWidth-1, (chromoHeight*2)+(int)chromoSpacing);
	}

	private void drawLabels(Graphics2D g)
	{
		drawNormalLabel(g);
		drawHighlightedLabel(g);
	}

	private void drawNormalLabel(Graphics2D g)
	{
		int lineLength = 30;
		int lineX = leftXPos;
		int lineY = (int) chromoSpacing + (chromoHeight / 2);
		int labelX = lineX-lineLength;
		FontMetrics fm = g.getFontMetrics();
		float arcSize = fm.getHeight() / 1.5f;
		int labelY = lineY + (fm.getHeight() / 2);
		float stringWidth = fm.stringWidth("Label");

		g.setColor(scheme.getFeatureColour());
		g.drawLine(lineX, lineY, lineX-lineLength, lineY);
		
		RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX-stringWidth, labelY - fm.getHeight(), stringWidth, fm.getHeight(), arcSize, arcSize);
		g.fill(backGroundRect);

		g.setColor(scheme.getFeatureLabelColour());
		g.drawString("Label", labelX-stringWidth, labelY-(fm.getHeight()/4));
	}

	private void drawHighlightedLabel(Graphics2D g)
	{
		int lineLength = 30;
		int lineX = rightXPos;
		int lineY = (chromoHeight*2)+(int)chromoSpacing;
		int labelX = lineX+lineLength;
		FontMetrics fm = g.getFontMetrics();
		float arcSize = fm.getHeight() / 1.5f;
		int labelY = lineY + (fm.getHeight() / 2);
		float stringWidth = fm.stringWidth("Label");

		g.setColor(scheme.getHighlightedFeatureLabelBackgroundColour());
		g.drawLine(lineX, lineY, lineX+lineLength, lineY);

		RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(labelX, labelY - fm.getHeight(), stringWidth, fm.getHeight(), arcSize, arcSize);
		g.fill(backGroundRect);

		g.setColor(scheme.getHighlightedFeatureLabelColour());
		g.drawString("Label", labelX, labelY-(fm.getHeight()/4));
	}

	public void setColourScheme(DefaultColourScheme scheme)
	{
		this.scheme = scheme;
	}
}