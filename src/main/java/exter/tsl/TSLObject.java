package exter.tsl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a TSL Object.
 */
public class TSLObject
{
  //Strings
  private Map<String, List<String>> strings_map;
  //Objects
  private Map<String, List<TSLObject>> objects_map;

  /**
   * Create a blank object.
   */
  public TSLObject()
  {
    strings_map = new HashMap<String, List<String>>();
    objects_map = new HashMap<String, List<TSLObject>>();
  }

  
  /**
   * Remove all values.
   */
  public void clear()
  {
    strings_map.clear();
    objects_map.clear();
  }
  
  /**
   * Create from TSLReader.
   * The reader's state must be TSLReader.State.OBJECT
   * @param reader TSL reader to use
   * @throws IllegalStateException if reader's state is not TSLReader.State.OBJECT
   */
  public TSLObject(TSLReader reader) throws InvalidTSLException, IOException
  {
    strings_map = new HashMap<String, List<String>>();
    objects_map = new HashMap<String, List<TSLObject>>();

    loadFromReader(reader);
  }

  /**
   * Load contents from TSLReader, clearing all existing content.
   * The reader's state must be TSLReader.State.OBJECT
   * @param reader TSL reader to use
   * @throws IllegalStateException if reader's state is not TSLReader.State.OBJECT
   */
  public void loadFromReader(TSLReader reader) throws InvalidTSLException, IOException
  {
    clear();

    if(reader.getState() != TSLReader.State.OBJECT)
    {
      throw new IllegalStateException("Current value is not a TSL Object");
    }
    while(true)
    {
      reader.moveNext();

      switch(reader.getState())
      {
        case STRING:
          putString(reader.getName(), reader.getString());
          break;
        case OBJECT:
          putObject(reader.getName(), new TSLObject(reader));
          break;
        case ENDOBJECT:
          return;
        default:
          assert false;
      }
    }
  }

  /**
   * Returns the all strings with the specified name.
   * @param name Name of strings.
   * @return List of all strings with the specified name.
   */
  public List<String> getStringList(String name)
  {
    List<String> child = strings_map.get(name);
    if(child == null)
    {
      child = new ArrayList<String>();
    }
    return Collections.unmodifiableList(child);
  }

  /**
   * Returns all objects with the specified name.
   * @param name Name of the objects.
   * @return List of all objects with the specified name.
   */
  public List<TSLObject> getObjectList(String name)
  {
    List<TSLObject> child = objects_map.get(name);
    if(child == null)
    {
      child = new ArrayList<TSLObject>();
    }
    return Collections.unmodifiableList(child);
  }

  /**
   * Returns the first instance of an object with the specified name.
   * @param name Name of object.
   * @return First object with the specified name, null if no object with the spcified name exists.
   */
  public TSLObject getObject(String name)
  {
    List<TSLObject> child = objects_map.get(name);
    if(child == null || child.size() < 1)
    {
      return null;
    }
    return child.get(0);
  }



  /**
   * Puts a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, String value)
  {
    TSLUtil.validateValueName(name);
    List<String> list = strings_map.get(name);
    if(list == null)
    {
      list = new ArrayList<String>();
      list.add(value);
      strings_map.put(name, list);
    } else
    {
      list.add(value);
    }
  }

  /**
   * Puts an char represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, char value)
  {
    putString(name,String.valueOf(value));
  }

  /**
   * Puts a byte represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, byte value)
  {
    putString(name,String.valueOf(value));
  }
  
  /**
   * Puts a short represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, short value)
  {
    putString(name,String.valueOf(value));
  }

  /**
   * Puts an integer represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, int value)
  {
    putString(name,String.valueOf(value));
  }


  /**
   * Puts an long represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, long value)
  {
    putString(name,String.valueOf(value));
  }

  /**
   * Put a float represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, float value)
  {
    putString(name,String.valueOf(value));
  }

  /**
   * Put a double represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, double value)
  {
    putString(name,String.valueOf(value));
  }

  /**
   * Puts an double represented as a string.
   * @throws IllegalArugmentException if the name is invalid.
   * @param name Name of the string.
   * @param value Value to put.
   */
  public void putString(String name, BigDecimal value)
  {
    putString(name,value.toPlainString());
  }

  /**
   * Puts an object.
   * @throws IllegalArugmentException if the name is invalid.
   * @param obj Object to put.
   */
  public void putObject(String name,TSLObject obj)
  {
    TSLUtil.validateValueName(name);
    List<TSLObject> list = objects_map.get(name);
    if(list == null)
    {
      list = new ArrayList<TSLObject>();
      list.add(obj);
      objects_map.put(name, list);
    } else
    {
      list.add(obj);
    }
  }

  /**
   * Remove all values of a given name.
   * @param name Value's name.
   */
  public void removeValues(String name)
  {
    strings_map.remove(name);
    objects_map.remove(name);
  }

  /**
   * Remove all strings of a given name and value.
   * @param name String's name.
   */
  public void removeValues(String name, String str)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list != null)
    {
      value_list.remove(str);
      if(value_list.isEmpty())
      {
        strings_map.remove(name);
      }
    }
  }

  /**
   * Remove all objects of a given name and value.
   * @param name Object's name.
   */
  public void removeValues(String name, TSLObject obj)
  {
    List<TSLObject> collection_list = objects_map.get(name);
    if(collection_list != null)
    {
      collection_list.remove(obj);
      if(collection_list.isEmpty())
      {
        objects_map.remove(name);
      }
    }
  }

  /**
   * Writes the Object to a TSLWriter.
   * Does nothing if the object is empty.
   * @param writer Destination TSL writer.
   * @param name Name for the object.
   * @throws IOException from the TSLWriter.
   */
  public void write(TSLWriter writer,String name) throws IOException
  {
    if(objects_map.isEmpty() && strings_map.isEmpty())
    {
      return;
    }
    TSLUtil.validateValueName(name);
    writer.startObject(name);
    writer.pushFormatter();
    writer.getFormatter().setNewLine(!objects_map.isEmpty());
    for(Map.Entry<String, List<String>> child : strings_map.entrySet())
    {
      String child_name = child.getKey();
      for(String value : child.getValue())
      {
        writer.putString(child_name, value);
      }
    }
    for(Map.Entry<String, List<TSLObject>> child : objects_map.entrySet())
    {
      for(TSLObject collection : child.getValue())
      {
        collection.write(writer,child.getKey());
      }
    }
    writer.endObject();
    writer.popFormatter();
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + objects_map.hashCode();
    result = prime * result + strings_map.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
    {
      return true;
    }
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof TSLObject))
    {
      return false;
    }
    TSLObject other = (TSLObject) obj;
    return objects_map.equals(other.objects_map) && strings_map.equals(other.strings_map);
  }

  /**
   * Gets a string.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the value.
   * @param def Default value.
   * @return Value string, defualt if not found.
   */
  public String getString(String name, String def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    return value_list.get(0);
  }

  /**
   * Gets a string, converted to a char.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as a char, defualt if not found, or the string is not a single character.
   */
  public char getStringAsChar(String name, char def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    String value = value_list.get(0);
    if(value.length() != 1)
    {
      return def;
    }
    return value.charAt(0);
  }

  /**
   * Gets a string, converted to a byte.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as a byte, defualt if not found, or not a valid integer.
   */
  public byte getStringAsByte(String name, byte def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Byte.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }

  /**
   * Gets a string, converted to a short.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as a short, defualt if not found, or not a valid integer.
   */
  public short getStringAsShort(String name, short def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Short.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }
  
  /**
   * Gets a string, converted to an int.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as an int, defualt if not found, or not a valid integer.
   */
  public int getStringAsInt(String name, int def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Integer.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }

  /**
   * Gets a string, converted to a long.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as a long, defualt if not found, or not a valid integer.
   */
  public long getStringAsLong(String name, long def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Long.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }
  
  /**
   * Gets a string, converted to a float.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return String as float, defualt if not found, or not a valid float.
   */
  public float getStringAsFloat(String name, float def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Float.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }

  /**
   * Gets a string as a double.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return Value as double, defualt if not found, or not a valid double.
   */
  public double getStringAsDouble(String name, double def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return Double.valueOf(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }

  /**
   * Gets a string as a BigDecimal.
   * If multiple instances exists, the first instance is returned.
   * @param name Name of the string.
   * @param def Default value.
   * @return Value as BigDecimal, defualt if not found, or not a valid BigDecimal.
   */
  public BigDecimal getStringAsBigDecimal(String name, BigDecimal def)
  {
    List<String> value_list = strings_map.get(name);
    if(value_list == null)
    {
      return def;
    }
    try
    {
      return new BigDecimal(value_list.get(0));
    } catch(NumberFormatException e)
    {
      return def;
    }
  }

  
  /**
   * Gets all strings of the same name as a Byte ArrayList.
   * Values that are not valid Bytes are skipped.
   * @param name Name of the value.
   * @return Values as Byte ArrayList.
   */
  public ArrayList<Byte> getStringAsByteList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Byte> result = new ArrayList<Byte>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Byte.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }

  /**
   * Gets all strings of the same name as a Short ArrayList.
   * Values that are not valid Shorts are skipped.
   * @param name Name of the value.
   * @return Values as Short ArrayList.
   */
  public ArrayList<Short> getStringAsShortList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Short> result = new ArrayList<Short>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Short.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }
  
  /**
   * Gets all strings of the same name as an Integer ArrayList.
   * Values that are not valid Integers are skipped.
   * @param name Name of the value.
   * @return Values as Integer ArrayList.
   */
  public ArrayList<Integer> getStringAsIntegerList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Integer> result = new ArrayList<Integer>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Integer.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }

  /**
   * Gets all strings of the same name as a Long ArrayList.
   * Values that are not valid Longs are skipped.
   * @param name Name of the value.
   * @return Values as Long ArrayList.
   */
  public ArrayList<Long> getStringAsLongList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Long> result = new ArrayList<Long>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Long.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }
  
  /**
   * Gets all strings of the same name as a Float ArrayList.
   * Values that are not valid Floats are skipped.
   * @param name Name of the value.
   * @return Values as Float ArrayList.
   */
  public ArrayList<Float> getStringAsFloatList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Float> result = new ArrayList<Float>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Float.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }


  /**
   * Gets all strings of the same name as a Double ArrayList.
   * Values that are not valid Doubles are skipped.
   * @param name Name of the value.
   * @return Values as Double ArrayList.
   */
  public ArrayList<Double> getStringAsDoubleList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<Double> result = new ArrayList<Double>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(Double.valueOf(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }
  

  /**
   * Gets all strings of the same name as a BigDecimal ArrayList.
   * Values that are not valid BigDecimal are skipped.
   * @param name Name of the value.
   * @return Values as BigDecimal ArrayList.
   */
  public ArrayList<BigDecimal> getStringAsBigDecimalList(String name)
  {
    List<String> value_list = strings_map.get(name);
    ArrayList<BigDecimal> result = new ArrayList<BigDecimal>();
    if(value_list == null)
    {
      return result;
    }
    
    for(String val:value_list)
    {
      try
      {
        result.add(new BigDecimal(val));
      } catch(NumberFormatException e)
      {
      }
    }
    return result;
  }


  
  /**
   * Get the unique names of all strings in the object
   * @return The unique names of all strings in the object
   */
  public Set<String> getStringNames()
  {
    return Collections.unmodifiableSet(strings_map.keySet());
  }

  /**
   * Get the unique names of all objects in the object
   * @return The unique names of all objects in the object
   */
  public Set<String> getObjectNames()
  {
    return Collections.unmodifiableSet(objects_map.keySet());
  }
}
