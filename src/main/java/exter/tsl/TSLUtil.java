package exter.tsl;

public class TSLUtil
{
  /**
   * Check if a TSL Object value's name is valid
   * @param name TSL Object value's name
   * @return true if the name is valid, false otherwise
   */
  static public boolean isValidValueName(String name)
  {
    int i;
    for(i = 0; i < name.length(); i++)
    {
      char c = name.charAt(i);
      if(!(Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-'))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Throw an IllegalArgumentException if the TSL Object's name is invalid.
   * @param name the Object's name.
   * @throws IllegalArgumentException if the TSL Object's name is invalid.
   */
  static public void validateValueName(String name)
  {
    if(name == null)
    {
      throw new IllegalArgumentException("Invalid TSL Object name: null.");
    }
    if(!TSLUtil.isValidValueName(name))
    {
      throw new IllegalArgumentException("Invalid TSL Object name: '" + name + "'.");
    }
  }
}
