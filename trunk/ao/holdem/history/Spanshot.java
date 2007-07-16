package ao.holdem.history;

import ao.holdem.def.model.Money;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * History Snapshot.
 */
public class Spanshot
{
    //--------------------------------------------------------------------
    private List<PlayerHandle> players       = new ArrayList<PlayerHandle>();
    private List<PlayerHandle> activePlayers = new ArrayList<PlayerHandle>();
    private PlayerHandle       nextToAct;
    private BettingRound       round;

    private Hole         hole;
    private Community    community;

    private boolean[]    folded;
    private Money        stakes;
    private Money        nextToActPot;

    private int          remainingBets;
    private PlayerHandle smallBlind;
    private PlayerHandle bigBlind;

    private Map<PlayerHandle, Money>             commitment;
    private Map<PlayerHandle, List<TakenAction>> actions;


    //--------------------------------------------------------------------
    public Spanshot(List<PlayerHandle> players,
                    List<Event>        events,
                    Hole               nextToActHole,
                    Community          community)
    {
        this.players.addAll( players );

        this.hole      = nextToActHole;
        this.community = community;
    }



    //--------------------------------------------------------------------
    public List<PlayerHandle> players()
    {
        return players;
    }

    public List<PlayerHandle> activePlayers()
    {
        return activePlayers;
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
    public Hole hole()
    {
        return hole;
    }

    public Community community()
    {
        return community;
    }

    public boolean canRaise()
    {
        return remainingBets > 0;
    }

    public Money toCall(PlayerHandle player)
    {
        return commitment(player).minus( stakes );
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
        return nextToActPot;
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
