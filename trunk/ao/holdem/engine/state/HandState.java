package ao.holdem.engine.state;

import ao.holdem.engine.HoldemRuleBreach;
import ao.holdem.model.BettingRound;
import ao.holdem.model.Money;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.engine.persist.Event;
import ao.holdem.engine.persist.PlayerHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * To handle the corner case:
 *  unexpected move on burney, PREFLOP
 *   2	0	0	2	2	[7d, 8c, 9d, Jd, 4d]
 *   burney    812871541  2  1 B   -     -     -   1000    5    4 Js 4s
 *   THyde     812871541  2  2 BA  -     -     -      1    1    2 5c 3d
 * Awareness of bet quantities is needed, fortunetally
 *  this corner case is irrelavent because no action is necessary.
 *
 * Also, cases such as:
 *   the hand is already done
 *   2	0	0	0	0	[]
 *   geg       797477841  2  1 BQ  -     -     -         2648   10   10
 *   Liver     797477841  2  2 Q   -     -     -         2470    0    0
 * Are not gonna get accepted from IRC because really they are
 *  being interpreted correctly.  This error comes up because the Quits
 *  are asynchronouse and IRC doesn't contain timing information.
 *
 *
 * Note, obects of this class are immutable.
 */
public class HandState
{
    //--------------------------------------------------------------------
    private static final    int BETS_PER_ROUND = 4;


    //--------------------------------------------------------------------
    private final BettingRound round;
    private final PlayerState  players[];
    private final int          nextToAct;
    private final int          remainingRoundBets;
    private final int          latestRoundStaker;
    private final Money        stakes;
    private final HandState    startOfRound;


    //--------------------------------------------------------------------
    // expects blind actions
    public HandState(List<PlayerHandle> clockwiseDealerLast)
    {
        players = new PlayerState[ clockwiseDealerLast.size() ];
        for (int i = 0; i < players.length; i++)
        {
            players[i] = addInitPlayerState(clockwiseDealerLast, i);
        }

        round              = BettingRound.PREFLOP;
        nextToAct          = 0;
        remainingRoundBets = BETS_PER_ROUND - 1; // -1 for upcoming BB
        latestRoundStaker  = -1;
        stakes             = Money.ZERO;
        startOfRound       = this;
    }
    private PlayerState addInitPlayerState(
            List<PlayerHandle> clockwiseDealerLast,
            int                playerIndex)
    {
        PlayerHandle player = clockwiseDealerLast.get( playerIndex );
        return new PlayerState(
                    false, false, /*true,*/ Money.ZERO, player);
    }

    // automatically posts blinds
    public static HandState autoBlindInstance(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return new HandState(clockwiseDealerLast)
                    .advanceBlind( RealAction.SMALL_BLIND )
                    .advanceBlind( RealAction.BIG_BLIND );
    }

    // copy constructor
    private HandState(BettingRound copyRound,
                      PlayerState  copyPlayers[],
                      int          copyNextToAct,
                      int          copyRemainingRoundBets,
                      int          copyLatestRoundStaker,
                      Money        copyStakes,
                      HandState    copyStartOfRound)
    {
        round              = copyRound;
        players            = copyPlayers;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
        stakes             = copyStakes;
        startOfRound       = (copyStartOfRound == null)
                              ? this : copyStartOfRound;
    }


    //--------------------------------------------------------------------
    public HandState advance(PlayerHandle player, RealAction act)
    {
        validateNextAction(player, act);
        return act.isBlind()
                ? advanceBlind(act)
                : advanceVoluntary(act);
    }
    public HandState advance(RealAction act)
    {
        return advance(nextToAct().handle(), act);
    }


    //--------------------------------------------------------------------
    private HandState advanceVoluntary(RealAction act)
    {
        BettingRound nextRound  = nextBettingRound(act);
        boolean      roundEnder = (round != nextRound);
        boolean      betRaise   = act.isBetRaise();

        PlayerState
              nextPlayers[]     = nextPlayers(act);
        int   nextNextToAct     = nextNextToAct(nextPlayers, roundEnder);
        int   nextRemainingBets = nextRemainingBets(roundEnder, betRaise);
        Money nextStakes        = nextStakes(nextPlayers, betRaise);
        int   nextRoundStaker   =
                nextLatestRoundStaker(act, roundEnder, betRaise);

        return new HandState(nextRound,
                             nextPlayers,
                             nextNextToAct,
                             nextRemainingBets,
                             nextRoundStaker,
                             nextStakes,
                             roundEnder ? null : startOfRound);
    }

    private PlayerState[] nextPlayers(RealAction act)
    {
        PlayerState nextPlayers[] = players.clone();
        nextPlayers[nextToAct] = nextPlayers[nextToAct]
                                    .advance(act, stakes, betSize());
        return nextPlayers;
    }

    private Money nextStakes(PlayerState[] nextPlayers, boolean betRaise)
    {
        return (betRaise)
               ? nextPlayers[nextToAct].commitment()
               : stakes;
    }

    private int nextNextToAct(
            PlayerState[] nextPlayers, boolean roundEnder)
    {
        return roundEnder
                ? nextActiveAfter(nextPlayers, players.length - 1)
                : nextActiveAfter(nextPlayers, nextToAct);
    }

    private BettingRound nextBettingRound(RealAction act)
    {
        return nextActionCritical()
                ? nextToActCanRaise()
                    ? (act.toSimpleAction() == SimpleAction.RAISE)
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
            RealAction act, boolean roundEnder, boolean betRaise)
    {
        return roundEnder
                ? -1
                : (betRaise
                   ? nextToAct
                   : (latestRoundStaker == -1 && // when all players check
                        !act.isFold())
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
    private HandState advanceBlind(RealAction act)
    {
        boolean isSmall = act.isSmallBlind();

        if ( isSmall && !stakes.equals( Money.ZERO ))
            throw new HoldemRuleBreach("Small Blind already in.");
        if (!isSmall && stakes.compareTo( Money.BIG_BLIND ) >= 0)
            throw new HoldemRuleBreach("Big Blind already in.");

        Money       betSize       = Money.blind(isSmall);
        PlayerState nextPlayers[] = players.clone();
        nextPlayers[nextToAct]    =
                nextPlayers[nextToAct].advanceBlind(act, betSize);

        int nextNextToAct   = nextActiveAfter(nextPlayers, nextToAct);
        int nextRoundStaker =
                act == RealAction.BIG_BLIND_ALL_IN
                ? nextToAct : latestRoundStaker;
        
        return new HandState(round,
                             nextPlayers,
                             nextNextToAct,
                             remainingRoundBets,
                             nextRoundStaker,
                             betSize,
//                             nextHandId,
                             startOfRound);
    }


    //--------------------------------------------------------------------
    // assert ! quitter.equals( nextToAct().handle() )
    public HandState advanceQuitter(
            PlayerHandle quitter, List<Event> events)
    {
        int index = indexOf(quitter);
        if (players[ index ].isFolded()) return this;

        PlayerState nextPlayers[] = players.clone();
        nextPlayers[ index ] = nextPlayers[ index ].fold();

        int perlimStaker   =
                roundStakerAfterQuit(nextPlayers, index, events);
        boolean roundEnder =
                perlimStaker == nextStakingUnfoldedAfter(
                                    nextPlayers, index(nextToAct - 1));

        int nextRoundStaker =
                roundEnder
                ? -1 : perlimStaker;

        BettingRound nextRound = roundEnder ? round.next() : round;
        int nextNextToAct =
                roundEnder
                ? nextActiveAfter(nextPlayers, players.length - 1)
                : nextActiveAfter(nextPlayers, index(nextToAct - 1));
        
        return new HandState(nextRound,
                             nextPlayers,
                             nextNextToAct,
                             nextRemainingBets(roundEnder, false),
                             nextRoundStaker,
                             stakes,
                             roundEnder ? null : startOfRound);
    }

    private int roundStakerAfterQuit(
            PlayerState pStates[],
            int         index,
            List<Event> events)
    {
        if (latestRoundStaker != index) return latestRoundStaker;

        PlayerHandle stakerHandle = pStates[index].handle();
        for (int i = events.size() - 1; i < events.size(); i++)
        {
            Event e = events.get(i);
            if (e.getPlayer().equals( stakerHandle )) break;

            RealAction act = e.getAction();
            if (act.isCheckCall() || act.isBetRaise())
            {
                return indexOf( e.getPlayer() );
            }
        }
        return -1;
    }


    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return atShowdown()    ||
               nextToAct == -1 ||
               nextToActIsLastActivePlayer() && nextToActCanCheck() ||
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

    public Money betSize()
    {
        return isSmallBet() ? Money.SMALL_BET : Money.BIG_BET;
    }
    private boolean isSmallBet()
    {
        return round == BettingRound.PREFLOP ||
               round == BettingRound.FLOP;
    }

    public RealAction toRealAction(EasyAction easyAction)
    {
        return easyAction.toRealAction(nextToActCanCheck(),
                                       nextToActCanRaise());
    }
    private boolean nextToActCanCheck()
    {
        return stakes.equals( nextToAct().commitment() );
    }
    public boolean nextToActCanRaise()
    {
        return remainingRoundBets > 0;
    }


    //--------------------------------------------------------------------
    // these functions are provided as convenience,
    //  they are not actually used by state tracking.

    public BettingRound round()
    {
        return round;
    }

    public PlayerState[] players()
    {
        return players;
    }

    public int numActivePlayers()
    {
        int count = 0;
        for (PlayerState state : players)
            if (state.isActive()) count++;
        return count;
    }

    public List<PlayerState> unfolded()
    {
        List<PlayerState> condenters = new ArrayList<PlayerState>();

        int firstUnfolded = nextUnfoldedAfter( nextToAct - 1 );
        int cursor        = firstUnfolded;
        do
        {
            condenters.add( players[cursor] );
            cursor = nextUnfoldedAfter(cursor);
        }
        while (cursor != firstUnfolded);

        return condenters;
    }

    public Money pot()
    {
        Money pot = Money.ZERO;
        for (PlayerState player : players)
            pot = pot.plus( player.commitment() );
        return pot;
    }

    public Money stakes()
    {
        return stakes;
    }

    public int remainingBetsInRound()
    {
        return remainingRoundBets;
    }

    public Money toCall()
    {
        return stakes.minus( nextToAct().commitment() );
    }
    public int betsToCall()
    {
        return toCall().bets( isSmallBet() );
    }

    public double position()
    {
        return (double) (nextToAct + 1) / players.length;
    }
    public double activePosition()
    {
        int activePosition = 0;
        for (int i = 0; i <= nextToAct; i++)
        {
            if (players[i].isActive()) activePosition++;
        }
        return (double) activePosition / numActivePlayers();
    }


    //--------------------------------------------------------------------
    private int index(int fromIndex)
    {
        return fromIndex < 0
                ? fromIndex + players.length
                : fromIndex % players.length;
    }
    private int indexOf(PlayerHandle player)
    {
        for (int i = 0; i < players.length; i++)
        {
            if (players[ i ].handle().equals( player )) return i;
        }
        return -1;
    }

    private int nextActiveAfter(int playerIndex)
    {
        return nextActiveAfter( players, playerIndex );
    }
    private int nextActiveAfter(PlayerState[] states, int playerIndex)
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
        return nextStakingUnfoldedAfter(players, playerIndex);
    }
    private int nextStakingUnfoldedAfter(
            PlayerState[] pStates, int playerIndex)
    {
        int index = nextUnfoldedAfter(pStates, playerIndex);
        while (startOfRound.players[ index ].isAllIn() ||
                players[ index ].isAllIn() &&
               !players[ index ].commitment().equals( stakes ))
        {
            index = nextUnfoldedAfter(index);
        }
        return index;
    }

    private int nextUnfoldedAfter(int playerIndex)
    {
        return nextUnfoldedAfter(players, playerIndex);
    }
    private int nextUnfoldedAfter(
            PlayerState pStates[],
            int         playerIndex)
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

    public PlayerState nextToAct()
    {
        return players[ nextToAct ];
    }

    public int nextToActIndex()
    {
        return nextToAct;
    }


    //--------------------------------------------------------------------
    private void validateNextAction(
            PlayerHandle player, RealAction act)
    {
        if (atEndOfHand())
            throw new HoldemRuleBreach("the hand is already done");

        if (! nextToAct().handle().equals( player ))
            throw new HoldemRuleBreach(
                        "expected " + nextToAct().handle() + " not " +
                                      player);

        if (remainingRoundBets == 0 &&
                act.toSimpleAction() == SimpleAction.RAISE)
            throw new HoldemRuleBreach("round betting cap exceeded");
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return nextToAct() + ", " + round;
    }

//    public boolean handsEqual(HandState with)
//    {
//        return with != null &&
//               handId == with.handId;
//    }
}
