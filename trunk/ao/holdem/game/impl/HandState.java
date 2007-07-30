package ao.holdem.game.impl;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.domain.BetsToCall;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.domain.DealerDistance;
import ao.holdem.def.state.domain.Opposition;
import ao.holdem.def.state.env.GodEnvironment;
import ao.holdem.def.state.env.Player;
import ao.holdem.def.state.env.Position;
import ao.holdem.def.state.env.TakenAction;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Poker hand (ie. from pre-flop till showdown) history.
 */
public class HandState
{
    //--------------------------------------------------------------------
    private final static Logger log =
            Logger.getLogger(HandState.class.getName());

    private static final int DEALER = 0;


    //--------------------------------------------------------------------
    private Hole[]      holes;
    private int[]       commitment;
    private TakenAction actions[];
    private Community   community;
    private boolean[]   folded;
    private int         toMatch;
    private int         remainingBets;
    private int         smallBlind;
    private int         bigBlind;


    //--------------------------------------------------------------------
    public HandState(int numPlayers)
    {
        log.debug("initiating poker hand state.");

        holes      = new Hole[ numPlayers ];
        community  = new Community();
        folded     = new boolean[ numPlayers ];
        commitment = new int[ numPlayers ];
        actions    = new TakenAction[ numPlayers ];

//        active = new ArrayList<Integer>( numPlayers );
    }


    //--------------------------------------------------------------------
    public GodEnvironment envFor(int awayFromDealer)
    {
        log.debug("producing environment for: " +
                  awayFromDealer + " clockwise from dealer.");

        int    yourPosition     = -1;
        Player fromFirstToAct[] = new Player[ players() ];
        int    byPosition[]     = position2dealerDistance();
        for (int i = 0; i < players(); i++)
        {
            int dealerDistance = byPosition[i];

            Position pos      = new Position(i, players(), preFlop());
            fromFirstToAct[i] =
                    new Player(pos,
                               commitment[  dealerDistance ],
                               isActive(    dealerDistance ),
                               lastActionOf( dealerDistance ));

            if (awayFromDealer == dealerDistance)
            {
                yourPosition = i;
            }
        }

        return new GodEnvironment(
                    community,
                    fromFirstToAct,
                    yourPosition,
                    toCall(awayFromDealer),
                    potSize(),
                    commitment[awayFromDealer],
                    remainingBets,
                    domainBettingRound(),
                    holes, position2dealerDistance());
    }

    private int potSize()
    {
        int pot = 0;
        for (int commit : commitment)
        {
            pot += commit;
        }
        return pot;
    }


    //--------------------------------------------------------------------
    private int toCall(int awayFromDealer)
    {
        return toMatch - commitment[awayFromDealer];
    }

    public BetsToCall domainBets(int awayFromDealer)
    {
        return BetsToCall.from(toCall(awayFromDealer), betSize());
    }

    public DealerDistance domainPosition(int awayFromDealer)
    {
        return DealerDistance.from(awayFromDealer);
    }

    public Opposition domainOpposition()
    {
        return Opposition.fromPlayers( players() );
    }
    public BettingRound domainBettingRound()
    {
        return community.round();
    }

    public boolean canCheck(int awayFromDealer)
    {
        return toMatch <= commitment[ awayFromDealer ];
    }
    public boolean canRaise()
    {
        return remainingBets > 0;
    }


    //--------------------------------------------------------------------
    public void toMatch(int smallBlinds)
    {
        if (toMatch < smallBlinds)
        {
            log.debug("raise from " + toMatch +
                             " to " + smallBlinds + ".");
            toMatch = smallBlinds;
        }
    }

    public void replenishBets(int remainingBets)
    {
        log.debug("bets remaining in hand: " + remainingBets + ".");

        // this is done to take into account big blind
        this.remainingBets =
                Math.min(this.remainingBets + remainingBets, 4);
    }


    //--------------------------------------------------------------------
    public void checked(int awayFromDealer)
    {
        log.debug(awayFromDealer + " clockwise from dealer checks.");

//        actions[ awayFromDealer ] = TakenAction.CHECK;
        actions[ awayFromDealer ] = TakenAction.CALL;
    }

    public void called(int awayFromDealer)
    {
        log.debug(awayFromDealer + " clockwise from dealer calls.");

        commit(awayFromDealer, toMatch);
        actions[ awayFromDealer ] = TakenAction.CALL;
    }

    public void raised(int awayFromDealer)
    {
        raised(awayFromDealer, false);
    }
    private void raised(int awayFromDealer, boolean isBigBlind)
    {
        remainingBets--;
        log.debug(awayFromDealer + " clockwise from dealer raises. " +
                  remainingBets + " bets remaining.");

        if (isBigBlind)
        {
            commit(awayFromDealer, betSize());
        }
        else
        {
            commit(awayFromDealer, toMatch + betSize());
            actions[ awayFromDealer ] = TakenAction.RAISE;
        }
    }

    public void folded(int awayFromDealer)
    {
        log.debug(awayFromDealer + " clockwise from dealer folds.");

        folded[ awayFromDealer ] = true;
        actions[ awayFromDealer ] = TakenAction.FOLD;
    }


    //--------------------------------------------------------------------
    public void dealHoleCards(int awayFromDealer, Hole holeCards)
    {
        log.debug(awayFromDealer + " clockwise from dealer " +
                    "gets " + holeCards + " hole cards.");
        holes[ awayFromDealer ] = holeCards;
    }


    //--------------------------------------------------------------------
    public void designateSmallBlind(int awayFromDealer)
    {
        log.debug("small blind is " + awayFromDealer +
                  " clockwise from dealer.");
        smallBlind = awayFromDealer;
        commit(smallBlind, 1);
    }
    public void designateBigBlind(int awayFromDealer)
    {
        log.debug("big blind is " + awayFromDealer +
                  " clockwise from dealer.");
        bigBlind = awayFromDealer;
        raised(awayFromDealer, true);
//        commit(bigBlind, 2);
    }

    public void designateBlinds()
    {
        designateSmallBlind(
                headsUp() ? DEALER : clockwise(DEALER));
        designateBigBlind(
                clockwise(smallBlind));
    }


    //--------------------------------------------------------------------
    public void commit(int awayFromDealer, int bet)
    {
        assert commitment[ awayFromDealer ] <= bet;

        int delta = bet - commitment[ awayFromDealer ];
        if (delta != 0)
        {
            log.debug(awayFromDealer + " clockwise from dealer " +
                        "raises commitment by " + delta +
                                          " to " + bet   + ".");
            commitment[ awayFromDealer ] = bet;
            toMatch(bet);
        }
    }


    //--------------------------------------------------------------------
    public void dealFlop(Community flop)
    {
        log.debug("updating community with flop.");
        community = flop;
    }

    public void dealTurn(Card turn)
    {
        log.debug("updating community with turn.");
        community = community.addTurn(turn);
    }

    public void dealRiver(Card river)
    {
        log.debug("updating community with river.");
        community = community.addRiver(river);
    }


    //--------------------------------------------------------------------
    public boolean isActive(int awayFromDealer)
    {
        return !folded[ awayFromDealer ];
    }

    public TakenAction lastActionOf(int awayFromDealer)
    {
        TakenAction action = actions[ awayFromDealer ];
//        return (action == null) ? TakenAction.YET_TO_ACT
//                                : action;
        return action;
    }


    //--------------------------------------------------------------------
    public boolean preFlop()
    {
        return !community.hasFlop();
    }

    public boolean preTurn()
    {
        return !community.hasTurn();
    }

    public boolean headsUp()
    {
        return players() == 2;
    }


    //--------------------------------------------------------------------
    public int betSize()
    {
        return preTurn() ? 2 : 4;
    }


    //--------------------------------------------------------------------
    public List<Integer> activeByAwayFromDealerInActionOrder()
    {
        List<Integer> active = new ArrayList<Integer>();

        for (int awayFromDealerInActionOrder : position2dealerDistance())
        {
            if (isActive(awayFromDealerInActionOrder))
            {
                active.add( awayFromDealerInActionOrder );
            }
        }

        return active;
    }

    // byPosition()[0] = how far away from dealer is first to act
    private int[] position2dealerDistance()
    {
        int[] byPosition = new int[ players() ];

        for (int awayFromFirstToAct = 0,
                 awayFromDealer     = firstToAct();

                 awayFromFirstToAct < byPosition.length;

                 awayFromFirstToAct++,
                 awayFromDealer = clockwise(awayFromDealer))
        {
            byPosition[ awayFromFirstToAct ] = awayFromDealer;
        }

        return byPosition;
    }


    //--------------------------------------------------------------------
    public int players()
    {
        return holes.length;
    }

    private int clockwise(int from)
    {
        return (from + 1) % players();
    }

    private int firstToAct()
    {
        return clockwise(
                preFlop() ? bigBlind : DEALER);
    }
}
