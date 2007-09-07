package ao.decision.context;

import ao.holdem.bots.opp_model.predict.def.context.GenericContext;
import ao.decision.attr.AttributePool;

/**
 *
 */
public class PostFlopContext extends PreFlopContext
{
    public PostFlopContext(AttributePool pool, GenericContext ctx)
    {
        super(pool, ctx);
    }
}
