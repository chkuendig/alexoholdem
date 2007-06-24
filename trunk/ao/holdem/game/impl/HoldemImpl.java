package ao.holdem.game.impl;

import ao.holdem.def.bot.BotProvider;
import ao.holdem.game.Holdem;
import ao.holdem.game.Outcome;
import org.apache.log4j.Logger;


/**
 * The caveat is that if someone raises and is all
 *  in, his raise must be at lease that of the previous raise
 *  in order for you to reraise him.
 *
 * Holdem Rules

    1) 2 hole cards distributed to each player.
    2) assign SB, BB relative to Dealer:
        - heads up: SB = dealer
              else: SB = clockwise of Dealer
        - BB = clockwise of SB
    3) preflop betting
        - round of betting(first to act: left of BB,
                               bet size: big blind)
    4) flop is dealt
    5) flop betting
        - round of betting(first to act: left of Dealer,
                               bet size: big blind)
    6) turn is dealt
    7) turn betting
        - round of betting(first to act: left of Dealer,
                               bet size: 2 x big blind)
    8) river dealt
    9) river betting
        - round of betting(first to act: left of Dealer,
                               bet size: 2 x big blind)
    10) showdown
 *
 */
public class HoldemImpl implements Holdem
{
    //--------------------------------------------------------------------
    private final static Logger log =
            Logger.getLogger(HoldemImpl.class.getName());


    //--------------------------------------------------------------------
    // 0 index means dealer.
//    private final BotProvider BY_SEAT[];
    private       int         button = -1;
    private       int         numPlayers;
    private       BotProvider botProvider;


    //--------------------------------------------------------------------
    public HoldemImpl() {}


    //--------------------------------------------------------------------
    public synchronized void
            configure( int         numPlayers,
                       BotProvider botProvider )
    {
        assert 2 <= numPlayers && numPlayers <= 10;

        log.debug("configuring holdem game for " +
                    numPlayers + " players.");
        this.numPlayers  = numPlayers;
        this.botProvider = botProvider;
    }


    //--------------------------------------------------------------------
    public synchronized Outcome play()
    {
        log.debug("starting hand.");
        advanceButton();

        HandPlay hand = new HandPlay(numPlayers, botProvider);

        log.debug("setting up hand.");
        hand.dealHoleCards();
        hand.designateBlinds();

        log.debug("preflop betting round (1st).");
        if (hand.roundOfBetting()) return hand.outcome();

        log.debug("flop.");
        hand.flop();

        log.debug("flop betting round (2nd).");
        if (hand.roundOfBetting()) return hand.outcome();

        log.debug("turn.");
        hand.turn();
        log.debug("turn betting round (3rd).");
        if (hand.roundOfBetting()) return hand.outcome();

        log.debug("river.");
        hand.river();

        log.debug("river betting round (4th).");
        if (hand.roundOfBetting()) return hand.outcome();

        log.debug("showdown.");
        return hand.showdown();
    }

    //--------------------------------------------------------------------
    private void advanceButton()
    {
        button = clockwise(button, 1);
        log.debug("advancing button to " + button +
                  " away from first dealer.");
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "HoldemImpl";
    }


    //--------------------------------------------------------------------
    private static int clockwise(int fromSeat, int by)
    {
        return mod10(fromSeat + by);
    }
    private static int mod10(int index)
    {
        return (index >= 0) ? index % 10 : 10 - index;
    }
}

