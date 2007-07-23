package ao.holdem.history;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;

import java.util.*;

/**
 * History Snapshot.
 */
public class Snapshot
{
    //--------------------------------------------------------------------
    private List<PlayerHandle> players       = new ArrayList<PlayerHandle>();
    private Set<PlayerHandle>  activePlayers = new HashSet<PlayerHandle>();
    private PlayerHandle       nextToAct;
    private BettingRound       round;

    private Money        stakes;
    private Money        pot;

    private int          remainingBets;
    private PlayerHandle smallBlind;
    private PlayerHandle bigBlind;

    private Map<PlayerHandle, Money>             commitment;
    private Map<PlayerHandle, List<TakenAction>> actions;

    private PlayerHandle latestRoundStaker = null;


    //--------------------------------------------------------------------
    public Snapshot(List<PlayerHandle> players,
                    List<Event>        events)
    {
        this.players.addAll( players );
        activePlayers.addAll( players );

        commitment = new HashMap<PlayerHandle, Money>();
        actions = new HashMap<PlayerHandle, List<TakenAction>>();
        for (PlayerHandle player : players)
        {
            actions.put(player, new ArrayList<TakenAction>());
            commitment.put(player, Money.ZERO);
        }

        if (isHeadsUp())
        {
            smallBlind = players.get(1); // dealer
            bigBlind   = players.get(0);
        }
        else
        {
            smallBlind = players.get(0);
            bigBlind   = players.get(1);
        }
        remainingBets = 3;
        commitment.put(smallBlind, Money.SMALL_BLIND);
        commitment.put(bigBlind,   Money.BIG_BLIND  );
        stakes    = Money.BIG_BLIND;
        pot       = Money.SMALL_BLIND.plus( Money.BIG_BLIND );
        round     = BettingRound.PREFLOP;
        nextToAct = nextActive( bigBlind );

        for (Event event : events)
        {
            addNextEvent( event );
        }
    }


    //--------------------------------------------------------------------
    public Snapshot addNextEvent(Event event)
    {
        assert nextToAct.equals( event.getPlayer() );
        if (isRoundDone())
        {
            advanceRound();
        }
        nextToAct = nextActive( nextToAct );

        actions.get( event.getPlayer() ).add( event.getAction() );
        switch (event.getAction())
        {
            case FOLD:
                activePlayers.remove( event.getPlayer() );
                break;

            case RAISE:
                stakes = stakes.plus( betSize() );
                commitment.put(event.getPlayer(),
                               stakes);
                pot    = pot.plus( betSize() );
                latestRoundStaker = event.getPlayer();
                remainingBets--;
                break;

            case CALL:
                Money delta = stakes.minus(
                                commitment.get(event.getPlayer()));
                pot = pot.plus(delta);
                commitment.put(event.getPlayer(), stakes);
                defineLatestRoundStaker( event.getPlayer() );
                break;

            case CHECK:
                defineLatestRoundStaker( event.getPlayer() );
                break;
        }

        return this;
    }

    private void advanceRound()
    {
        assert round != BettingRound.RIVER;

        remainingBets = 4;
        round = BettingRound.values()[ round.ordinal() + 1 ];
    }

    private void defineLatestRoundStaker(PlayerHandle player)
    {
        if (latestRoundStaker == null)
        {
            latestRoundStaker = player;
        }
    }
    
    private Money betSize()
    {
        return round == BettingRound.PREFLOP ||
               round == BettingRound.FLOP
                ? Money.SMALL_BET
                : Money.BIG_BET;
    }
    
    public boolean isRoundDone()
    {
        return remainingBets == 0 ||
               nextToAct().equals( latestRoundStaker );
    }

    public boolean isGameOver()
    {
        return activePlayers.size() == 1 ||
                round == BettingRound.RIVER &&
                isRoundDone();
    }


    //--------------------------------------------------------------------
    public List<PlayerHandle> players()
    {
        return new ArrayList<PlayerHandle>( players );
    }

    public List<PlayerHandle> activePlayers()
    {
        return new ArrayList<PlayerHandle>( activePlayers );
    }

    public boolean isActive(PlayerHandle handle)
    {
        return activePlayers.contains( handle );
    }

    public PlayerHandle nextToAct()
    {
        return nextToAct;
    }

    public boolean isHeadsUp()
    {
        return players.size() == 2;
    }

    public PlayerHandle nextActive(PlayerHandle after)
    {
        int position = players.indexOf( after );

        for (int cursor  = position + 1;
                 cursor != position;
                 cursor++)
        {
            int          index  = cursor % players.size();
            PlayerHandle player = players.get(index);
            if (activePlayers.contains( player ))
            {
                return player;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    public PlayerHandle smallBlind()
    {
        return smallBlind;
    }

    public PlayerHandle bigBlind()
    {
        return bigBlind;
    }

    public PlayerHandle button()
    {
        return players.get( players.size() - 1 );
    }

    public PlayerHandle underTheGun()
    {
        return null;
    }

    public PlayerHandle cutoff()
    {
        return null;
    }


    //--------------------------------------------------------------------
    public boolean canRaise()
    {
        return remainingBets > 0;
    }

    public boolean canCheck()
    {
        return toCall().equals( Money.ZERO );
    }

    public Money toCall(PlayerHandle player)
    {
        return stakes.minus( commitment(player) );
    }
    public Money toCall()
    {
        return toCall( nextToAct() );
    }

//    public Money bankRoll(PlayerHandle player)
//    {
//        return null;
//    }

    public BettingRound round()
    {
        return round;
    }

    public Money pot()
    {
        return pot;
    }

    public Money stakes()
    {
        return stakes;
    }

    public Money commitment(PlayerHandle player)
    {
        return commitment.get( player );
    }
    public Money commitment()
    {
        return commitment( nextToAct() );
    }


    //--------------------------------------------------------------------
    public int raises()
    {
        return (4 - remainingBets);
    }
    public int remainingRaises()
    {
        return remainingBets;
    }

    public List<TakenAction> actions(PlayerHandle by)
    {
        return actions.get( by );
    }
}
