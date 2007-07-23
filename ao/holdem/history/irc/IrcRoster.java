package ao.holdem.history.irc;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * ROSTER INFORMATION (in hroster file)
    ------------------------------------
    766303976  8  Marzon spiney doublebag neoncap maurer andrea zorak justin
 */
public class IrcRoster
{
    //--------------------------------------------------------------------
    // 766303976  8  Marzon spiney doublebag neoncap maurer andrea zorak justin
    private final static Pattern pat =
            Pattern.compile("(\\d+)\\s+" +
                            "(\\d+)\\s+" +
                            "(.*)");

    //--------------------------------------------------------------------
    private final long   timestamp;
    private final int    numPlayers;
    private final String names[];


    //--------------------------------------------------------------------
    public IrcRoster(String line)
    {
        Matcher m = pat.matcher(line);
        if (! m.matches()) throw new Error();

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
}
