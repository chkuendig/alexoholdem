package ao.state;

import ao.holdem.engine.DeckCardSource;
import ao.holdem.model.CardSource;
import ao.holdem.model.Hand;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.stats.HandStats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class StateManager
{
    //--------------------------------------------------------------------
    private HandState   head;
    private HandStats   stats;
    private List<Event> events = new ArrayList<Event>();
    private CardSource  cards  = new DeckCardSource();
    private boolean     roundJustChanged;
//    private HandActionExamples


    //--------------------------------------------------------------------
    private StateManager() {}

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            CardSource         cards)
    {
        this(clockwiseDealerLast);
        this.cards = cards;
    }
    public StateManager(
            List<PlayerHandle> clockwiseDealerLast)
    {
        head = new HandState(clockwiseDealerLast);
    }

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds,
            CardSource         cards)
    {
        this(clockwiseDealerLast, autoPostBlinds);
        this.cards = cards;
    }
    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds)
    {
        head = autoPostBlinds
               ? HandState.autoBlindInstance(clockwiseDealerLast)
               : new HandState(clockwiseDealerLast);
        stats = new HandStats(this);
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

        HandState nextState = head.advance(act);
        process(act, nextState);
        head = nextState;

        events.add( event );
    }


    //--------------------------------------------------------------------
    private void process(RealAction act, HandState nextState)
    {
        roundJustChanged = (head.round() != nextState.round());

        stats.advance(head.nextToAct(),
                      act,
                      nextState,
                      cards().community());
//        stats.
    }

    public boolean roundJustChanged()
    {
        return roundJustChanged;
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
    public HandState head()
    {
        return head;
    }

    public HandStats stats()
    {
        return stats;
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
        Collection<PlayerState> finalists = head().unfolded();

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
    public StateManager continueFrom()
    {
        StateManager proto = new StateManager();
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
