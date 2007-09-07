package ao.decision.context;

import ao.holdem.bots.opp_model.predict.def.context.GenericContext;
import ao.decision.attr.AttributePool;

/**
 *
 */
public class PreFlopContext extends FirstActContext
{
    public PreFlopContext(AttributePool pool, GenericContext ctx)
    {
        super(pool, ctx);
    }
}
