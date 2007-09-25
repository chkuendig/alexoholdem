package ao.stats;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.impl.SpecificStats;

import java.util.Collection;

/**
 *
 */
public class PlayerStats implements CumulativeStatistic
{
    //--------------------------------------------------------------------
    private SpecificStats stats;


    //--------------------------------------------------------------------
    public PlayerStats(PlayerHandle player, Hole hole)
    {
        stats = new SpecificStats( player, hole );
    }


    //--------------------------------------------------------------------
    public void init(HandState startOfHand)
    {
        stats.init(startOfHand);
    }


    //--------------------------------------------------------------------
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct)
    {
        stats.advance(stateBeforeAct, actor, act, communityBeforeAct);
    }


    //--------------------------------------------------------------------
    public Collection<Attribute<?>> stats(AttributePool pool)
    {
        return stats.stats( pool );
    }
}
