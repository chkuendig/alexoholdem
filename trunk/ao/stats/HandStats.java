package ao.stats;

import ao.holdem.model.Community;
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
    public HandStats()
    {

    }


    //--------------------------------------------------------------------
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct)
    {
        if (! stateBeforeAct.equals( prevState ))
        {
            for (PlayerState player : stateBeforeAct.players())
            {
                PlayerHandle handle = player.handle();
                if (! stats.containsKey( handle.getId() ))
                {
                    stats.put( handle.getId(),
                           new PlayerStats(handle,
                                           communityBeforeAct.cards().holeFor(handle)));
                }
            }
        }

        common.advance(stateBeforeAct, actor, act, communityBeforeAct);
        for (PlayerStats stat : stats.values())
        {
            stat.advance(stateBeforeAct, actor, act, communityBeforeAct);
        }
    }


    //--------------------------------------------------------------------
    public Statistic forPlayer(PlayerHandle player)
    {
        return new MultiStatistic(common, stats.get( player ));
    }
}
