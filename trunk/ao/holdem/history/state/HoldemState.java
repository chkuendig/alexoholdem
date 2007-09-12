package ao.holdem.history.state;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.RealAction;
import ao.holdem.history.PlayerHandle;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class HoldemState
{
    //--------------------------------------------------------------------
    private final BettingRound round;
    private final PlayerState  players[];
    private final int          nextToAct;


    //--------------------------------------------------------------------
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
        round = BettingRound.PREFLOP;

        players = new PlayerState[ clockwiseDealerLast.size() ];
        for (int i = 0; i < players.length; i++)
        {
            players[i] = initStateOf(clockwiseDealerLast.get(i),
                                     smallBlind, bigBlind);
        }

        nextToAct = nextActive(1);
    }

    //--------------------------------------------------------------------
    private PlayerState initStateOf(
            PlayerHandle player,
            PlayerHandle smallBlind,
            PlayerHandle bigBlind)
    {
        Money   commitment   = Money.ZERO;
        boolean isSmallBlind = false;
        boolean isBigBlind   = false;

        if (player.equals( smallBlind ))
        {
            isSmallBlind = true;
            isBigBlind   = false;
            commitment   = Money.SMALL_BLIND;
        }
        else if (player.equals( bigBlind ))
        {
            isBigBlind = true;
            commitment = Money.BIG_BLIND;
        }

        return new PlayerState(
                        true, false, true,
                        isSmallBlind, isBigBlind,
                        commitment,
                        player);
    }

    //--------------------------------------------------------------------
    private static PlayerHandle smallBlind(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return clockwiseDealerLast.get(0);
    }
    private static PlayerHandle bigBlind(
            List<PlayerHandle> clockwiseDealerLast)
    {
        return clockwiseDealerLast.get(1);
    }


    //--------------------------------------------------------------------
    public HoldemState advance(PlayerHandle player, RealAction act)
    {
        assert !atEndOfHand();
        assert nextToAct().handle().equals( player );

        BettingRound nextRound     = nextBettingRound(act);
        int          nextNextToAct = 0;


        return null;
    }

    private BettingRound nextBettingRound(RealAction act)
    {
        return raiseToContinueRound()
                ? (act == RealAction.RAISE ||
                   act == RealAction.RAISE_ALL_IN)
                    ? round == BettingRound.RIVER
                       ? null
                       : round.next()
                    : round
                : round;
    }


    //--------------------------------------------------------------------
    private int nextActive(int after)
    {
        for (int cursor = after + 1;; cursor++)
        {
            int index = cursor % players.length;
            if (players[ index ].isActive())
            {
                return index;
            }
        }
    }


    //--------------------------------------------------------------------
    public boolean atEndOfHand()
    {
        return false;
    }

    public Collection<PlayerHandle> showdownBetween()
    {
        return null;
    }

    public PlayerState nextToAct()
    {
        return null;
    }

    public boolean nextToActCanCheck()
    {
        return false;
    }

    public boolean nextToActCanRaise()
    {
        return false;
    }

    public boolean raiseToContinueRound()
    {
        return false;
    }
}
