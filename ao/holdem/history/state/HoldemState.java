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
        nextToAct          = nextActive(bigBlindIndex);
        remainingRoundBets = BETS_PER_ROUND
                             - ((bigBlindIndex >= 0) ? 1 : 0);
        latestRoundStaker  = -1;
//        smallBlind         = smallBlindIndex;
//        bigBlind           = bigBlindIndex;
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
                        int          copyLatestRoundStaker)
    {
        round              = copyRound;
        players            = copyPlayers;
        nextToAct          = copyNextToAct;
        remainingRoundBets = copyRemainingRoundBets;
        latestRoundStaker  = copyLatestRoundStaker;
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
                    ? nextActive(nextPlayers, players.length - 1)
                    : nextActive(nextPlayers, nextToAct);

        boolean isBetRaise = (act.toTakenAction() == TakenAction.RAISE);
        int nextRemainingBets = isRoundEnder
                                ? 4 // bets per round
                                : (isBetRaise
                                   ? remainingRoundBets - 1
                                   : remainingRoundBets);
        int nextLatestRoundStaker = isRoundEnder
                                    ? -1
                                    : (isBetRaise
                                       ? nextToAct
                                       : latestRoundStaker);

        return new HoldemState(nextRound,
                               nextPlayers,
                               nextNextToAct,
                               nextRemainingBets,
                               nextLatestRoundStaker);
    }

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
        return latestRoundStaker == nextActive( nextToAct );
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
        return atShowdown() || onePlayerLeft();
    }

    private boolean onePlayerLeft()
    {
        return nextToAct == nextActive(nextToAct);
    }

    private boolean atShowdown()
    {
        return round == null;
    }


    //--------------------------------------------------------------------
    public Collection<PlayerState> finalists()
    {
        if (!atEndOfHand()) return Collections.emptyList();

        Collection<PlayerState> condenters =
                new ArrayList<PlayerState>();
        if (onePlayerLeft())
        {
            condenters.add( players[nextToAct] );
        }
        else
        {
            for (int cursor  = nextActive( nextToAct );
                 cursor != nextToAct;
                 cursor  = nextActive(cursor))
            {
                condenters.add( players[cursor] );
            }
        }
        return condenters;
    }

    public boolean nextToActCanCheck()
    {
        return prevPlayer().commitment().equals(
                nextToAct().commitment());
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
//    private int indexOf(PlayerHandle player)
//    {
//        for (int i = 0; i < players.length; i++)
//        {
//            PlayerState state = players[i];
//            if (state.handle().equals(player)) return i;
//        }
//        return -1;
//    }
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
    private int nextActive(int after)
    {
        return nextActive( players, after );
    }
    private int nextActive(PlayerState[] states, int after)
    {
        for (int cursor = after + 1;; cursor++)
        {
            int index = cursor % states.length;
            if (states[ index ].isActive())
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


    //--------------------------------------------------------------------
    private void validateNextAction(
            PlayerHandle player, RealAction act)
    {
        if (act == RealAction.SMALL_BLIND ||
                act == RealAction.BIG_BLIND)
            throw new HoldemRuleBreach("blind acts not supported");

        if (atEndOfHand())
            throw new HoldemRuleBreach("the hand is already done");

        if (! nextToAct().handle().equals( player ))
            throw new HoldemRuleBreach(
                        "expected " + nextToAct().handle() + " not " +
                                      player);

        if (remainingRoundBets == 0 &&
                act.toTakenAction() == TakenAction.RAISE)
            throw new HoldemRuleBreach("round betting cap exceeded");
    }
}
