package ao.stats;

import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.CumulativeState;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.state.StateManager;
import ao.stats.impl.GenericStats;
import ao.stats.impl.MultiStatistic;
import ao.stats.impl.SpecificStats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class HandStats implements CumulativeState
{
    //--------------------------------------------------------------------
    private Map<Serializable, SpecificStats> stats =
                    new HashMap<Serializable, SpecificStats>();
    private GenericStats common;


    //--------------------------------------------------------------------
    public HandStats(StateManager state)
    {
        common = new GenericStats( state.cards() );

        for (PlayerState pState : state.head().players())
        {
            stats.put(pState.handle().getId(),
                      new SpecificStats( pState.handle() ));
        }
    }

    private HandStats(GenericStats                     copyCommon,
                      Map<Serializable, SpecificStats> shallowStats)
    {
        common = copyCommon;

        for (Map.Entry<Serializable, SpecificStats> stat :
                shallowStats.entrySet())
        {
            stats.put( stat.getKey(), stat.getValue().prototype() );
        }
    }


    //--------------------------------------------------------------------
    public void advance(HandState    stateBeforeAct,
                        PlayerHandle actor,
                        RealAction   act,
                        HandState    stateAfterAct)
    {
        common.advance(stateBeforeAct,
                       actor,
                       act,
                       stateAfterAct );

        for (SpecificStats pStats : stats.values())
        {
            pStats.advance(stateBeforeAct,
                           actor,
                           act,
                           stateAfterAct);
        }
    }


    //--------------------------------------------------------------------
    public Statistic forPlayer(Serializable playerId)
    {
        return new MultiStatistic(common, stats.get( playerId ));
    }


    //--------------------------------------------------------------------
    public HandStats prototype()
    {
        return new HandStats(common.prototype(), stats);
    }
}
