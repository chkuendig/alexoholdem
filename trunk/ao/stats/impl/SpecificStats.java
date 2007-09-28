package ao.stats.impl;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextBuilder;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.ai.opp_model.decision.domain.BetsToCall;
import ao.ai.opp_model.decision.domain.PotOdds;
import ao.holdem.model.BettingRound;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.stats.CumulativeStatistic;

import java.io.Serializable;

/**
 * is Committed this round
 * bets to call
 * betting round
 * is last bets called > 0
 * is last act: bet/raise
 * immediate pot odds
 * hand strength (predicted)
 */
public class SpecificStats implements CumulativeStatistic<SpecificStats>
{
    //--------------------------------------------------------------------
    private HandState    startOfRoundForNextAct;
    private RealAction   prevAct;
    private HandState    beforeCurrAct;
    private RealAction   currAct;
    private HandState    beforeNextAct;
    private Serializable subjectId;
    private boolean      isUnfolded;


    //--------------------------------------------------------------------
    public SpecificStats(PlayerHandle subject)
    {
        subjectId  = subject.getId();
        isUnfolded = true;
    }

    private SpecificStats(HandState    copyStartOfRound,
                          HandState    copyPrevState,
                          RealAction   copyPrevAct,
                          HandState    copyCurrState,
                          RealAction   copyCurrAct,
                          Serializable copySubjectId,
                          boolean      copyIsUnfolded)
    {
        startOfRoundForNextAct = copyStartOfRound;
        beforeCurrAct          = copyPrevState;
        prevAct                = copyPrevAct;
        beforeNextAct          = copyCurrState;
        currAct                = copyCurrAct;
        subjectId              = copySubjectId;
        isUnfolded             = copyIsUnfolded;
    }


    //-------------------------------------------------------------------
    public void advance(HandState    stateBeforeAct,
                        PlayerHandle actor,
                        RealAction   act,
                        HandState    stateAfterAct)
    {
        if (!isUnfolded) return;

        if (actor.getId().equals( subjectId ) &&
                !act.isBlind())
        {
            if (act.isFold())
            {
                isUnfolded = false;
                return;
            }

            prevAct       = currAct;
            currAct       = act;
            beforeCurrAct = stateBeforeAct;
        }

        if (stateAfterAct.nextToAct().handle()
                .getId().equals( subjectId ))
        {
            beforeNextAct = stateAfterAct;
        }

        if (startOfRoundForNextAct == null ||
                startOfRoundForNextAct.round() !=
                        stateAfterAct.round() &&
                stateAfterAct.round() != null)
        {
            startOfRoundForNextAct = stateAfterAct;
        }
    }


    //--------------------------------------------------------------------
    public HoldemContext nextActContext(AttributePool pool)
    {
        assert isUnfolded;

        ContextBuilder ctx = new ContextBuilder();
        ctx.addDomain( ContextDomain.FIRST_ACT );

        ctx.add(pool.fromEnum(
                PotOdds.fromPotOdds(
                        ((double) beforeNextAct.toCall().smallBlinds()) /
                          (beforeNextAct.toCall().smallBlinds() +
                           beforeNextAct.pot().smallBlinds()))));

        Money roundCommit = beforeNextAct.nextToAct().commitment().minus(
                                startOfRoundForNextAct.stakes());
        ctx.add(pool.fromUntyped(
                "Is Committed This Round",
                roundCommit.compareTo( Money.ZERO ) > 0));

        ctx.add(pool.fromEnum(
                BetsToCall.fromBets(beforeNextAct.betsToCall())));

        if (beforeCurrAct != null && prevAct != null)
        {
            ctx.add(pool.fromUntyped(
                    "Last Act: Bet/Raise",
                    prevAct.toSimpleAction() == SimpleAction.RAISE));

            ctx.add(pool.fromUntyped(
                    "Last Bets Called > 0",
                    beforeCurrAct.toCall().compareTo(Money.ZERO) > 0));

            ctx.addDomain((beforeNextAct.round() == BettingRound.PREFLOP)
                           ? ContextDomain.PRE_FLOP
                           : ContextDomain.POST_FLOP);
        }

        return ctx;
    }

    
    //--------------------------------------------------------------------
    public SpecificStats prototype()
    {
        return new SpecificStats(startOfRoundForNextAct,
                                 beforeCurrAct, prevAct,
                                 beforeNextAct, currAct,
                                 subjectId, isUnfolded);
    }
}
