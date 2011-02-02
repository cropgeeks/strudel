package sbrn.mapviewer.io;

import java.io.*;
import sbrn.mapviewer.*;

public abstract class AbtractFileParser extends TrackableReader
{
	
	boolean isIndeterminate = false;
	
	//any inheriting parsers must implement this method and instantiate a new dataset in it
	public abstract void parseFile() throws Exception;
	
	@Override
	public void runJob() throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(true), "ASCII"));
		parseFile();
		if (okToRun)
		{
			isIndeterminate = true;
			boolean mapNumbersOK = DataLoadUtils.setUpViewAfterDataLoading();
			in.close();
			if (!mapNumbersOK)
			{
				resetDataLoadingFlags();
				int numMapsSupported = DataLoadUtils.calculateNumberOfSupportedMaps();
				String errorMessage = "\nOne or more mapsets contain more chromosomes than can be rendered on the available canvas." + "\nThe number of maps supported at the current canvas size is " + numMapsSupported + ".";
				throw new Exception(errorMessage);
			}
		}
	}
	
	private void resetDataLoadingFlags()
	{
		Strudel.winMain.fatController.loadOwnData = false;
		Strudel.winMain.fatController.dragAndDropDataLoad = false;
		Strudel.winMain.fatController.recentFileLoad = false;
	}

	@Override
	public boolean isIndeterminate()
	{
		return isIndeterminate;
	}

	@Override
	public String getMessage()
	{
		String progressBarMessage = null; 
		if(isIndeterminate)
			progressBarMessage = "Initializing link sets";
		else
		{
			String rate = getTransferRate();
			progressBarMessage = "Loading at: " + rate;
		}
		return progressBarMessage;
	}


	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
