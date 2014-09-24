package exter.tsl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Stack;

public class TSLWriter
{

  static public class Formatter
  {
    private int indent;
    private String indent_str;
    private boolean newline;
    
    public Formatter setIndent(int ind)
    {
      if(ind < 0)
      {
        ind = 0;
      }
      indent = ind;
      int i;
      StringBuilder builder = new StringBuilder();
      for(i = 0; i < indent; i++)
      {
        builder.append(' ');
      }
      indent_str = builder.toString();
      return this;
    }
    
    protected String getIndentString()
    {
      return indent_str;
    }

    public Formatter setNewLine(boolean nl)
    {
      newline = nl;
      return this;
    }
    
    public boolean getNewLine()
    {
      return newline;
    }
    
    public Formatter(int ind,boolean nl)
    {
      setIndent(ind);
      newline = nl;
    }
    
    public Formatter(Formatter f)
    {
      this(f.indent,f.newline);
    }
  }

  private static final String ERROR_ROOTCLOSED = "Root TSL Object already closed.";
  
  private OutputStreamWriter osw;
  private int level;
  private boolean first_element;
  private boolean root_element;
  private boolean closed;
  private Formatter formatter;
  private Stack<Formatter> formatter_stack;


  private void writeSeparator(boolean put_comma) throws IOException
  {
    if(root_element)
    {
      return;
    }
    if(put_comma && !first_element)
    {
      osw.write(',');
    }
    if(formatter.getNewLine())
    {
      osw.write("\n");
      int i;
      for(i = 0; i < level; i++)
      {
        osw.write(formatter.getIndentString());
      }
    } else
    {
      osw.write(' ');
    }
  }
  
  private String encodeValue(String value)
  {
    StringBuilder builder = new StringBuilder();
    int i;
    for(i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);
      switch(c)
      {
        case '"':
          builder.append("\\\"");
          break;
        case '\\':
          builder.append("\\\\");
          break;
        default:
          builder.append(c);  
      }
    }    
    return builder.toString();
  }

  
  public TSLWriter(OutputStream os)
  {
    osw = new OutputStreamWriter(os);
    level = 0;
    first_element = true;
    root_element = true;
    closed = false;
    formatter = new Formatter(2,true);
    formatter_stack = new Stack<Formatter>();
  }
    

  public Formatter getFormatter()
  {
    return formatter;
  }
  
  public TSLWriter pushFormatter()
  {
    formatter_stack.push(formatter);
    formatter = new Formatter(formatter);
    return this;
  }
  
  public TSLWriter popFormatter()
  {
    if(formatter_stack.size() > 0)
    {
      formatter = formatter_stack.pop();
    }
    return this;
  }
  
  public TSLWriter putString(String name,String value) throws IOException
  {
    if(value == null)
    {
      return this;
    }
    TSLUtil.validateValueName(name);

    if(closed)
    {
      throw new IllegalStateException(ERROR_ROOTCLOSED);
    }
    if(root_element)
    {
      closed = true;
    }
    writeSeparator(true);

    osw.write(name);
    osw.write(" \"");
    osw.write(encodeValue(value));
    osw.write('"');
    
    first_element = false;
    return this;
  }
  
  public TSLWriter startObject(String name) throws IOException
  {

    TSLUtil.validateValueName(name);

    if(closed)
    {
      throw new IllegalStateException(ERROR_ROOTCLOSED);
    }
    
    writeSeparator(true);

    osw.write(name);
    osw.write(" [");
    first_element = true;
    root_element = false;
    level++;
    return this;
  }
  
  public TSLWriter endObject() throws IOException
  {
    if(level == 0 || closed)
    {
      throw new IllegalStateException(ERROR_ROOTCLOSED);      
    }
    if(first_element)
    {
      throw new IllegalStateException("Cannot close an empty TSL Object.");
    }
    level--;
    writeSeparator(false);

    osw.write("]");
    if(level == 0)
    {
      osw.flush();
      closed = true;
    }
    root_element = false;
    first_element = false;
    return this;
  }
}
