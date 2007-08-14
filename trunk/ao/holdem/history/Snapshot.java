package ao.holdem.history;

import ao.holdem.def.model.Money;
import ao.holdem.def.state.domain.BettingRound;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * History Snapshot.
 */
public class Snapshot
{
    //--------------------------------------------------------------------
    private final static Logger log = Logger.getLogger(Snapshot.class);
    static
    {
        log.setLevel(Level.WARN);
    }


    //--------------------------------------------------------------------
    private List<PlayerHandle> players       =
            new ArrayList<PlayerHandle>();
    private Set<PlayerHandle>  activePlayers =
            new LinkedHashSet<PlayerHandle>();

    private PlayerHandle       nextToAct;
    private BettingRound       round;

    private Money        stakes;
    private Money        pot;

    private int          numCalls;
    private int          remainingBets;
    private PlayerHandle smallBlind;
    private PlayerHandle bigBlind;

    private Map<BettingRound, Money>             roundCommitment;
    private Map<PlayerHandle, Money>             commitment;
//    private Map<PlayerHandle, List<TakenAction>> actions;

    private PlayerHandle latestRoundStaker = null;
    private int          unactedThisRound;


    //--------------------------------------------------------------------
    public Snapshot(List<PlayerHandle> players)
    {
        this.players.addAll( players );
        activePlayers.addAll( players );

        commitment = new HashMap<PlayerHandle, Money>();
        roundCommitment = new HashMap<BettingRound, Money>();
//        actions = new HashMap<PlayerHandle, List<TakenAction>>();
        for (PlayerHandle player : players)
        {
//            actions.put(player, new ArrayList<TakenAction>());
            commitment.put(player, Money.ZERO);
        }

        designateBlinds(players);

        remainingBets = 3;
        commitment.put(smallBlind, Money.SMALL_BLIND);
        commitment.put(bigBlind,   Money.BIG_BLIND  );
        stakes    = Money.BIG_BLIND;
        pot       = Money.SMALL_BLIND.plus( Money.BIG_BLIND );
        round     = BettingRound.PREFLOP;
        nextToAct = nextActive( bigBlind );

        roundCommitment.put(round, Money.BIG_BLIND);
        unactedThisRound = players.size();
    }

    private void designateBlinds(List<PlayerHandle> players)
    {
        // this makes logical sense, however it is NOT how the
        //  irc database records things.
        //  xxx todo: check which way pokerstars does it!!
//        if (isHeadsUp())
//        {
//            smallBlind = players.get(1); // dealer
//            bigBlind   = players.get(0);
//        }
//        else
//        {
//            smallBlind = players.get(0);
//            bigBlind   = players.get(1);
//        }
        
        smallBlind = players.get(0);
        bigBlind   = players.get(1);
    }

    //--------------------------------------------------------------------
    public boolean populate(List<Event> events)
    {
        for (Event event : events)
        {
            try
            {
                addNextEvent( event );
            }
            catch (Error e)
            {
                log.warn(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public Snapshot addNextEvent(Event event)
    {
        if (isRoundDone())
        {
            advanceRound();
        }

        if (! nextToAct.equals( event.getPlayer() ))
        {
            throw new Error("expected " + nextToAct +
                            " on "      + event);
        }
        nextToAct = nextActive( nextToAct );

//        actions.get( event.getPlayer() ).add( event.getAction() );
        switch (event.getAction())
        {
            case FOLD:
                activePlayers.remove( event.getPlayer() );
                break;

            case RAISE:
                if (remainingBets == 0)
                {
                    throw new Error("no more than 4 raises!");
                }

                stakes = stakes.plus( betSize() );
                commitment.put(event.getPlayer(),
                               stakes);
                roundCommitment.put(round, stakes);
                pot    = pot.plus( betSize() );
                latestRoundStaker = event.getPlayer();
                remainingBets--;
                break;

            case CALL:
                Money delta = stakes.minus(
                                commitment.get(event.getPlayer()));
                pot = pot.plus(delta);
                commitment.put(event.getPlayer(), stakes);
                roundCommitment.put(round, stakes);
                defineLatestRoundStaker( event.getPlayer() );
                numCalls++;
                break;

//            case CHECK:
//                defineLatestRoundStaker( event.getPlayer() );
//                break;
        }
        if (unactedThisRound > 0) unactedThisRound--;

        return this;
    }

    private boolean advanceRound()
    {
        if (round == BettingRound.RIVER)
        {
            throw new Error("cannot advance after RIVER");
        }

        remainingBets = 4;
        round = BettingRound.values()[ round.ordinal() + 1 ];
        nextToAct = firstToActPostFlop();
        latestRoundStaker = null;
        unactedThisRound  = activePlayers.size();

        return true;
    }

    private void defineLatestRoundStaker(PlayerHandle player)
    {
        if (latestRoundStaker == null)
        {
            latestRoundStaker = player;
        }
    }
    
    public Money betSize()
    {
        return isSmallBet()
                ? Money.SMALL_BET
                : Money.BIG_BET;
    }
    public boolean isSmallBet()
    {
        return round == BettingRound.PREFLOP ||
               round == BettingRound.FLOP;
    }
    
    public boolean isRoundDone()
    {
        return blindNextToAct().equals( latestRoundStaker );
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

    public List<PlayerHandle> opponents()
    {
        List<PlayerHandle> opps = players();
        opps.remove( nextToAct );
        return opps;
    }
    public List<PlayerHandle> activeOpponents()
    {
        List<PlayerHandle> opps = activePlayers();
        opps.remove( nextToAct );
        return opps;
    }

    public boolean isActive(PlayerHandle handle)
    {
        return activePlayers.contains( handle );
    }

    public PlayerHandle blindNextToAct()
    {
        return nextToAct;
    }

    public PlayerHandle nextToAct()
    {
        return isRoundDone()
                ? firstToActPostFlop()
                : nextToAct;
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
    public PlayerHandle firstToActPostFlop()
    {
        for (PlayerHandle player : players)
        {
            if (activePlayers.contains(player))
            {
                return player;
            }
        }
        return null;
    }

    public int unactedThisRound()
    {
        return unactedThisRound;
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

//    public PlayerHandle underTheGun()
//    {
//        return null;
//    }
//
//    public PlayerHandle cutoff()
//    {
//        return null;
//    }


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
        return toCall( blindNextToAct() );
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
        return commitment( blindNextToAct() );
    }

    public Money latestRoundCommitment(PlayerHandle player)
    {
        Money prevRoundCommitment = Money.ZERO;
        if (round != BettingRound.PREFLOP)
        {
            prevRoundCommitment =
                    roundCommitment.get(round.previous());
        }
        return commitment(player).minus( prevRoundCommitment );
    }
    public Money latestRoundCommitment()
    {
        return latestRoundCommitment( blindNextToAct() );
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

    public int numCalls()
    {
        return numCalls;
    }

//    public List<TakenAction> actions(PlayerHandle by)
//    {
//        return actions.get( by );
//    }


    //--------------------------------------------------------------------
    public Snapshot prototype()
    {
        Snapshot proto = new Snapshot(players);

        proto.activePlayers.addAll( activePlayers );
        proto.nextToAct = nextToAct;
        proto.round     = round;

        proto.stakes = stakes;
        proto.pot    = pot;

        proto.numCalls      = numCalls;
        proto.remainingBets = remainingBets;
        proto.smallBlind    = smallBlind;
        proto.bigBlind      = bigBlind;

        proto.roundCommitment.putAll( roundCommitment );
        proto.commitment.putAll( commitment );
        proto.latestRoundStaker = latestRoundStaker;
        proto.unactedThisRound  = unactedThisRound;

        return proto;
    }
}

