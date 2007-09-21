package ao.stats;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.holdem.model.Community;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;

import java.util.Collection;

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
        stats = new SpecificStats( player, null );
    }


    //--------------------------------------------------------------------
    public void init(HandState startOfHand)
    {
        stats.init(startOfHand);
    }

    public void advance(PlayerState actor,
                        RealAction  act,
                        HandState   afterAct,
                        Community   community)
    {
        stats.advance(actor, act, afterAct, community);
    }

    public Collection<Attribute<?>> stats(AttributePool pool)
    {
        return stats.stats( pool );
    }
}
