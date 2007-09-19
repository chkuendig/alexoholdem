package ao.holdem.history.state;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.RealAction;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.PlayerHandle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 *
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
//    private final int          smallBlind;
//    private final int          bigBlind;
    private final Money        stakes;


    //--------------------------------------------------------------------
    public HoldemState(List<PlayerHandle> clockwiseDealerLast,
                       int                smallBlindIndex,
                       int                bigBlindIndex)
    {
        players = new PlayerState[ clockwiseDealerLast.size() ];
        for (int i = 0; i < players.length; i++)
        {
            players[i] = addPlayerState(clockwiseDealerLast, i,
                                        smallBlindIndex,
                                        bigBlindIndex);
        }

        round              = BettingRound.PREFLOP;
        nextToAct          = nextActiveAfter(bigBlindIndex);
        remainingRoundBets = BETS_PER_ROUND
                             - ((bigBlindIndex >= 0) ? 1 : 0);
        latestRoundStaker  = -1;
//        smallBlind         = smallBlindIndex;
//        bigBlind           = bigBlindIndex;

        stakes = players[ bigBlindIndex ].commitment().plus(
                    (smallBlindIndex == -1)
                     ? Money.ZERO
                     : players[ smallBlindIndex ].commitment());
    }

    public HoldemState(List<PlayerHandle> clockwiseDealerLast)
    {
        this(clockwiseDealerLast,
             smallBlind(clockwiseDealerLast),
             bigBlind(  clockwiseDealerLast));
    }
    public HoldemState(List<PlayerHandle> clockwiseDealerLast,
                       PlayerHandle       smallBlind,
                       PlayerHandle       bigBlind)
    {
        this(clockwiseDealerLast,
             clockwiseDealerLast.indexOf(smallBlind),
             clockwiseDealerLast.indexOf(bigBlind));
    }


    //--------------------------------------------------------------------
    private HoldemState(BettingRound copyRound,
                        PlayerState  copyPlayers[],
                        int          copyNextToAct,
                        int          copyRemainingRoundBets,
                        int          copyLatestRoundStaker,
                        Money        copyStakes)//,
//                        int          copySmallBlind,
//                        int          copyBigBlind)
    {
        round              = copyRound;
        players            = copyPlayers;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
//        smallBlind         = copySmallBlind;
//        bigBlind           = copyBigBlind;
        stakes             = copyStakes;
    }


    //--------------------------------------------------------------------
    private PlayerState addPlayerState(
            List<PlayerHandle> clockwiseDealerLast,
            int                playerIndex,
            int                smallBlindIndex,
            int                bigBlindIndex)
    {
        PlayerHandle player = clockwiseDealerLast.get( playerIndex );
        Money commitment = (playerIndex == smallBlindIndex)
                            ? Money.SMALL_BLIND
                            : (playerIndex == bigBlindIndex)
                               ? Money.BIG_BLIND
                               : Money.ZERO;
        return new PlayerState(
                        true, false, true,
                        commitment,
                        player);
    }

    //--------------------------------------------------------------------
    private static int smallBlind(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return (clockwiseDealerLast.size() > 2) ? 0 : 1;
    }
    private static int bigBlind(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return (clockwiseDealerLast.size() > 2) ? 1 : 0;
    }


    //--------------------------------------------------------------------
    public HoldemState advance(PlayerHandle player, RealAction act)
    {
        validateNextAction(player, act);
//        if (act.isBlind()) return advanceBlind(player, act);

        BettingRound nextRound = nextBettingRound(act);

        PlayerState nextPlayers[] = players.clone();
        nextPlayers[nextToAct] =
                nextPlayers[nextToAct].advance(
                        act,
                        players[prevActive(nextToAct)],
                        betSize());

        boolean isRoundEnder  = (round != nextRound);
        int     nextNextToAct =
                    isRoundEnder
                    ? nextActiveAfter(nextPlayers, players.length - 1)
                    : nextActiveAfter(nextPlayers, nextToAct);

        boolean isBetRaise = (act.toTakenAction() == TakenAction.RAISE);
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
                        act.toTakenAction() != TakenAction.FOLD)
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
                               stakes);//,
//                               smallBlind,
//                               bigBlind);
    }

//    private HoldemState advanceBlind(PlayerHandle player, RealAction act)
//    {
//        assert act.isBlind();
//        boolean isSmall = act.asSmallBlind().equals( act );
//        if (isSmall && smallBlind != indexOf(player))
//            throw new HoldemRuleBreach("wrang blinds");
//        if (!isSmall && bigBlind != indexOf(player))
//            throw new HoldemRuleBreach("wrang blinds");
//        return this;
//    }

    public HoldemState advance(RealAction act)
    {
        return advance(nextToAct().handle(), act);
    }


    //--------------------------------------------------------------------
    private BettingRound nextBettingRound(RealAction act)
    {
        return nextActionCritical()
                ? nextToActCanRaise()
                    ? (act.toTakenAction() == TakenAction.RAISE)
                        ? round
                        : round.next()
                    : round.next()
                : round;
    }

    // if it determines weather or not the Round is over.
    private boolean nextActionCritical()
    {
        // need to add detection for when everybody so far
        //  has checked
        return latestRoundStaker == nextActiveAfter( nextToAct );
    }

    public BettingRound round()
    {
        return round;
    }

    public PlayerState[] players()
    {
        return players;
    }


    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return atShowdown() ||
               oneActivePlayerLeft() && !nextToActCanCheck();
    }

    private boolean oneActivePlayerLeft()
    {
        return nextToAct == nextActiveAfter(nextToAct);
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

        int firstUnfolded = nextUnfoldedAtOrAfter( nextToAct );
        int cursor        = firstUnfolded;
        do
        {
            condenters.add( players[cursor] );
            cursor = nextUnfoldedAtOrAfter(cursor + 1);
        }
        while (cursor != firstUnfolded);

        return condenters;
    }

    public boolean nextToActCanCheck()
    {
        return stakes.equals( nextToAct().commitment() );
    }

    public boolean nextToActCanRaise()
    {
        return remainingRoundBets > 0;
    }

    private Money betSize()
    {
        return round == BettingRound.PREFLOP ||
               round == BettingRound.FLOP
                ? Money.SMALL_BET : Money.BIG_BET;
    }


    //--------------------------------------------------------------------
    private int prevActive(int before)
    {
        for (int cursor = before - 1;; cursor--)
        {
            int index = (cursor >= 0)
                        ? cursor
                        : cursor + players.length;
            if (players[ index ].isActive())
            {
                return index;
            }
        }
    }
    private int nextActiveAfter(int playerIndex)
    {
        return nextActiveAfter( players, playerIndex );
    }
    private int nextActiveAfter(PlayerState[] states, int playerIndex)
    {
        for (int cursor = playerIndex + 1;; cursor++)
        {
            int index = cursor % states.length;
            if (states[ index ].isActive())
            {
                return index;
            }
        }
    }

    private int nextUnfoldedAtOrAfter(int playerIndex)
    {
        for (int cursor = playerIndex;; cursor++)
        {
            int index = cursor % players.length;
            if (! players[ index ].isFolded())
            {
                return index;
            }
        }
    }

    private PlayerState prevPlayer()
    {
        return players[ prevActive(nextToAct) ];
    }
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

        if (act.isBlind())
            throw new HoldemRuleBreach("blinds not supported");

        if (! nextToAct().handle().equals( player ))
            throw new HoldemRuleBreach(
                        "expected " + nextToAct().handle() + " not " +
                                      player);

        if (remainingRoundBets == 0 &&
                act.toTakenAction() == TakenAction.RAISE)
            throw new HoldemRuleBreach("round betting cap exceeded");
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return nextToAct() + ", " + round;
    }
}
