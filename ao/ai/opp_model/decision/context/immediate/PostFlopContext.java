package ao.ai.opp_model.decision.context.immediate;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.immediate.GenericContext;

/**
 *
 */
public class PostFlopContext extends PreFlopContext
{
    //--------------------------------------------------------------------
    public PostFlopContext(AttributePool pool, GenericContext ctx)
    {
        super(pool, ctx);

        add(pool.fromUntyped(
                "Committed This Round",
                ctx.committedThisRound()));

        add(pool.fromEnum( ctx.round() ));

//        add(pool.fromEnum(
//                Heat.fromHeat(
//                        thermostat.heat(ctx.community()))));
    }


    //--------------------------------------------------------------------
    public ContextDomain domain()
    {
        return ContextDomain.FIRST_ACT;
    }
}
