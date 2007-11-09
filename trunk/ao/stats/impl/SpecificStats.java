package ao.stats.impl;

import ao.ai.opp_model.model.context.ContextDomain;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.model.domain.BetsToCall;
import ao.ai.opp_model.decision2.data.DataPool;
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

    private int checks;
    private int calls;
    private int raises;


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

                 if (act == RealAction.CHECK) checks++;
            else if (act.isCheckCall())       calls++;
            else if (act.isBetRaise())        raises++;
        }

//        if (! stateAfterAct.atEndOfHand())
//        {
            if (//stateAfterAct.atEndOfHand() ||
                    stateAfterAct.nextToAct().handle()
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
//        }
    }


    //--------------------------------------------------------------------
    public HoldemContext nextActContext(DataPool pool)
    {
        assert isUnfolded;

        HoldemContext ctx = new HoldemContext();
        ctx.addDomain( ContextDomain.FIRST_ACT );

        ctx.add(pool.newContinuous("Pot Odds",
                        ((double) beforeNextAct.toCall().smallBlinds()) /
                          (beforeNextAct.toCall().smallBlinds() +
                           beforeNextAct.pot().smallBlinds())));

        Money roundCommit = beforeNextAct.nextToAct().commitment().minus(
                                startOfRoundForNextAct.stakes());
        ctx.add(pool.newMultistate(
                "Is Committed This Round",
                roundCommit.compareTo( Money.ZERO ) > 0));

        ctx.add(pool.fromEnum(
                BetsToCall.fromBets(beforeNextAct.betsToCall())));

        // positional stats
        ctx.add(pool.newContinuous("Position",
                        beforeNextAct.nextToActPosition()));
        ctx.add(pool.newContinuous("Active Position",
                        beforeNextAct.nextToActActivePosition()));

        // betting stats
        int numActs = checks + calls + raises;
        if (numActs != 0)
        {
            ctx.add(pool.newContinuous("Bet Ratio",
                            (double) raises / numActs));
            ctx.add(pool.newContinuous("Call Ratio",
                            (double) calls / numActs));
            ctx.add(pool.newContinuous("Check Ratio",
                            (double) checks / numActs));
        }

        if (beforeCurrAct != null && prevAct != null)
        {
            ctx.add(pool.newMultistate(
                    "Last Act: Bet/Raise",
                    prevAct.toSimpleAction() == SimpleAction.RAISE));

            ctx.add(pool.newMultistate(
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
