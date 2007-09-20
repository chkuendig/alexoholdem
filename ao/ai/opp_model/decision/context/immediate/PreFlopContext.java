package ao.ai.opp_model.decision.context.immediate;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.holdem.def.state.env.TakenAction;

/**
 *
 */
public class PreFlopContext extends FirstActContext
{
    //--------------------------------------------------------------------
    public PreFlopContext(AttributePool pool, GenericContext ctx)
    {
        super(pool, ctx);

        add(pool.fromUntyped(
                "Last Bets Called > 0",
                ctx.lastBetsToCall() > 0));

        add(pool.fromUntyped(
                "Last Act: Bet/Raise",
                ctx.lastAct() == TakenAction.RAISE));
    }


    //--------------------------------------------------------------------
    public ContextDomain domain()
    {
        return ContextDomain.FIRST_ACT;
    }
}
