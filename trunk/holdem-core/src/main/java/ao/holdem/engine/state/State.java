package ao.holdem.engine.state;

import ao.holdem.engine.RuleBreach;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;

import java.util.*;

/**
 * Hold'em hand state.
 *
 * Player index is clockwise with dealer being last.
 */
public class State
{
    //--------------------------------------------------------------------
    private static final int BETS_PER_ROUND = 4;


    //--------------------------------------------------------------------
    private final Round     round;
    private final Seat      seats[];
    private final int       nextToAct;
    private final int       remainingRoundBets;
    private final int       latestRoundStaker;
    private final ChipStack stakes;
    private final State     startOfRound;


    //--------------------------------------------------------------------
    // expects blind actions
    public State(int playerCount)
    {
        assert playerCount >= 2;

        seats = new Seat[ playerCount ];
        for (int i = 0; i < seats.length; i++)
        {
            seats[i] = initPlayerState(i);
        }

        boolean isHeadsUp = (playerCount == 2);

        round              = Round.PREFLOP;
        nextToAct          = (isHeadsUp ? 1 : 0);
        remainingRoundBets = BETS_PER_ROUND - 1; // -1 for upcoming BB
        latestRoundStaker  = -1;
        stakes             = ChipStack.ZERO;
        startOfRound       = this;
    }
    private Seat initPlayerState(int playerIndex) {
        return new Seat(playerIndex);
    }

    // automatically posts blinds
    public static State autoBlindInstance(
            int playerCount)
    {
        return new State(playerCount)
                    .advanceBlind( Action.SMALL_BLIND )
                    .advanceBlind( Action.BIG_BLIND   );
    }

    // copy constructor
    private State(Round copyRound,
                  Seat  copySeats[],
                  int   copyNextToAct,
                  int   copyRemainingRoundBets,
                  int   copyLatestRoundStaker,
                  ChipStack copyStakes,
                  State copyStartOfRound)
    {
        round              = copyRound;
        seats              = copySeats;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
        stakes             = copyStakes;
        startOfRound       = (copyStartOfRound == null ? this : copyStartOfRound);
    }


    //--------------------------------------------------------------------
    public State advance(int playerIndex, Action act)
    {
        validateNextAction(playerIndex, act);
        return act.isBlind()
                ? advanceBlind(act)
                : advanceVoluntary(act);
    }
    public State advance(Action act)
    {
        return advance(nextToAct().player(), act);
    }


    //--------------------------------------------------------------------
    public Map<AbstractAction, State> viableActions()
    {
        return actions(true);
    }
    public Map<AbstractAction, State> actions(boolean nonDominated)
    {
        EnumMap<AbstractAction, State> validActions =
                new EnumMap<>(AbstractAction.class);

        State quitFold  = nonDominated && canCheck()
                          ? null : advanceIfValid(Action.FOLD);
        State checkCall = firstValid(Action.CALL, Action.CHECK);
        State betRaise  = firstValid(Action.RAISE, Action.BET);

        if (quitFold != null)
            validActions.put(AbstractAction.QUIT_FOLD, quitFold);

        if (checkCall != null)
            validActions.put(AbstractAction.CHECK_CALL, checkCall);

        if (betRaise != null)
            validActions.put(AbstractAction.BET_RAISE, betRaise);

        return validActions;
    }

    private State firstValid(Action... acts)
    {
        for (Action act : acts)
        {
            State nextState = advanceIfValid(act);
            if (nextState != null) return nextState;
        }
        return null;
    }
    private State advanceIfValid(Action act)
    {
        try
        {
            return advance(act);
        }
        catch (RuleBreach ignored)
        {
            return null;
        }
    }

                       
    //--------------------------------------------------------------------
    private State advanceVoluntary(Action act)
    {
        Round   nextRound  = nextBettingRound(act);
        boolean roundEnder = (round != nextRound);
        boolean betRaise   =
                    act.abstraction() == AbstractAction.BET_RAISE;

        Seat nextPlayers[]    = nextPlayers(act);
        int nextNextToAct     = nextNextToAct(nextPlayers, roundEnder);
        int nextRemainingBets = nextRemainingBets(roundEnder, betRaise);
        ChipStack nextStakes  = nextStakes(nextPlayers, betRaise);
        int nextRoundStaker   =
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
                        .advance(act, stakes, betSize(), round);
        return nextPlayers;
    }

    private ChipStack nextStakes(Seat[] nextPlayers, boolean betRaise)
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
        return latestRoundStaker ==
               nextStakingUnfoldedAfter( nextToAct );
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

        if ( isSmall && !stakes.equals( ChipStack.ZERO ))
            throw new RuleBreach("Small Blind already in.");
        if (!isSmall && stakes.compareTo( ChipStack.BIG_BLIND ) >= 0)
            throw new RuleBreach("Big Blind already in.");

        ChipStack betSize       = ChipStack.blind(isSmall);
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
    public State advanceQuitter(int quitterIndex)
    {
        if (seats[ quitterIndex ].isFolded() /*||
            atEndOfHand()*/) return this;

        Seat[] nextPlayers = seats.clone();
        nextPlayers[ quitterIndex ] = nextPlayers[ quitterIndex ].fold(round);
        
        int perlimStaker   =
                roundStakerAfterQuit(nextPlayers, quitterIndex);
        int nextActive =
                nextActiveAfter(nextPlayers, index(nextToAct - 1));
        boolean roundEnder = (perlimStaker == nextActive);

        int nextRoundStaker =
                roundEnder ? -1 : perlimStaker;

        Round nextRound = roundEnder ? round.next() : round;
        int nextNextToAct =
                roundEnder
                ? nextActiveAfter(nextPlayers, seats.length - 1)
                : nextActive;

        return new State(nextRound,
                         nextPlayers,
                         nextNextToAct,
                         nextRemainingBets(roundEnder, false),
                         nextRoundStaker,
                         stakes,
                         roundEnder ? null : startOfRound);
    }

    /**
     * the next round staker after quitting,
     * is first in the chain of players that played before me,
     *  and that have commitment equal stakes.
     * or -1 if nobody played before me this round,
     *  or if nobody has commitment equal stakes.
     *
     * @param pStates with quitter being folded
     * @param index seat #
     * @return seat # of the new round staker
     * */
    private int roundStakerAfterQuit(
            Seat pStates[], int index)
    {
        if (latestRoundStaker != index) return latestRoundStaker;

        int earliestInChain = -1;
        for (int i = 1; i <= index; i++)
        {
            int  prevActorIndex = index(index - i);
            Seat prevActorSeat  = pStates[ prevActorIndex ];

            if (! prevActorSeat.isFolded() &&
                  prevActorSeat.voluntarilyActedDuring( round ) &&
                  prevActorSeat.commitment().equals( stakes ))
            {
                earliestInChain = prevActorIndex;
            }
            else if (earliestInChain != -1)
            {
                return earliestInChain;
            }
        }

        return earliestInChain;
    }


    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return atShowdownOrSingleActive()                  ||
               nextToAct == -1                             ||
               nextToActIsLastActivePlayer() && canCheck() ||
               nextToAct == nextUnfoldedAfter(nextToAct);
    }
    private boolean nextToActIsLastActivePlayer()
    {
        return nextToAct == nextActiveAfter(nextToAct);
    }
    private boolean atShowdownOrSingleActive()
    {
        return round == null;
    }

    public ChipStack betSize()
    {
        return isSmallBet() ? ChipStack.SMALL_BET : ChipStack.BIG_BET;
    }
    private boolean isSmallBet()
    {
        return round == Round.PREFLOP ||
               round == Round.FLOP;
    }

    public Action reify(FallbackAction easyAction)
    {
        return easyAction.fallback(canCheck(), canRaise());
    }
    public boolean canCheck()
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

    public ChipStack pot()
    {
        ChipStack pot = ChipStack.ZERO;
        for (Seat player : seats)
            pot = pot.plus( player.commitment() );
        return pot;
    }

    public ChipStack stakes()
    {
        return stakes;
    }

    public int remainingBetsInRound()
    {
        return remainingRoundBets;
    }

    public ChipStack toCall()
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
        int index = nextUnfoldedAfter(seats, playerIndex);
        while (startOfRound.seats[ index ].isAllIn() ||
                seats[ index ].isAllIn() &&
               !seats[ index ].commitment().equals( stakes ))
        {
            index = nextUnfoldedAfter(index);

//            if (playerIndex == index) return index;
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

    /**
     * @return true if dealer is next to act,
     *          if the game is over, then returns as if
     *          
     */
    public boolean dealerIsNext()
    {
        return nextToAct == (seats.length - 1);
    }

    public boolean isStartOfRound()
    {
        return startOfRound.equals( this );
    }

    public HeadsUpStatus headsUpStatus()
    {
        if (seats.length != 2) return null;
        if (! atEndOfHand()) return HeadsUpStatus.IN_PROGRESS;
        if (numActivePlayers() == 2) return HeadsUpStatus.SHOWDOWN;
        return seats[1].equals( unfolded().get(0) )
               ? HeadsUpStatus.DEALER_WINS
               : HeadsUpStatus.DEALEE_WINS;
    }


    //--------------------------------------------------------------------
    private void validateNextAction(
            int playerIndex, Action act)
    {
        if (atEndOfHand()) {
            throw new RuleBreach("the hand is already done: " + this);
        }

        if (nextToAct().player() != playerIndex) {
            throw new RuleBreach("expected " + nextToAct().player() + " not " + playerIndex);
        }

        if (remainingRoundBets == 0 &&
                act.abstraction() == AbstractAction.BET_RAISE) {
            throw new RuleBreach("round betting cap exceeded");
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "State{" +
//                     "round              = " + round                +
//               "\n\t, seats              = " + Arrays.asList(seats) +
//               "\n\t, nextToAct          = " + seats[nextToAct]     +
//               "\n\t, remainingRoundBets = " + remainingRoundBets   +

//                     "stakes             = " + stakes               +
                       "pot                = " + pot()                +
               "\n    , to call            = " + toCall()             +
               '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
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

    @Override public int hashCode()
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
