package sbrn.mapviewer.io;

import java.io.*;
import java.text.*;
import java.util.zip.*;
import sbrn.mapviewer.gui.SimpleJob;
import scri.commons.file.*;

/**
 * An abstract class which defines an implementation of ITrackableJob for reading
 * from I/O sources.
 */
public abstract class TrackableReader extends SimpleJob
{
	private static DecimalFormat df = new DecimalFormat("0.0");

	protected StrudelFile file;
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	// The number of bytes read so far
	private long bytesRead;

	// The total size (in bytes) of all the files
	private long totalSize;

	// Tracking variables for transfer rate (MB/s) info
	private long lastBytesRead;
	private long lastTime;

	/**
	 * Set the source of the I/O, this is a StrudelFile which can either be a
	 * file on disk, or a url connection.
	 * @param file
	 */
	void setInput(StrudelFile file)
	{
		this.file = file;

		totalSize = file.length();
	}

	/**
	 * Reads a line of input from the BufferedReader and increments the linecount.
	 * Use of this allows the ProgressInputStream to keep track of the number of
	 * bytes read.
	 * @return
	 * @throws IOException
	 */
	String readLine()
		throws IOException
	{
		lineCount++;
		return in.readLine();
	}

	@Override
	public boolean isIndeterminate()
		{ return totalSize == 0; }

	@Override
	public int getMaximum()
		{ return 5555; }

	/**
	 * Returns the current value of progress of the job. Useful for updating
	 * progress bars such as that in ProgressDialog.
	 *
	 * @return
	 */
	@Override
	public int getValue()
	{
		if (is == null)
				return 0;

			// Update the value for the file currently being read
			bytesRead= is.getBytesRead();

			// But calculate the overall percentage using all the files
			long total = 0;
				total += bytesRead;

			return Math.round((total / (float) totalSize) * 5555);
	}

	/**
	 * Sets up the ProgressInputStream based on the StrudelFile provided.
	 * Also allows for GZip files to be read via the GZipInputStream, the boolean
	 * tryZipped determines whether or not we try to open a GZipInputStream or not.
	 *
	 * @param tryZipped true for trying to open a GZipInputStream, otherwise false.
	 * @return
	 * @throws Exception
	 */
	InputStream getInputStream(boolean tryZipped)
		throws Exception
	{
		// Reset the counter for this file
		lastBytesRead = bytesRead = 0;
		lastTime = System.currentTimeMillis();

		if (tryZipped)
		{
			// We always open the file itself (as this tracks the bytes read)
			is = new ProgressInputStream(file.getInputStream());

			try
			{
				// But we might have a gzip file, and the actual stream we want to
				// read is inside of it
				GZIPInputStream zis = new GZIPInputStream(is);
				return zis;
			}
			catch (Exception e) { is.close(); }
		}

		// If not, just return the normal stream
		is = new ProgressInputStream(file.getInputStream());

		return is;
	}

	/**
	 * Return the transfer rate in MB/sec or KB/sec.
	 *
	 * @return
	 */
	String getTransferRate()
	{
		// Time between reads
		long timeDiff = System.currentTimeMillis() - lastTime;
		long byteDiff = bytesRead - lastBytesRead;

		float bytesPerSec = byteDiff / (timeDiff / (float) 1000);
		float kbPerSec = bytesPerSec / 1024;

		if (kbPerSec >= 1024)
		{
			float mbPerSec = kbPerSec / 1024;
			return df.format(mbPerSec) + " MB/sec";
		}

		return df.format(kbPerSec) + " KB/sec";
	}
}
