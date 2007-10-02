package ao.state;

import ao.holdem.engine.DeckCardSource;
import ao.holdem.model.card.CardSource;
import ao.holdem.model.card.Hand;
import ao.holdem.model.card.Hole;
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
    private List<Event> events = new ArrayList<Event>();
    private CardSource  cards  = new DeckCardSource();
    private HandStats   stats;
    private boolean     roundJustChanged;



    //--------------------------------------------------------------------
    private StateManager() {}

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds,
            CardSource         cardSource)
    {
        head = autoPostBlinds
               ? HandState.autoBlindInstance(clockwiseDealerLast)
               : new HandState(clockwiseDealerLast);
        cards = cardSource;
        init();
    }

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            CardSource         cardSource)
    {
        this(clockwiseDealerLast, false, cardSource);
    }
    public StateManager(
            List<PlayerHandle> clockwiseDealerLast)
    {
        this(clockwiseDealerLast, false, new DeckCardSource());
    }
    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds)
    {
        this(clockwiseDealerLast, autoPostBlinds, new DeckCardSource());
    }

    private void init()
    {
        stats = new HandStats(this);
//        nextActContext.advance(head);
    }


    //--------------------------------------------------------------------
    public CardSource cards()
    {
        return cards;
    }


    //--------------------------------------------------------------------
    public void advance(RealAction act)
    {
        PlayerHandle actor = nextToAct();
        Event        event = new Event(actor, head().round(), act);

        HandState nextState = head.advance(act);
        process(head, actor, act, nextState);
        head = nextState;

        events.add( event );
    }
    public void advanceQuitter(PlayerHandle quitter)
    {
        if (quitter.equals( nextToAct() ))
        {
            advance( RealAction.QUIT );
        }
        else
        {
            Event event =
                    new Event(quitter, head().round(), RealAction.QUIT);

            HandState nextState = head.advanceQuitter(quitter, events);
            process(head, quitter, RealAction.QUIT, nextState);
            head = nextState;

            events.add( event );
        }
    }


    //--------------------------------------------------------------------
    private void process(HandState    beforeAct,
                         PlayerHandle actor,
                         RealAction   act,
                         HandState    afterAct)
    {
        roundJustChanged = (beforeAct.round() != afterAct.round());

        if (! afterAct.atEndOfHand())
        {
            stats.advance(
                beforeAct, actor, act, afterAct);
        }
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

        assignDeltas(hist, winners);
        return hist;
    }

    private void assignDeltas(
            HandHistory       hist,
            List<PlayerState> winners)
    {
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
                Hole hole = cards.holeFor(player.handle());
                if (hole == null || hole.incomplete()) continue;

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
        StateManager proto     = new StateManager();
        proto.head             = head;
        proto.cards            = cards.prototype();
        proto.stats            = stats.prototype();
        proto.roundJustChanged = roundJustChanged;
        return proto;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return head().toString();
    }
}
