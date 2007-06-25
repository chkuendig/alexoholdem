package ao.holdem.def.bot;

import ao.holdem.def.state.domain.Domain;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A bot with some specific domain.
 */
public class LocalBot
{
    //--------------------------------------------------------------------
    private final Bot    BOT;
    private final Domain DOMAIN;


    //--------------------------------------------------------------------
    public LocalBot(Bot bot, Domain domain)
    {
        BOT    = bot;
        DOMAIN = domain;
    }


    //--------------------------------------------------------------------
    public Bot bot()
    {
        return BOT;
    }

    public Domain domain()
    {
        return DOMAIN;
    }


    //--------------------------------------------------------------------
    public static void writeTabDelimitedHeader(OutputStream to)
    {
        Domain.writeTabDelimitedHeader(to);
        new PrintStream(to).print("\tBot Name");
    }

    public void writeTabDelimited(OutputStream to)
    {
        DOMAIN.writeTabDelimited(to);
        new PrintStream(to).print("\t" + BOT);
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return BOT + " at " + DOMAIN;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalBot localBot = (LocalBot) o;

        return BOT.equals(localBot.BOT) &&
               DOMAIN.equals(localBot.DOMAIN);
    }

    @Override
    public int hashCode()
    {
        int result;
        result = BOT.hashCode();
        result = 31 * result + DOMAIN.hashCode();
        return result;
    }
}
