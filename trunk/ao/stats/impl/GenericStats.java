package ao.stats.impl;

import ao.ai.opp_model.model.context.ContextDomain;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.decision2.data.DataPool;
import ao.holdem.model.BettingRound;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.card.CommunitySource;
import ao.odds.CommunityMeasure;
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
    public HoldemContext nextActContext(DataPool pool)
    {
        HoldemContext ctx = new HoldemContext();
        ctx.addDomains( ContextDomain.values() );

        BettingRound round = forefront.round();
        ctx.add(pool.fromEnum( round ));

        ctx.add(pool.newContinuous("Community Heat",
                        CommunityMeasure.measure(
                                community.community().asOf(round))));

        return ctx;
    }


    //--------------------------------------------------------------------
    public GenericStats prototype()
    {
        return new GenericStats(forefront, community);
    }
}
