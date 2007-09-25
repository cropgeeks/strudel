package sbrn.mapviewer.gui.tests.syntenyviewer2d;

import java.awt.*;

public class Chromosome
{
	private Point position;
	
	// Width and height
	private int w, h;
	
	private boolean isMouseOver;
	
	private GradientPaint redGradient, grayGradient;
	
	Chromosome()
	{
		position = new Point(100, 50);
		
		w = 25;
		h = 350;
		
		mixPallete();
	}
	
	private void mixPallete()
	{
		redGradient = new GradientPaint(0, 0, Color.RED, w, 0, Color.WHITE);
		grayGradient = new GradientPaint(0, 0, Color.GRAY, w, 0, Color.WHITE);
	}
	
	void paint(Graphics2D g)
	{
		g.translate(position.x, position.y);
		
//		g.setPaint(grayGradient);
//		g.fillRect(5, 0, 15, h);
//		g.setColor(isMouseOver ? Color.blue : Color.black);
//		g.drawRect(5, 0, 15, h);		
		
		g.setPaint(redGradient);
		g.fillRect(0, 20, w, h-40);
		g.setColor(isMouseOver ? Color.blue : Color.black);
		g.drawRect(0, 20, w, h-40);
	}
	
	boolean isMouseOver(Point p)
	{
		if (p.x >= position.x && p.x <= position.x+w &&
			p.y >= position.y && p.y <= position.y+h)
		{
			isMouseOver = true;
		}
		else
			isMouseOver = false;
		
		return isMouseOver;
	}
	
	void moveTo(Point p)
	{
		position = new Point(p.x, p.y);
	}
}
