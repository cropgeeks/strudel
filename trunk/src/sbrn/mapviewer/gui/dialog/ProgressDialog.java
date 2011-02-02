package sbrn.mapviewer.gui.dialog;

import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import sbrn.mapviewer.Strudel;
import sbrn.mapviewer.gui.ITrackableJob;
import scri.commons.gui.TaskDialog;

/**
 * Class for use whenever a dialog with a progress bar is required. The dialog
 * can monitor the progress of any task which implements the ITrackableJob
 * interface.
 */
public class ProgressDialog extends JDialog implements Runnable, ActionListener
{
	private static DecimalFormat d = new DecimalFormat("0.00");

	//job status identifiers
	public static final int JOB_COMPLETED = 0;
	public static final int JOB_CANCELLED = 1;
	public static final int JOB_FAILED = 2;

	private int jobStatus = JOB_COMPLETED;

	private NBProgressPanel nbPanel;

	// Runnable object that will be active while the dialog is visible
	private ITrackableJob job;

	// A reference to any exception thrown while the job was active
	private Exception exception = null;

	private Timer timer;

	public ProgressDialog(ITrackableJob job, String title, String label)
	{
		super(Strudel.winMain, title, true);

		this.job = job;

		nbPanel = new NBProgressPanel(job, label);
		new Thread(this).start();

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				setVisible(false);
				TaskDialog.info("Data import canceled.", "Close");
			}
		});

		nbPanel.bCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelJob();
				setVisible(false);
				TaskDialog.info("Data import canceled.", "Close");
			}
		});

		add(nbPanel);

		pack();
		setLocationRelativeTo(Strudel.winMain);
		setResizable(false);
		// Only show the dialog if the job hasn't finished yet
		if (this.job != null)
			setVisible(true);
	}

	public Exception getException()
		{ return exception; }

	// Called every 100ms to update the status of the progress bar
	public void actionPerformed(ActionEvent e)
	{
		// The job will be null when it's finished/failed or was cancelled
		if (job == null)
		{
			setVisible(false);
			return;
		}

		nbPanel.pBar.setIndeterminate(job.isIndeterminate());
		nbPanel.pBar.setStringPainted(!job.isIndeterminate());

		int val = job.getValue();
		int max = job.getMaximum();
		nbPanel.pBar.setMaximum(max);
		nbPanel.pBar.setValue(val);

		String message = job.getMessage();
		if (message != null)
			nbPanel.msgLabel.setText(message);

		// If the job doesn't know its maximum (yet), this would
		// have caused a 0 divided by 0 which isn't pretty
		if (max == 0)
			nbPanel.pBar.setString(d.format(0) + "%");
		else
		{
			float value = ((float) val / (float) max) * 100;
			nbPanel.pBar.setString(d.format(value) + "%");
		}
	}

	private void cancelJob()
	{
		job.cancelJob();
		jobStatus = JOB_CANCELLED;
	}

	// Starts the job running in its own thread, catching any exceptions that
	// may occur as it runs.
	public void run()
	{
		Thread.currentThread().setName("ProgressDialog-ITrackableJob");

		// calls ActionPerformed every 100ms
		timer = new Timer(100, this);
		timer.start();

		try
		{
			job.runJob();
		}
		catch (Exception e)
		{
			e.printStackTrace();

			exception = e;
			jobStatus = JOB_FAILED;
		}

		// Remove all references to the job once completed, because this window
		// never seems to get garbage-collected meaning its references never die
		job = null;
	}

	public int getResult()
		{ return jobStatus; }

	// End of variables declaration
	public javax.swing.JButton getDataLoadcancelButton()
	{
		return nbPanel.bCancel;
	}

	public javax.swing.JLabel getDataLoadingLabel()
	{
		return nbPanel.mainLabel;
	}
	
	public NBProgressPanel getProgressPanel()
	{
		return nbPanel;
	}
}
