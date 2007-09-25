package ao.stats.impl;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.domain.BetsToCall;
import ao.ai.opp_model.decision.domain.PotOdds;
import ao.holdem.model.Money;
import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.PlayerState;
import ao.stats.CumulativeStatistic;

import java.util.ArrayList;
import java.util.Collection;

/**
 * is Committed this round
 * bets to call
 * betting round
 * is last bets called > 0
 * is last act: bet/raise
 * immediate pot odds
 * hand strength (predicted)
 */
public class SpecificStats implements CumulativeStatistic
{
    //--------------------------------------------------------------------
    private PlayerHandle subject;
    private Hole         hole;

    private HandState  startOfRound;

    private HandState  prevState;
    private RealAction prevAct;

    private HandState  currState;
    private RealAction currAct;


    //--------------------------------------------------------------------
    public SpecificStats(PlayerHandle subject, Hole hole)
    {
        this.subject = subject;
        this.hole    = hole;
    }


    //--------------------------------------------------------------------
//    public void init(HandState startOfHand)
//    {
////        advanceState(startOfHand);
//    }


    //--------------------------------------------------------------------
    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct)
    {
        advanceAct(actor, act);
        advanceState(stateBeforeAct);
    }


    //--------------------------------------------------------------------
    private void advanceAct(PlayerState actor, RealAction act)
    {
        if (subject.equals( actor.handle() ))
        {
            prevAct = currAct;
            currAct = act;
        }
    }
    private void advanceState(HandState forefront)
    {
        if (subject.equals( forefront.nextToAct().handle() ))
        {
            prevState = currState;
            currState = forefront;
        }
        if (startOfRound.round() != forefront.round() &&
               forefront.round() != null)
        {
            startOfRound = forefront;
        }
    }



    //--------------------------------------------------------------------
    public Collection<Attribute<?>> stats(AttributePool pool)
    {
        Collection<Attribute<?>> stats = new ArrayList<Attribute<?>>();

        stats.add(pool.fromEnum(
                PotOdds.fromPotOdds(
                        ((double) currState.toCall().smallBlinds()) /
                          (currState.toCall().smallBlinds() +
                           currState.pot().smallBlinds()))));

        Money roundCommit = currState.nextToAct().commitment().minus(
                                startOfRound.stakes());
        stats.add(pool.fromUntyped(
                "Is Committed This Round",
                roundCommit.compareTo( Money.ZERO ) > 0));

        stats.add(pool.fromEnum(
                BetsToCall.fromBets(currState.betsToCall())));

        if (prevAct != null)
        {
            stats.add(pool.fromUntyped(
                    "Last Act: Bet/Raise",
                    prevAct.toSimpleAction() == SimpleAction.RAISE));
        }
        if (prevState != null)
        {
            stats.add(pool.fromUntyped(
                    "Last Bets Called > 0",
                    prevState.toCall().compareTo(Money.ZERO) > 0));
        }

        return stats;
    }
}
