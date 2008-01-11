package ao.stats.impl;

import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.ContextImpl;
import ao.ai.opp_model.decision.input.raw.example.Datum;
import ao.ai.opp_model.model.domain.BetsToCall;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
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

    private int betsCalled[] = new int[4];
    private int betsRaised[] = new int[4];

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

            if (act.isBetRaise())
            {
                betsRaised[ beforeCurrAct.round().ordinal() ]++;
            }
            else if (act.isCheckCall())
            {
                betsCalled[ beforeCurrAct.round().ordinal() ] +=
                        beforeCurrAct.betsToCall();
            }
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
    public Context nextActContext()
    {
        assert isUnfolded;

        Context ctx = new ContextImpl();

        ctx.add(new Datum("Immediate Pot Odds",
                        ((double) beforeNextAct.toCall().smallBlinds()) /
                          (beforeNextAct.toCall().smallBlinds() +
                           beforeNextAct.pot().smallBlinds())));

        Money roundCommit = beforeNextAct.nextToAct().commitment().minus(
                                startOfRoundForNextAct.stakes());
        ctx.add(new Datum(
                "Is Committed This Round",
                roundCommit.compareTo( Money.ZERO ) > 0));
                                             
        ctx.add(new Datum(
                BetsToCall.fromBets(beforeNextAct.betsToCall())));

        // positional stats
//        ctx.add(new Datum("Position",
//                        beforeNextAct.nextToActPosition()));
        ctx.add(new Datum("Active Position",
                        beforeNextAct.nextToActActivePosition()));

        for (int i = 0; i <= beforeNextAct.round().ordinal(); i++)
        {
            ctx.add(new Datum("round " + i + " raises",
                                betsRaised[ i ]));
//            ctx.add(new Datum("round " + i + " calls",
//                                betsCalled[ i ]));
        }

        // betting stats
        int numActs = checks + calls + raises;
        if (numActs != 0)
        {
            ctx.add(new Datum("Bet Ratio",
                            (double) raises / numActs));
//            ctx.add(new Datum("Call Ratio",
//                            (double) calls / numActs));
//            ctx.add(new Datum("Check Ratio",
//                            (double) checks / numActs));
        }

        if (beforeCurrAct != null && prevAct != null)
        {
//            ctx.add(new Datum(
//                    "Last Act: Bet/Raise",
//                    prevAct.toSimpleAction() == SimpleAction.RAISE));
            ctx.add(new Datum("Last Act", prevAct));

            ctx.add(new Datum(
                    "Last Bets Called > 0",
                    beforeCurrAct.toCall().compareTo(Money.ZERO) > 0));
//            ctx.add(new Datum(
//                    "Last Bets Called",
//                    beforeCurrAct.betsToCall()));
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
