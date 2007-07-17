package ao.holdem.game.impl;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.bot.LocalBot;
import ao.holdem.def.state.domain.*;
import ao.holdem.game.Holdem;
import ao.holdem.game.Outcome;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;


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
//    private final LocalBotProvider BY_SEAT[];
    private       int         button = -1;
//    private       int         numPlayers;
//    private       LocalBotProvider botProvider;
    private List<Bot> bots;
//    private List<BotHandle> bots;


    //--------------------------------------------------------------------
    public HoldemImpl() {}


    //-------------------------------------------------------------------
//    public void configure(List<BotHandle> players)
//    {
    public void configure(List<Bot> players)
    {
        shutDown();

        bots = players;
//        for (BotHandle bot : bots)
//        {
//            bot.bot().introduce();
//        }
        for (Bot bot : bots)
        {
            bot.introduce();
        }
    }

    public void shutDown()
    {
        if (bots != null)
        {
//            for (BotHandle bot : bots)
//            {
//                bot.bot().retire();
//            }
            for (Bot bot : bots)
            {
                bot.retire();
            }
            bots.clear();
        }
    }


    //--------------------------------------------------------------------
//    public Outcome play(HandHistory            startingFrom,
//                        Map<PlayerHandle, Bot> players)
//    {
//        return null;
//    }


    //--------------------------------------------------------------------
    public Outcome play()
    {
        log.debug("starting hand.");
        advanceButton();

        HandPlay hand = new HandPlay(bots);

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
    public LocalBot winningBigBlind()
    {
        int bigBlind = (bots.size() == 2) ? 1 : 2;

        Domain domain =
                new Domain(BetsToCall.ZERO,
                           BettingRound.PREFLOP,
                           DealerDistance.from(bigBlind),
                           Opposition.fromPlayers(bots.size()));

        return new LocalBot(bots.get(bigBlind), domain);
    }


    //--------------------------------------------------------------------
    private void advanceButton()
    {
        Collections.rotate(bots, -1);

//        button = clockwise(button, 1);
        log.debug("advancing button to " + button +
                  " away from first dealer.");
    }

//    private int clockwise(int fromSeat, int by)
//    {
//        return modNumPlayers(fromSeat + by);
//    }
//    private int modNumPlayers(int index)
//    {
//        return (index >= 0)
//               ? index % bots.size()
//               : bots.size() - index;
//    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "HoldemImpl";
    }
}

