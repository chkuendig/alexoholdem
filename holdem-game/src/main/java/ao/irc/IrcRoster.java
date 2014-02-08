package ao.irc;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ROSTER INFORMATION (in hroster file)
    ------------------------------------
    766303976  8  Marzon spiney doublebag neoncap maurer andrea zorak justin
 */
public class IrcRoster
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(IrcRoster.class);


    //--------------------------------------------------------------------
    // 766303976  8  Marzon spiney doublebag neoncap maurer andrea zorak justin
    private final static Pattern pat =
            Pattern.compile(//"\\D*" +
                            "(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(.*)");

    public static IrcRoster fromLine(String line)
    {
        try
        {
            return new IrcRoster(line.trim());
        }
        catch (Error e)
        {
            LOG.error("can't load from line: " + line, e);
            return null;
        }
    }


    //--------------------------------------------------------------------
    private final long   timestamp;
    private final int    numPlayers;
    private final String names[];


    //--------------------------------------------------------------------
    private IrcRoster(String line)
    {
        Matcher m = pat.matcher(line);
        if (! m.matches())
        {
            throw new Error("can't match roster: " + line);
        }

        timestamp  = Long.parseLong(   m.group(1));
        numPlayers = Integer.parseInt( m.group(2));
        names      = m.group(3).split("\\s+");
    }


    //--------------------------------------------------------------------
    public long timestamp()
    {
        return timestamp;
    }

    public int numPlayers()
    {
        return numPlayers;
    }

    public String[] names()
    {
        return names;
    }

    public int size()
    {
        return names.length;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Arrays.toString( names );
    }
}
