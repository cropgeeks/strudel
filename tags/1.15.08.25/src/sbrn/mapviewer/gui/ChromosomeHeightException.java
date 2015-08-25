package sbrn.mapviewer.gui;

public class ChromosomeHeightException extends Exception
{
	@Override
	public String getMessage()
	{
		return "Insufficient vertical screen space for rendering chromosomes";
	}
}
