package ao.state;

import ao.holdem.engine.DeckCardSource;
import ao.holdem.model.CardSource;
import ao.holdem.model.Hand;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * pot ratio,
 *
 * data types:
 *      # bets/raises,
 *      # checks,
 *      # calls,
 *      bet ratio,
 *      pot ratio
 *
 * data context:
 *      actual this round / max this round,
 *      actual in prev rounds / max in prev rounds,
 *      actual in all rounds / max in all brounds 
 *
 * data set:
 *      for you,
 *      for unfolded,
 *      for unfolded - you,
 *      for all,
 *      for all - you
 */
public class Context
{
    //--------------------------------------------------------------------
    private HoldemState head;
    private List<Event> events = new ArrayList<Event>();
    private CardSource  cards  = new DeckCardSource();

    private boolean roundJustChanged;





    //--------------------------------------------------------------------
    private Context() {}

    public Context(
            List<PlayerHandle> clockwiseDealerLast,
            CardSource         cards)
    {
        this(clockwiseDealerLast);
        this.cards = cards;
    }
    public Context(
            List<PlayerHandle> clockwiseDealerLast)
    {
        head = new HoldemState(clockwiseDealerLast);
    }

    public Context(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds,
            CardSource         cards)
    {
        this(clockwiseDealerLast, autoPostBlinds);
        this.cards = cards;
    }
    public Context(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds)
    {
        head = autoPostBlinds
               ? HoldemState.autoBlindInstance(clockwiseDealerLast)
               : new HoldemState(clockwiseDealerLast);
    }


    //--------------------------------------------------------------------
    public CardSource cards()
    {
        return cards;
    }


    //--------------------------------------------------------------------
    public void advance(RealAction act)
    {
        Event event = new Event(nextToAct(), head().round(), act);

        HoldemState nextState = head.advance(act);
        process(act, nextState);
        head = nextState;

        events.add( event );
    }


    //--------------------------------------------------------------------
    private void process(RealAction act, HoldemState newState)
    {
        roundJustChanged = (head.round() != newState.round());
    }

    public boolean roundJustChanged()
    {
        return roundJustChanged;
    }
    public double immediatePotOdds()
    {
        return ((double) head.toCall().smallBlinds()) /
                (head.toCall().smallBlinds() +
                    head.pot().smallBlinds());
    }




    //--------------------------------------------------------------------
    public HandHistory toHistory()
    {
        HandHistory hist = new HandHistory();

        List<PlayerState> winners = winners();
        for (PlayerState player : head().players())
        {
            PlayerHandle handle = player.handle();
            hist.addPlayer( handle );

            if (winners.contains( player ))
            {
                hist.addHole( handle, cards.holeFor(handle) );
            }
        }

        hist.setCommunity( cards.community() );

        for (Event event : events)
        {
            hist.addEvent( event );
        }

        assignDeltas(hist);
        return hist;
    }

    private void assignDeltas(HandHistory hist)
    {
        List<PlayerState> winners = winners();

        Money totalLost = new Money();
        for (PlayerState player : head().players())
        {
            if (! winners.contains(player))
            {
                Money commit = player.commitment();
                hist.setDelta(player.handle(),
                              Money.ZERO.minus(commit));
                totalLost = totalLost.plus(commit);
            }
        }
        
        Money winnings  = totalLost.split(     winners.size() );
        Money remainder = totalLost.remainder( winners.size() );
        for (int i = 0; i < winners.size(); i++)
        {
            PlayerHandle winner = winners.get(i).handle();
            Money total = (i == 0)
                          ? winnings
                          : winnings.plus( remainder );
            hist.setDelta(winner, total);
        }
    }


    //--------------------------------------------------------------------
    public HoldemState head()
    {
        return head;
    }

    public PlayerHandle nextToAct()
    {
        return head().nextToAct().handle();
    }

    public boolean winnersKnown()
    {
        return head().atEndOfHand();
    }

    // XXX TODO:
    // need to account for different pot groups, when
    //  somebody goes all-in in the middle of a hand
    //  and then wins a portion of the final hand.
    // so really, this should be List<List<PlayerState>>
    //  grouping together splitting winners by commitment group
    public List<PlayerState> winners()
    {
        List<PlayerState>       winners   = new ArrayList<PlayerState>();
        Collection<PlayerState> finalists = head().finalists();

        if (finalists.size() == 1)
        {
            winners.addAll( finalists );
        }
        else if (finalists.size() > 1)
        {
            // for each commitment, from lowest to highest
            //  this accoun

            short topHandRank = -1;
            for (PlayerState player : finalists)
            {
                short handRank =
                    new Hand(cards.holeFor(player.handle()),
                             cards.community()).value();

                if (handRank > topHandRank)
                {
                    winners.clear();
                    topHandRank = handRank;
                }
                if (handRank == topHandRank) winners.add( player );
            }
        }
        return winners;
    }


    //--------------------------------------------------------------------
    public Context continueFrom()
    {
        Context proto = new Context();
        proto.head    = head;
        proto.cards   = cards.prototype();
        return proto;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return head().toString();
    }
}
