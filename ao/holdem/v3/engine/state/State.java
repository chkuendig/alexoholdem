package ao.holdem.v3.engine.state;

import ao.holdem.engine.HoldemRuleBreach;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.RealAction;
import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Round;
import ao.holdem.v3.model.Stack;
import ao.holdem.v3.model.act.AbstractAction;
import ao.holdem.v3.model.act.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holdem hand state.
 */
public class State
{
    //--------------------------------------------------------------------
    private static final int BETS_PER_ROUND = 4;


    //--------------------------------------------------------------------
    private final Round round;
    private final Seat  seats[];
    private final int   nextToAct;
    private final int   remainingRoundBets;
    private final int   latestRoundStaker;
    private final Stack stakes;
    private final State startOfRound;


    //--------------------------------------------------------------------
    // expects blind actions
    public State(List<Avatar> clockwiseDealerLast)
    {
        seats = new Seat[ clockwiseDealerLast.size() ];
        for (int i = 0; i < seats.length; i++)
        {
            seats[i] = initPlayerState(clockwiseDealerLast, i);
        }

        round              = Round.PREFLOP;
        nextToAct          = 0;
        remainingRoundBets = BETS_PER_ROUND - 1; // -1 for upcoming BB
        latestRoundStaker  = -1;
        stakes             = Stack.ZERO;
        startOfRound       = this;
    }
    private Seat initPlayerState(
            List<Avatar> clockwiseDealerLast,
            int          playerIndex)
    {
        Avatar player = clockwiseDealerLast.get( playerIndex );
        return new Seat(
                    player, Stack.ZERO, false, false);
    }

    // automatically posts blinds
    public static State autoBlindInstance(
            List<Avatar> clockwiseDealerLast)
    {
        return new State(clockwiseDealerLast)
                    .advanceBlind( Action.SMALL_BLIND )
                    .advanceBlind( Action.BIG_BLIND   );
    }

    // copy constructor
    private State(Round copyRound,
                  Seat  copySeats[],
                  int   copyNextToAct,
                  int   copyRemainingRoundBets,
                  int   copyLatestRoundStaker,
                  Stack copyStakes,
                  State copyStartOfRound)
    {
        round              = copyRound;
        seats              = copySeats;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
        stakes             = copyStakes;
        startOfRound       = (copyStartOfRound == null)
                              ? this : copyStartOfRound;
    }


    //--------------------------------------------------------------------
    public State advance(Avatar player, Action act)
    {
        validateNextAction(player, act);
        return act.isBlind()
                ? advanceBlind(act)
                : advanceVoluntary(act);
    }
    public State advance(Action act)
    {
        return advance(nextToAct().player(), act);
    }


    //--------------------------------------------------------------------
    private State advanceVoluntary(Action act)
    {
        Round   nextRound  = nextBettingRound(act);
        boolean roundEnder = (round != nextRound);
        boolean betRaise   =
                    act.abstraction() == AbstractAction.BET_RAISE;

        Seat  nextPlayers[]     = nextPlayers(act);
        int   nextNextToAct     = nextNextToAct(nextPlayers, roundEnder);
        int   nextRemainingBets = nextRemainingBets(roundEnder, betRaise);
        Stack nextStakes        = nextStakes(nextPlayers, betRaise);
        int   nextRoundStaker   =
                nextLatestRoundStaker(act, roundEnder, betRaise);

        return new State(nextRound,
                         nextPlayers,
                         nextNextToAct,
                         nextRemainingBets,
                         nextRoundStaker,
                         nextStakes,
                         roundEnder ? null : startOfRound);
    }

    private Seat[] nextPlayers(Action act)
    {
        Seat nextPlayers[] = seats.clone();
        nextPlayers[nextToAct] =
                nextPlayers[nextToAct]
                        .advance(act, stakes, betSize());
        return nextPlayers;
    }

    private Stack nextStakes(Seat[] nextPlayers, boolean betRaise)
    {
        return (betRaise)
               ? nextPlayers[nextToAct].commitment()
               : stakes;
    }

    private int nextNextToAct(
            Seat[] nextPlayers, boolean roundEnder)
    {
        return roundEnder
                ? nextActiveAfter(nextPlayers, seats.length - 1)
                : nextActiveAfter(nextPlayers, nextToAct);
    }

    private Round nextBettingRound(Action act)
    {
        return nextActionCritical()
                ? canRaise()
                    ? (act.abstraction() == AbstractAction.BET_RAISE)
                        ? round
                        : round.next()
                    : round.next()
                : round;
    }
    // it determines weather or not the next action can end the round.
    private boolean nextActionCritical()
    {
        return latestRoundStaker == nextStakingUnfoldedAfter( nextToAct );// ||
                //nextToAct == nextActiveAfter( nextToAct );
    }

    private int nextLatestRoundStaker(
            Action act, boolean roundEnder, boolean betRaise)
    {
        return roundEnder
                ? -1
                : (betRaise
                   ? nextToAct
                   : (latestRoundStaker == -1 && // when all players check
                        act.abstraction() != AbstractAction.QUIT_FOLD)
                      ? nextToAct
                      : latestRoundStaker);
    }

    private int nextRemainingBets(boolean roundEnder, boolean betRaise)
    {
        return roundEnder
               ? BETS_PER_ROUND
               : (betRaise
                   ? remainingRoundBets - 1
                   : remainingRoundBets);
    }


    //--------------------------------------------------------------------
    private State advanceBlind(Action act)
    {
        boolean isSmall = act.isSmallBlind();

        if ( isSmall && !stakes.equals( Stack.ZERO ))
            throw new HoldemRuleBreach("Small Blind already in.");
        if (!isSmall && stakes.compareTo( Stack.BIG_BLIND ) >= 0)
            throw new HoldemRuleBreach("Big Blind already in.");

        Stack betSize       = Stack.blind(isSmall);
        Seat  nextPlayers[] = seats.clone();
        nextPlayers[nextToAct] =
                nextPlayers[nextToAct].advanceBlind(act, betSize);

        int nextNextToAct   = nextActiveAfter(nextPlayers, nextToAct);
        int nextRoundStaker =
                act == Action.BIG_BLIND_ALL_IN
                ? nextToAct : latestRoundStaker;

        return new State(round,
                         nextPlayers,
                         nextNextToAct,
                         remainingRoundBets,
                         nextRoundStaker,
                         betSize,
                         startOfRound);
    }


    //--------------------------------------------------------------------
    // assert ! quitter.equals( nextToAct().handle() )
    public State advanceQuitter(Avatar quitter)
    {
        int index = indexOf(quitter);
        if (seats[ index ].isFolded()) return this;

        Seat nextPlayers[] = seats.clone();
        nextPlayers[ index ] = nextPlayers[ index ].fold();

        int perlimStaker   =
                roundStakerAfterQuit(nextPlayers, index);
        boolean roundEnder =
                perlimStaker == nextStakingUnfoldedAfter(
                                    nextPlayers, index(nextToAct - 1));

        int nextRoundStaker =
                roundEnder
                ? -1 : perlimStaker;

        Round nextRound = roundEnder ? round.next() : round;
        int nextNextToAct =
                roundEnder
                ? nextActiveAfter(nextPlayers, seats.length - 1)
                : nextActiveAfter(nextPlayers, index(nextToAct - 1));

        return new State(nextRound,
                         nextPlayers,
                         nextNextToAct,
                         nextRemainingBets(roundEnder, false),
                         nextRoundStaker,
                         stakes,
                         roundEnder ? null : startOfRound);
    }

    private int roundStakerAfterQuit(
            Seat        pStates[],
            int         index)
    {
        if (latestRoundStaker != index) return latestRoundStaker;

        for (int i = 1; i <= index; i++)
        {
            int  prevActorIndex = index(index - i);
            Seat prevActorSeat  = pStates[ prevActorIndex ];

            if (! prevActorSeat.isFolded())
            {
                return prevActorIndex;
            }
        }

        return -1;
    }


    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return atShowdown()    ||
               nextToAct == -1 ||
               nextToActIsLastActivePlayer() && canCheck() ||
               nextToAct == nextUnfoldedAfter(nextToAct);
    }
    private boolean nextToActIsLastActivePlayer()
    {
        return nextToAct == nextActiveAfter(nextToAct);
    }
    private boolean atShowdown()
    {
        return round == null;
    }

    public Stack betSize()
    {
        return isSmallBet() ? Stack.SMALL_BET : Stack.BIG_BET;
    }
    private boolean isSmallBet()
    {
        return round == Round.PREFLOP ||
               round == Round.FLOP;
    }

    public RealAction toRealAction(EasyAction easyAction)
    {
        return easyAction.toRealAction(canCheck(),
                                       canRaise());
    }
    private boolean canCheck()
    {
        return stakes.equals( nextToAct().commitment() );
    }
    public boolean canRaise()
    {
        return remainingRoundBets > 0;
    }


    //--------------------------------------------------------------------
    // these functions are provided as convenience,
    //  they are not actually used by track the state.

    public Round round()
    {
        return round;
    }

    public Seat[] seats()
    {
        return seats;
    }
    public Seat seats(int indexDealerLast)
    {
        return seats[   indexDealerLast < 0
                      ? indexDealerLast + seats.length
                      : indexDealerLast];
    }

    public int numActivePlayers()
    {
        int count = 0;
        for (Seat state : seats)
            if (state.isActive()) count++;
        return count;
    }

    public List<Seat> unfolded()
    {
        List<Seat> condenters = new ArrayList<Seat>();

        int firstUnfolded = nextUnfoldedAfter( nextToAct - 1 );
        int cursor        = firstUnfolded;
        do
        {
            condenters.add( seats[cursor] );
            cursor = nextUnfoldedAfter(cursor);
        }
        while (cursor != firstUnfolded);

        return condenters;
    }

    public Stack pot()
    {
        Stack pot = Stack.ZERO;
        for (Seat player : seats)
            pot = pot.plus( player.commitment() );
        return pot;
    }

    public Stack stakes()
    {
        return stakes;
    }

    public int remainingBetsInRound()
    {
        return remainingRoundBets;
    }

    public Stack toCall()
    {
        return stakes.minus( nextToAct().commitment() );
    }
    public int betsToCall()
    {
        return toCall().bets( isSmallBet() );
    }

    public double position()
    {
        return (double) (nextToAct + 1) / seats.length;
    }
    public double activePosition()
    {
        int activePosition = 0;
        for (int i = 0; i <= nextToAct; i++)
        {
            if (seats[i].isActive()) activePosition++;
        }
        return (double) activePosition / numActivePlayers();
    }


    //--------------------------------------------------------------------
    private int index(int fromIndex)
    {
        return fromIndex < 0
                ? fromIndex + seats.length
                : fromIndex % seats.length;
    }
    private int indexOf(Avatar player)
    {
        for (int i = 0; i < seats.length; i++)
        {
            if (seats[ i ].player().equals( player )) return i;
        }
        return -1;
    }

    private int nextActiveAfter(int playerIndex)
    {
        return nextActiveAfter(seats, playerIndex );
    }
    private int nextActiveAfter(Seat[] states, int playerIndex)
    {
        for (int i = 1; i <= states.length; i++)
        {
            int index = index(playerIndex + i);
            if (states[ index ].isActive())
            {
                return index;
            }
        }
        return -1;
    }

    private int nextStakingUnfoldedAfter(int playerIndex)
    {
        return nextStakingUnfoldedAfter(seats, playerIndex);
    }
    private int nextStakingUnfoldedAfter(
            Seat[] pStates, int playerIndex)
    {
        int index = nextUnfoldedAfter(pStates, playerIndex);
        while (startOfRound.seats[ index ].isAllIn() ||
                seats[ index ].isAllIn() &&
               !seats[ index ].commitment().equals( stakes ))
        {
            index = nextUnfoldedAfter(index);
        }
        return index;
    }

    private int nextUnfoldedAfter(int playerIndex)
    {
        return nextUnfoldedAfter(seats, playerIndex);
    }
    private int nextUnfoldedAfter(
            Seat pStates[],
            int  playerIndex)
    {
        for (int i = 1; i <= pStates.length; i++)
        {
            int index = index(playerIndex + i);
            if (! pStates[ index ].isFolded())
            {
                return index;
            }
        }
        return -1;
    }

    public Seat nextToAct()
    {
        return seats[ nextToAct ];
    }

    public int nextToActIndex()
    {
        return nextToAct;
    }


    //--------------------------------------------------------------------
    private void validateNextAction(
            Avatar player, Action act)
    {
        if (atEndOfHand())
            throw new HoldemRuleBreach(
                        "the hand is already done: " + this);

        if (! nextToAct().player().equals( player ))
            throw new HoldemRuleBreach(
                        "expected " + nextToAct().player() + " not " +
                                      player);

        if (remainingRoundBets == 0 &&
                act.abstraction() == AbstractAction.BET_RAISE)
            throw new HoldemRuleBreach("round betting cap exceeded");
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return nextToAct() + ", " + round;
    }

    public boolean equals(Object o)
    {
        //if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        State state = (State) o;

        return latestRoundStaker  == state.latestRoundStaker  &&
               nextToAct          == state.nextToAct          &&
               remainingRoundBets == state.remainingRoundBets &&
               round              == state.round              &&
               Arrays.equals(seats, state.seats)              &&
               stakes.equals(state.stakes);

    }

    public int hashCode()
    {
        int result;
        result = round.hashCode();
        result = 31 * result + Arrays.hashCode(seats);
        result = 31 * result + nextToAct;
        result = 31 * result + remainingRoundBets;
        result = 31 * result + latestRoundStaker;
        result = 31 * result + stakes.hashCode();
        return result;
    }
}
