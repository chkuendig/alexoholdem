package ao.holdem.history.irc;

import com.google.inject.Inject;

/**
 *
 */
public class IrcRunner
{
    //--------------------------------------------------------------------
    @Inject IrcHistorian historian;


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        historian.fromSnapshot(ircDir);
    }
}
