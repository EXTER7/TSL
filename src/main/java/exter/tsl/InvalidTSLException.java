package exter.tsl;

/**
 * Exception thrown when a TSLReader ecounters malformed TSL when reading. 
 */
public class InvalidTSLException extends Exception
{
  private static final long serialVersionUID = 7960288289987802766L;

  public InvalidTSLException(String message)
  {
    super(message);
  }
}
