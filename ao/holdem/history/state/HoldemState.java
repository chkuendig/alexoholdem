package ao.holdem.history.state;

import ao.holdem.model.Money;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.act.RealAction;
import ao.holdem.def.state.domain.BettingRound;
import ao.persist.PlayerHandle;
import ao.state.PlayerState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * immutable.
 */
public class HoldemState
{
    //--------------------------------------------------------------------
    private static final int BETS_PER_ROUND = 4;


    //--------------------------------------------------------------------
    private final BettingRound round;
    private final PlayerState  players[];
    private final int          nextToAct;
    private final int          remainingRoundBets;
    private final int          latestRoundStaker;
    private final Money        stakes;


    //--------------------------------------------------------------------
    // expects blind actions
    public HoldemState(List<PlayerHandle> clockwiseDealerLast)
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
    }
    private PlayerState addInitPlayerState(
            List<PlayerHandle> clockwiseDealerLast,
            int                playerIndex)
    {
        PlayerHandle player = clockwiseDealerLast.get( playerIndex );
        return new PlayerState(false, false, true, Money.ZERO, player);
    }

    // automatically posts blinds
    public static HoldemState autoBlindInstance(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return new HoldemState(clockwiseDealerLast)
                    .advanceBlind( RealAction.SMALL_BLIND )
                    .advanceBlind( RealAction.BIG_BLIND );
    }

    // copy constructor
    private HoldemState(BettingRound copyRound,
                        PlayerState  copyPlayers[],
                        int          copyNextToAct,
                        int          copyRemainingRoundBets,
                        int          copyLatestRoundStaker,
                        Money        copyStakes)
    {
        round              = copyRound;
        players            = copyPlayers;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
        stakes             = copyStakes;
    }


    //--------------------------------------------------------------------
    public HoldemState advance(PlayerHandle player, RealAction act)
    {
        validateNextAction(player, act);
        return act.isBlind()
                ? advanceBlind(act)
                : advanceVoluntary(act);
    }
    public HoldemState advance(RealAction act)
    {
        return advance(nextToAct().handle(), act);
    }

    // allow for asynchronouse folds
    private HoldemState advanceVoluntary(RealAction act)
    {
        BettingRound nextRound = nextBettingRound(act);

        PlayerState nextPlayers[] = players.clone();
        nextPlayers[nextToAct] =
                nextPlayers[nextToAct].advance(act, stakes, betSize());

        boolean isRoundEnder  = (round != nextRound);
        int     nextNextToAct =
                    isRoundEnder
                    ? nextInAfter(nextPlayers, players.length - 1)
                    : nextInAfter(nextPlayers, nextToAct);

        boolean isBetRaise = (act.toTakenAction() == SimpleAction.RAISE);
        int nextRemainingBets = isRoundEnder
                                ? 4 // bets per round
                                : (isBetRaise
                                   ? remainingRoundBets - 1
                                   : remainingRoundBets);

        int nextLatestRoundStaker =
                isRoundEnder
                ? -1
                : (isBetRaise
                   ? nextToAct
                   : (latestRoundStaker == -1 && // when all players check
                        act.toTakenAction() != SimpleAction.FOLD)
                      ? nextToAct
                      : latestRoundStaker);

        Money nextStakes = (isBetRaise)
                            ? nextPlayers[nextToAct].commitment()
                            : stakes;

        return new HoldemState(nextRound,
                               nextPlayers,
                               nextNextToAct,
                               nextRemainingBets,
                               nextLatestRoundStaker,
                               nextStakes);
    }


    private HoldemState advanceBlind(RealAction act)
    {
        boolean isSmall = act.asSmallBlind().equals( act );

        if ( isSmall && !stakes.equals( Money.ZERO ))
            throw new HoldemRuleBreach("Small Blind already in.");
        if (!isSmall && stakes.compareTo( Money.BIG_BLIND ) >= 0)
            throw new HoldemRuleBreach("Big Blind already in.");

        Money       betSize       = Money.blind(isSmall);
        PlayerState nextPlayers[] = players.clone();
        nextPlayers[nextToAct]    =
                nextPlayers[nextToAct].advanceBlind(act, betSize);

        return new HoldemState(round,
                               nextPlayers,
                               index(nextToAct + 1),
                               remainingRoundBets,
                               latestRoundStaker,
                               betSize);
    }


    //--------------------------------------------------------------------
    private BettingRound nextBettingRound(RealAction act)
    {
        return nextActionCritical()
                ? nextToActCanRaise()
                    ? (act.toTakenAction() == SimpleAction.RAISE)
                        ? round
                        : round.next()
                    : round.next()
                : round;
    }

    // if it determines weather or not the Round is over.
    private boolean nextActionCritical()
    {
        return latestRoundStaker == nextUnfoldedAfter( nextToAct );
    }

    public BettingRound round()
    {
        return round;
    }

    public PlayerState[] players()
    {
        return players;
    }

    public int numPlayersIn()
    {
        int count = 0;
        for (PlayerState state : players)
            if (state.isIn()) count++;
        return count;
    }



    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return atShowdown() ||
               nextToActIsLastPlayerIn() && nextToActCanCheck();
    }

    private boolean nextToActIsLastPlayerIn()
    {
        return nextToAct == nextInAfter(nextToAct);
    }
//    private boolean oneUnfoldedPlayerLeft()
//    {
//        return nextToAct == nextUnfoldedAtOrAfter(nextToAct + 1);
//    }

    private boolean atShowdown()
    {
        return round == null;
    }


    //--------------------------------------------------------------------
    public Collection<PlayerState> finalists()
    {
        if (!atEndOfHand()) return Collections.emptyList();

        Collection<PlayerState> condenters = new ArrayList<PlayerState>();

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

    public boolean nextToActCanCheck()
    {
        return stakes.equals( nextToAct().commitment() );
    }
    public int betsToCall()
    {
        return stakes.minus( nextToAct().commitment() )
                        .bets( isSmallBet() );
    }
    public boolean nextToActCanRaise()
    {
        return remainingRoundBets > 0;
    }
    public int remainingBetsInRound()
    {
        return remainingRoundBets;
    }

    private Money betSize()
    {
        return isSmallBet() ? Money.SMALL_BET : Money.BIG_BET;
    }
    public boolean isSmallBet()
    {
        return round == BettingRound.PREFLOP ||
               round == BettingRound.FLOP;
    }


    //--------------------------------------------------------------------
    private int index(int fromIndex)
    {
        return fromIndex < 0
                ? fromIndex + players.length
                : fromIndex % players.length;
    }

    private int nextInAfter(int playerIndex)
    {
        return nextInAfter( players, playerIndex );
    }
    private int nextInAfter(PlayerState[] states, int playerIndex)
    {
        for (int i = 1; i <= states.length; i++)
        {
            int index = index(playerIndex + i);
            if (states[ index ].isIn())
            {
                return index;
            }
        }
        return -1;
    }

    private int nextUnfoldedAfter(int playerIndex)
    {
        for (int i = 1; i <= players.length; i++)
        {
            int index = index(playerIndex + i);
            if (! players[ index ].isFolded())
            {
                return index;
            }
        }
        return -1;
    }

//    private PlayerState prevPlayer()
//    {
//        return players[ prevActive(nextToAct) ];
//    }
    public PlayerState nextToAct()
    {
        return players[ nextToAct ];
    }

//    private int indexOf(PlayerHandle handle)
//    {
//        for (int i = 0; i < players.length; i++)
//        {
//            if (players[ i ].handle().equals( handle )) return i;
//        }
//        return -1;
//    }


    //--------------------------------------------------------------------
    private void validateNextAction(
            PlayerHandle player, RealAction act)
    {
        if (atEndOfHand())
            throw new HoldemRuleBreach("the hand is already done");

//        if (act.isBlind())
//            throw new HoldemRuleBreach("blinds not supported");

        if (! nextToAct().handle().equals( player ))
            throw new HoldemRuleBreach(
                        "expected " + nextToAct().handle() + " not " +
                                      player);

        if (remainingRoundBets == 0 &&
                act.toTakenAction() == SimpleAction.RAISE)
            throw new HoldemRuleBreach("round betting cap exceeded");
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return nextToAct() + ", " + round;
    }
}
