package ao.stats;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.impl.SpecificStats;

/**
 *
 */
public class PlayerStats implements CumulativeStatistic
{
    //--------------------------------------------------------------------
    private SpecificStats stats;


    //--------------------------------------------------------------------
    public PlayerStats(PlayerHandle player)
    {
        stats = new SpecificStats( player );
    }


    //--------------------------------------------------------------------
    public void reset()
    {
        stats.reset();
    }


    //--------------------------------------------------------------------
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct,
            Hole        hole)
    {
        stats.advance(stateBeforeAct,
                      actor,
                      act,
                      communityBeforeAct,
                      hole);
    }


    //--------------------------------------------------------------------
    public HoldemContext stats(AttributePool pool)
    {
        return stats.stats( pool );
    }
}
