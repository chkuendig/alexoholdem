package ao.stats.impl;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextBuilder;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.ai.opp_model.decision.domain.BetsToCall;
import ao.ai.opp_model.decision.domain.PotOdds;
import ao.holdem.model.BettingRound;
import ao.holdem.model.Community;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.state.HandState;
import ao.stats.CumulativeStatistic;

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
    private HandState  startOfRound;
    private HandState  prevState;
    private RealAction prevAct;
    private HandState  currState;
    private RealAction currAct;


    //--------------------------------------------------------------------
    public SpecificStats()
    {
//        subjectId = subject.getId();
    }

    private SpecificStats(HandState  copyStartOfRound,
                          HandState  copyPrevState,
                          RealAction copyPrevAct,
                          HandState  copyCurrState,
                          RealAction copyCurrAct)
    {
        startOfRound = copyStartOfRound;
        prevState    = copyPrevState;
        prevAct      = copyPrevAct;
        currState    = copyCurrState;
        currAct      = copyCurrAct;
    }


    //-------------------------------------------------------------------
    public void advance(HandState stateBeforeAct)
    {
        advanceState(stateBeforeAct);
    }
    public void advance(RealAction act, Community communityBeforeAct)
    {
        advanceAct(act);
    }


    //--------------------------------------------------------------------
    private void advanceAct(RealAction act)
    {
//        if (subjectId.equals( actor.handle().getId() ))
//        {
            prevAct = currAct;
            currAct = act;
//        }
    }
    private void advanceState(HandState forefront)
    {
//        if (subjectId.equals( forefront.nextToAct().handle().getId() ))
//        {
            prevState = currState;
            currState = forefront;
//        }
        if (startOfRound.round() != forefront.round() &&
               forefront.round() != null)
        {
            startOfRound = forefront;
        }
    }



    //--------------------------------------------------------------------
    public HoldemContext stats(AttributePool pool)
    {
        ContextBuilder ctx = new ContextBuilder();
        ctx.addDomain( ContextDomain.FIRST_ACT );

        ctx.add(pool.fromEnum(
                PotOdds.fromPotOdds(
                        ((double) currState.toCall().smallBlinds()) /
                          (currState.toCall().smallBlinds() +
                           currState.pot().smallBlinds()))));

        Money roundCommit = currState.nextToAct().commitment().minus(
                                startOfRound.stakes());
        ctx.add(pool.fromUntyped(
                "Is Committed This Round",
                roundCommit.compareTo( Money.ZERO ) > 0));

        ctx.add(pool.fromEnum(
                BetsToCall.fromBets(currState.betsToCall())));

        if (prevState != null && prevAct != null)
        {
            ctx.add(pool.fromUntyped(
                    "Last Act: Bet/Raise",
                    prevAct.toSimpleAction() == SimpleAction.RAISE));

            ctx.add(pool.fromUntyped(
                    "Last Bets Called > 0",
                    prevState.toCall().compareTo(Money.ZERO) > 0));

            ctx.addDomain((currState.round() == BettingRound.PREFLOP)
                           ? ContextDomain.PRE_FLOP
                           : ContextDomain.POST_FLOP);
        }

        return ctx;
    }

    
    //--------------------------------------------------------------------
    public SpecificStats prototype()
    {
        return new SpecificStats(startOfRound,
                                 prevState, prevAct,
                                 currState, currAct);
    }
}
