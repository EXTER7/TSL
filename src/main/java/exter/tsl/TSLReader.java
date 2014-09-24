package exter.tsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses a TSL from an InputStream
 */
public class TSLReader
{
  // Input stream.
  private BufferedReader isr;
  
  // Internal state.
  private enum ReaderState
  {
    NAME,
    VALUE,
    COMMA_END,
    VALUESTART_OBJECT,
    CHARESCAPE
  }
  
  // Reader State
  public enum State
  {
    START,      // Start of TSL stream

    STRING,     // String Value
    
    OBJECT,     // Object Value
    
    ENDOBJECT,  // End of Object
    
    END         // End of TSL stream
  }
  
  private ReaderState reader_state;
  private State state;
  private String name;
  private String string;
  private int level;

  // Internal string builder.
  private char[] builder;
  private int builder_length;
  
  private void putCharBuilder(char c)
  {
    if(builder_length == builder.length)
    {
      char[] nbuilder = new char[builder.length + 1024];
      int i;
      for(i = 0; i < builder.length; i++)
      {
        nbuilder[i] = builder[i];
      }
      builder = nbuilder;
    }
    builder[builder_length++] = c;
  }
  
  public TSLReader(InputStream is)
  {
    builder = new char[4096];
    builder_length = 0;

    isr = new BufferedReader(new InputStreamReader(is));
    reader_state = ReaderState.NAME;
    name = null;
    string = null;
    state = State.START;
  }
  
  /**
   * Get the current value's name
   * @return Current value's name
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * Get the current string value.
   * @return Value string, or null if the current TSL value is not a string.
   */
  public String getString()
  {
    return string;
  }
  
  /**
   * Get the reader's state.
   * @return The reader's state.
   */
  public State getState()
  {
    return state;
  }
  
  /**
   * Skip an object.
   * The reader's state is set to {@link State.ENDOBJECT} corresponding the skipped object.
   * Does nothing if the state is not {@link State.OBJECT}.
   * @throws InvalidTSLException if a TSL parsing error occurs.
   */
  public void skipObject() throws InvalidTSLException, IOException
  {
    if(state != State.OBJECT)
    {
      return;
    }
    int lv = level - 1;
    while(level > lv)
    {
      moveNext();
    }
  }
  
  /**
   * Advance the reader to next value.
   * @throws InvalidTSLException if a TSL parsing error occurs.
   */
  public void moveNext() throws InvalidTSLException, IOException
  {
    if(state == State.ENDOBJECT && level == 0)
    {
      state = State.END;
    }
    if(state == State.END)
    {
      return;
    }
    builder_length = 0;
    while(true)
    {
      int i;
      i = isr.read();
      if(i == -1)
      {
        state = State.END;
        throw new InvalidTSLException("Unexpected end of stream.");
      }
      char c = (char)i;
      switch(reader_state)
      {
        case NAME:
          if(Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-')
          {
            putCharBuilder(c);
          } else if(c == '"')
          {
            name = String.valueOf(builder, 0, builder_length);
            builder_length = 0;
            reader_state = ReaderState.VALUE;
          } else if(c == '[')
          {
            name = String.valueOf(builder, 0, builder_length);
            string = null;
            state = State.OBJECT;
            reader_state = ReaderState.NAME;
            level++;
            return;
          } else if(Character.isWhitespace(c))
          {
            if(builder_length > 0)
            {
              name = String.valueOf(builder, 0, builder_length);
              reader_state = ReaderState.VALUESTART_OBJECT;
            }
          } else
          {
            state = State.END;
            name = null;
            string = null;
            throw new InvalidTSLException("Unexpected name character: '"+ String.valueOf(c) + "'.");
          }
          break;
        case VALUE:
          if(c == '"')
          {
            reader_state = ReaderState.COMMA_END;
            string = String.valueOf(builder, 0, builder_length);
            if(level == 0)
            {
              state = State.END;
            } else
            {
              state = State.STRING;
            }
            return;
          } else if(c == '\\')
          {
            reader_state = ReaderState.CHARESCAPE;
          } else
          {
            putCharBuilder(c);
          }
          break;
        case COMMA_END:
          if(c == ',')
          {
            reader_state = ReaderState.NAME;
          } else if(c == ']')
          {
            level--;
            state = State.ENDOBJECT;
            return;
          } else if(!Character.isWhitespace(c))
          {
            state = State.END;
            throw new InvalidTSLException("Expected ',' or ']'.");
          }
          break;
        case VALUESTART_OBJECT:
          if(!Character.isWhitespace(c))
          {
            if(c == '"')
            {
              reader_state = ReaderState.VALUE;
              builder_length = 0;
            } else if(c == '[')
            {
              name = String.valueOf(builder, 0, builder_length);
              string = null;
              state = State.OBJECT;
              reader_state = ReaderState.NAME;
              level++;
              return;              
            } else
            {
              state = State.END;
              throw new InvalidTSLException("Expected '[' or value after Oject name");
            }
          }
          break;
        case CHARESCAPE:
          if(c == '"')
          {
            putCharBuilder('"');
            reader_state = ReaderState.VALUE;
          } else if(c == '\\')
          {
            putCharBuilder('\\');
            reader_state = ReaderState.VALUE;
          } else
          {
            state = State.END;
            throw new InvalidTSLException("Invalid value escape sequence '\\" + String.valueOf(c) + "'.");
          }
          break;
      }
    }
  }
}
