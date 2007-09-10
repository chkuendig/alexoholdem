package ao.decision.context.immediate;

import ao.decision.attr.AttributePool;
import ao.decision.context.ContextDomain;
import ao.decision.context.HoldemContext;
import ao.decision.data.ContextImpl;
import ao.decision.domain.*;
import ao.holdem.bots.opp_model.predict.def.context.GenericContext;

/**
 *
 */
public class FirstActContext
        extends ContextImpl
        implements HoldemContext
{
    //--------------------------------------------------------------------
    public FirstActContext(AttributePool pool, GenericContext ctx)
    {
        add(pool.fromEnum(
                BetsToCall.fromBets(ctx.betsToCall())));

        add(pool.fromEnum(
                ActivePosition.fromPosition(
                        ctx.numOpps(), ctx.activePosition())));

        add(pool.fromEnum(
                ActiveOpponents.fromActiveOpps(
                        ctx.numActiveOpps())));

        add(pool.fromEnum(
                BetRatio.fromBetRatio(
                        ctx.betRatio())));
        add(pool.fromEnum(
                PotOdds.fromPotOdds(
                        ctx.immedatePotOdds())));
        add(pool.fromEnum(
                PotRatio.fromPotRatio(
                        ctx.potRatio())));
    }

    
    //--------------------------------------------------------------------
    public ContextDomain domain()
    {
        return ContextDomain.FIRST_ACT;
    }
}
