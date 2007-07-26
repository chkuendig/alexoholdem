package ao.holdem.history.irc;

import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history_game.Dealer;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 *
 */
public class IrcRunner
{
    //--------------------------------------------------------------------
    @Inject IrcHistorian historian;
    @Inject Provider<Dealer> dealerProvider;
    @Inject PlayerHandleLookup players;


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        historian.fromSnapshot(ircDir);
    }
}
