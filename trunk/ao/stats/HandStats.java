package ao.stats;

import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.CumulativeState;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.impl.GenericStats;
import ao.stats.impl.MultiStatistic;

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
    private HandState    prevState;


    //--------------------------------------------------------------------
    public HandStats() {}


    //--------------------------------------------------------------------
    public void reset()
    {
        common.reset();
    }


    //--------------------------------------------------------------------
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct,
            Hole        hole)
    {
        if (! stateBeforeAct.handsEqual( prevState ))
        {
            for (PlayerState player : stateBeforeAct.players())
            {
                PlayerHandle handle = player.handle();
                PlayerStats  pStats = stats.get( handle.getId() );
                if (pStats == null)
                {
                    pStats = new PlayerStats(handle);
                    stats.put(handle.getId(), pStats);
                }
                pStats.reset();
            }
        }

        common.advance(stateBeforeAct,
                       actor, act, communityBeforeAct, hole);
        for (PlayerStats stat : stats.values())
        {
            stat.advance(stateBeforeAct,
                         actor, act, communityBeforeAct, hole);
        }
        prevState = stateBeforeAct;
    }


    //--------------------------------------------------------------------
    public Statistic forPlayer(Serializable playerId)
    {
        return new MultiStatistic(common, stats.get( playerId ));
    }
}
