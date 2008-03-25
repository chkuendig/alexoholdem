package ao.holdem.engine.state;

import ao.ai.monte_carlo.uct.Node;
import ao.ai.opp_model.predict.act.HandStats;
import ao.holdem.engine.DeckCardSource;
import ao.holdem.engine.persist.Event;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.persist.PlayerHandle;
import ao.holdem.model.BettingRound;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.card.CardSource;
import ao.holdem.model.card.Hand;
import ao.holdem.model.card.Hole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

//    private Map<PlayerHandle, Context> allInContexts =
//                new HashMap<PlayerHandle, Context>();


    //--------------------------------------------------------------------
    private StateManager() {}

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds,
            CardSource         cardSource)
    {
        head  = new HandState(clockwiseDealerLast);
        cards = cardSource;
        stats = new HandStats( head.players() );

        if (autoPostBlinds)
        {
            advance(RealAction.SMALL_BLIND);
            advance(RealAction.BIG_BLIND);
        }
    }

    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            CardSource         cardSource)
    {
        this(clockwiseDealerLast, false, cardSource);
    }
//    public StateManager(
//            List<PlayerHandle> clockwiseDealerLast)
//    {
//        this(clockwiseDealerLast, false, new DeckCardSource());
//    }
    public StateManager(
            List<PlayerHandle> clockwiseDealerLast,
            boolean            autoPostBlinds)
    {
        this(clockwiseDealerLast, autoPostBlinds, new DeckCardSource());
    }


    //--------------------------------------------------------------------
    public CardSource cards()
    {
        return cards;
    }

    public boolean holeVisible(PlayerHandle forPlayer)
    {
        Hole hole = cards.holeFor( forPlayer );
        return (hole != null && hole.bothCardsVisible());
    }


    //--------------------------------------------------------------------
    public PlayerState advance(RealAction act)
    {
        int          nextActIndex = head.nextToActIndex();
        PlayerHandle actor        = nextToAct();
        Event        event        = new Event(actor, head().round(), act);
        
        HandState nextState = head.advance(act);
        process(head, actor, act, nextState);
        head = nextState;

        events.add( event );
        return head.players()[ nextActIndex ];
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
//            if (act.isAllIn() && !act.isBlind())
//            {
//                allInContexts.put(
//                        actor,
//                        stats.forPlayer(actor.getId()).nextActContext());
//            }

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

            Hole hole = cards.holeFor( handle );
            if (hole != null)
            {
                hist.addHole( handle, hole );
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
        if (winners.isEmpty()) return;

        PlayerHandle smallBlind, bigBlind;
        if (events.get(0).getAction().isSmallBlind())
        {
            smallBlind = events.get(0).getPlayer();
            bigBlind   = events.get(1).getPlayer();
        }
        else
        {
            smallBlind = null;
            bigBlind   = events.get(0).getPlayer();
        }

        Money totalLost = new Money();
        for (PlayerState player : head().players())
        {
            if (! winners.contains(player))
            {
                Money commit = player.commitment();
                if (player.handle().equals( smallBlind ))
                {
                    commit = commit.plus( Money.SMALL_BLIND );
                }
                else if(player.handle().equals( bigBlind ))
                {
                    commit = commit.plus( Money.BIG_BLIND );
                }

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

//    public List<Event> events()
//    {
//        return events;
//    }

//    public Context allInContext(PlayerHandle forPlayer)
//    {
//        return allInContexts.get( forPlayer );
//    }

    public PlayerHandle nextToAct()
    {
        return head().nextToAct().handle();
    }

    public boolean atEndOfHand()
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
            if (cards.community().round() != BettingRound.RIVER)
            {
                return Collections.emptyList();
            }

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
    public StateManager prototype(boolean faithful)
    {
        StateManager proto     = new StateManager();
        proto.head             = head;
        proto.stats            = stats.prototype();
        proto.roundJustChanged = roundJustChanged;

        if (faithful)
        {
            proto.cards  = cards.prototype();
            proto.events = new ArrayList<Event>( events );
        }

        return proto;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return head().toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateManager that = (StateManager) o;
        return head.equals(that.head);
    }

    public int hashCode()
    {
        return head.hashCode();
    }

    public List<Node> generateActionNodes(Node current)
    {
        List<Node> actionNodes = new ArrayList<Node>( 3 );
        if (atEndOfHand()) return actionNodes;

        Node afterFold = current.advance(this, SimpleAction.FOLD);
        actionNodes.add(afterFold);
        
        Node afterCall = current.advance(this, SimpleAction.CALL);
        actionNodes.add(afterCall);

        if (head().canRaise())
        {
            actionNodes.add(
                current.advance(this, SimpleAction.RAISE));
        }

        return actionNodes;
    }
}
