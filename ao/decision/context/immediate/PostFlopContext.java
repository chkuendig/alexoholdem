package ao.decision.context.immediate;

import ao.decision.attr.AttributePool;
import ao.decision.context.ContextDomain;
import ao.holdem.bots.opp_model.predict.def.context.GenericContext;

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
