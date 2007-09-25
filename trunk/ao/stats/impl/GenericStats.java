package ao.stats.impl;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextBuilder;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.domain.Heat;
import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;
import ao.odds.CommunityMeasure;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.CumulativeStatistic;

/**
 *
 */
public class GenericStats implements CumulativeStatistic
{
    //--------------------------------------------------------------------
    private HandState forefront;
    private Community currCommunity;

            
    //--------------------------------------------------------------------
    public void reset()
    {
        forefront     = null;
        currCommunity = null;
    }
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct,
            Hole        hole)
    {
        forefront     = stateBeforeAct;
        currCommunity = communityBeforeAct;
    }


    //--------------------------------------------------------------------
    public HoldemContext stats(AttributePool pool)
    {
        ContextBuilder ctx = new ContextBuilder();
        ctx.addDomains( ContextDomain.values() );

        ctx.add(pool.fromEnum( forefront.round() ));

        ctx.add(pool.fromEnum(
                Heat.fromHeat(
                        CommunityMeasure.measure(currCommunity))));

        return ctx;
    }
}
