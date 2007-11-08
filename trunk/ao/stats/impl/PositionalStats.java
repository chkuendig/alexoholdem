package ao.stats.impl;

import ao.ai.opp_model.model.context.ContextDomain;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.decision2.data.DataPool;
import ao.holdem.model.BettingRound;
import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.stats.CumulativeStatistic;

import java.io.Serializable;

/**
 * position / # players
 * active position / # active players
 * # unfolded players / # players
 */
public class PositionalStats
        implements CumulativeStatistic<PositionalStats>
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
    public PositionalStats(PlayerHandle subject)
    {
        subjectId  = subject.getId();
        isUnfolded = true;
    }

    private PositionalStats(HandState    copyStartOfRound,
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

        ctx.add(pool.newContinuous("Position",
                        beforeNextAct.nextToActPosition()));

        ctx.add(pool.newContinuous("Active Position",
                        beforeNextAct.nextToActActivePosition()));

        if (beforeCurrAct != null && prevAct != null)
        {
            ctx.addDomain((beforeNextAct.round() == BettingRound.PREFLOP)
                           ? ContextDomain.PRE_FLOP
                           : ContextDomain.POST_FLOP);
        }

        return ctx;
    }


    //--------------------------------------------------------------------
    public PositionalStats prototype()
    {
        return new PositionalStats(startOfRoundForNextAct,
                                   beforeCurrAct, prevAct,
                                   beforeNextAct, currAct,
                                   subjectId, isUnfolded);
    }
}
