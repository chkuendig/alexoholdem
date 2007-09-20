package ao.holdem.tourney;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class Scoreboard
{
    //--------------------------------------------------------------------
    public static Scoreboard getInstance()
    {
        return new Scoreboard();
    }


    //--------------------------------------------------------------------
    private final Map<LocalBot, Winnings> SCORES;


    //--------------------------------------------------------------------
    private Scoreboard()
    {
        SCORES = new HashMap<LocalBot, Winnings>();
    }


    //--------------------------------------------------------------------
    public void update(LocalBot bot, Winnings winnings)
    {
        Winnings existing = SCORES.get( bot );
        if (existing == null)
        {
            SCORES.put(bot, new Winnings(winnings));
        }
        else
        {
            existing.add( winnings );
        }
    }


    //--------------------------------------------------------------------
    public void writeTabDelimited(OutputStream to) throws IOException
    {
        LocalBot.writeTabDelimitedHeader(to);
        new PrintStream(to).println("\tWinnings");

        for (Map.Entry<LocalBot, Winnings> entry : SCORES.entrySet())
        {
            entry.getKey().writeTabDelimited(to);
            new PrintStream(to).println("\t" + entry.getValue());
        }
        to.flush();
    }
}
