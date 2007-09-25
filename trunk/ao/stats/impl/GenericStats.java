package ao.stats.impl;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.domain.Heat;
import ao.holdem.model.Community;
import ao.holdem.model.act.RealAction;
import ao.odds.CommunityMeasure;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.CumulativeStatistic;

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
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct)
    {
        forefront     = stateBeforeAct;
        currCommunity = communityBeforeAct;
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
