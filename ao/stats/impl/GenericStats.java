package ao.stats.impl;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextBuilder;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.holdem.model.BettingRound;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.card.CommunitySource;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.stats.CumulativeStatistic;

/**
 * betting round
 * community heat
 * 
 */
public class GenericStats implements CumulativeStatistic<GenericStats>
{
    //--------------------------------------------------------------------
    private HandState       forefront;
    private CommunitySource community;


    //--------------------------------------------------------------------
    public GenericStats(CommunitySource communitySource)
    {
        community = communitySource;
    }

    private GenericStats(HandState       copyForefront,
                         CommunitySource copyCommunity)
    {
        forefront = copyForefront;
        community = copyCommunity;
    }


    //--------------------------------------------------------------------
    public void advance(HandState    stateBeforeAct,
                        PlayerHandle actor,
                        RealAction   act,
                        HandState    stateAfterAct)
    {
        forefront = stateAfterAct;
    }


    //--------------------------------------------------------------------
    public HoldemContext nextActContext(AttributePool pool)
    {
        ContextBuilder ctx = new ContextBuilder();
        ctx.addDomains( ContextDomain.values() );

        BettingRound round = forefront.round();
        ctx.add(pool.fromEnum( round ));

//        ctx.add(pool.fromEnum(
//                Heat.fromHeat(
//                        CommunityMeasure.measure(
//                                community.community().asOf(round)))));

        return ctx;
    }


    //--------------------------------------------------------------------
    public GenericStats prototype()
    {
        return new GenericStats(forefront, community);
    }
}
