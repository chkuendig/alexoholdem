package ao.holdem.game;

import ao.holdem.def.bot.BotProvider;


/**
 * Texas Hold'em game
 */
public interface Holdem
{
    //--------------------------------------------------------------------
    /**
     * @param numPlayers how many players there are, from 1 to 10.
     * @param botProvider will provide bots to play in seats.
     */
    public void configure( int         numPlayers,
                           BotProvider botProvider );


    //--------------------------------------------------------------------
    public Outcome play();
}
