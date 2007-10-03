package ao.net.ps;

import java.util.*;

/**
 *
 */
public class PsParser
{
    //--------------------------------------------------------------------
    private String             prevLines[] = new String[0];
    private LinkedList<String> buffer      = new LinkedList<String>();


    //--------------------------------------------------------------------
    public PsParser() {}


    //--------------------------------------------------------------------
    public void add(String snapshot)
    {
        String lines[] = snapshot.split("[\n\r]+");

        //if (prevLines.length == 0)
        //{
        //    buffer.addAll( Arrays.asList(lines) );
        //    return;
        //}

        assert lines.length >= prevLines.length;

        offset_loop:
        for (int offset = 0; offset < prevLines.length; offset++)
        {
            for (int i = 0; i < (prevLines.length - offset); i++)
            {
                if (! lines[i].equals( prevLines[offset + i] ))
                        continue offset_loop;
            }

            
        }


        Collection<String> subBuffer = new ArrayList<String>();
        if (! buffer.isEmpty())
        {
            subBuffer.add( buffer.getLast() );
        }
        if (buffer.size() > 1)
        {
            subBuffer.add(buffer.get( buffer.size() - 2 ));
        }

        LinkedList<String> addends = new LinkedList<String>();
        int i = lines.length - 1;
        while (lines.length < i &&
               !subBuffer.contains(lines[i]))
        {
            addends.addFirst(lines[i++]);
        }


        for (String line : lines)
        {
            if (! buffer.contains(line))
            {
                buffer.add( line );
            }
        }
        prevLines = lines;
    }
}
