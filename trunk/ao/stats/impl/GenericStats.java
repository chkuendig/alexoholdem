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

    private int checks;
    private int calls;
    private int raises;


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
        if (! act.isBlind())
        {
                 if (act == RealAction.CHECK) checks++;
            else if (act.isCheckCall())       calls++;
            else if (act.isBetRaise())        raises++;
        }

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

        // betting stats
        int numActs = checks + calls + raises;
        if (numActs != 0)
        {
            ctx.add(pool.newContinuous("Total Bet Ratio",
                            (double) raises / numActs));
            ctx.add(pool.newContinuous("Total Call Ratio",
                            (double) calls / numActs));
            ctx.add(pool.newContinuous("Total Check Ratio",
                            (double) checks / numActs));
        }

        ctx.add(pool.newContinuous("Players",
                        forefront.players().length));
        ctx.add(pool.newContinuous("Active Players",
                        forefront.numActivePlayers()));

        return ctx;
    }


    //--------------------------------------------------------------------
    public GenericStats prototype()
    {
        return new GenericStats(forefront, community);
    }
}
