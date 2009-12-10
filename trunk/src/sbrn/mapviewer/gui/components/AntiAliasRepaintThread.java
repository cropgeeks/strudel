package sbrn.mapviewer.gui.components;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import sbrn.mapviewer.*;

public class AntiAliasRepaintThread extends Thread
{
	public static boolean hasImage = false;

	private BufferedImage buffer;

	private Boolean killMe = false;
	private static AntiAliasRepaintThread previousThread;

	public AntiAliasRepaintThread(BufferedImage buffer)
	{
		this.buffer = buffer;
		hasImage = false;

		// Cancel any previous rendering threads that might be running
		if (previousThread != null)
		{
			previousThread.killMe = true;
			previousThread.interrupt();
		}

		previousThread = this;
		start();
	}

	//sleeps for a minimal amount of time to create a short delay for repainting, then repaints with antialias on
	//used for prettier redraws after aliased drawing (in some situations)
	@Override
	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);

		try { Thread.sleep(750); }
		catch (InterruptedException e) {}

		if (killMe)
			return;

		long s = System.nanoTime();

		Graphics2D g = buffer.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Strudel.winMain.mainCanvas.paintCanvas(g, killMe);
		g.dispose();

		if (killMe == false)
		{
			System.out.println("AA render time (ms) = " + (System.nanoTime() - s)/1000000.0f);

			hasImage = true;
			Strudel.winMain.mainCanvas.updateCanvas(false);
		}
	}
}
