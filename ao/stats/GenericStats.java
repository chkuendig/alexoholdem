package ao.stats;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.domain.Heat;
import ao.holdem.model.Community;
import ao.holdem.model.act.RealAction;
import ao.odds.CommunityMeasure;
import ao.state.HandState;
import ao.state.PlayerState;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class GenericStats implements CumulativeStatistic
{
    //--------------------------------------------------------------------
    private HandState forefront;
    private Community currCommunity;

            
    //--------------------------------------------------------------------
    public void init(HandState startOfHand)
    {
        forefront = startOfHand;
    }
    public void advance(
            PlayerState actor,
            RealAction  act,
            HandState   afterAct,
            Community   community)
    {
        forefront     = afterAct;
        currCommunity = community;
    }


    //--------------------------------------------------------------------
    public Collection<Attribute<?>> stats(AttributePool pool)
    {
        Collection<Attribute<?>> stats = new ArrayList<Attribute<?>>();

        stats.add(pool.fromEnum( forefront.round() ));

        stats.add(pool.fromEnum(
                Heat.fromHeat(
                        CommunityMeasure.measure(currCommunity))));

        return stats;
    }
}
