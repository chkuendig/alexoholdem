package ao.holdem.game;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.bot.LocalBot;

import java.util.List;


/**
 * Texas Hold'em game
 */
public interface Holdem
{
    //--------------------------------------------------------------------
//    /**
//     * @param numPlayers how many players there are, from 1 to 10.
//     * @param botProvider will provide bots to play in seats.
//     */
//    public void configure( int         numPlayers,
//                           LocalBotProvider botProvider );

    public void configure(List<Bot> players);
    public void shutDown();


    //--------------------------------------------------------------------
    public Outcome play();


    //--------------------------------------------------------------------
    /**
     * @return used to assign winnings to the big blind, when no
     *              action was taken by big blind.
     */
    public LocalBot winningBigBlind();
}
