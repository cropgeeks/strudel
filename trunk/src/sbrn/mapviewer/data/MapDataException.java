package sbrn.mapviewer.data;

/**
 * Exception class for errors related to use of the map API.
 */
public class MapDataException extends Exception
{
	/**
	 * Constructs a new MapDataException with null as its detail message.
	 */
	public MapDataException()
	{
		super();
	}
	
	/**
	 * Constructs an MapDataException with the specified detail message.
	 * @param s the detail message
	 */
	public MapDataException(String s)
	{
		super(s);
	}
}