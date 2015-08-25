package sbrn.mapviewer.io;

import java.io.*;

public class FileFormatDetector
{
	final static int FILEFORMAT_STRUDEL = 1;
	final static int FILEFORMAT_MAF = 2;
	
	public static int detectFileFormat(File file) throws IOException, FileNotFoundException
	{
		int format = -1;
		
		//parse the start of the file 
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String firstLine = reader.readLine();
		reader.close();
		
		//Strudel format files start with "feature"
		if(firstLine.startsWith("feature"))
			format = FILEFORMAT_STRUDEL;
		
		/*MAF files start with a MAF header: 			  
			 ##maf version=1 scoring=tba.v8 			 
		 */
		if(firstLine.startsWith("##maf"))
			format = FILEFORMAT_MAF;
		
		return format;
	}
	
}
