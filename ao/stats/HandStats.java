package ao.stats;

import ao.holdem.model.Community;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.CumulativeState;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.state.StateManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class HandStats implements CumulativeState
{
    //--------------------------------------------------------------------
    private Map<Serializable, PlayerStats> stats =
                    new HashMap<Serializable, PlayerStats>();
    private GenericStats common = new GenericStats();


    //--------------------------------------------------------------------
    public HandStats(StateManager state)
    {
        for (PlayerState player : state.head().players())
        {
            PlayerHandle handle = player.handle();
            stats.put( handle.getId(),
                       new PlayerStats(handle,
                                       state.cards().holeFor(handle)));
        }
        init( state.head() );
    }


    //--------------------------------------------------------------------
    public void init(HandState startOfHand)
    {
        common.init( startOfHand );
        for (PlayerStats stat : stats.values())
        {
            stat.init( startOfHand );
        }
    }


    //--------------------------------------------------------------------
    public void advance(PlayerState actor,
                        RealAction  act,
                        HandState   afterAct,
                        Community   community)
    {
        common.advance( actor, act, afterAct, community);
        for (PlayerStats stat : stats.values())
        {
            stat.advance( actor, act, afterAct, community);
        }
    }


    //--------------------------------------------------------------------
    public Statistic forPlayer(PlayerHandle player)
    {
        return stats.get( player );
    }
}
