package ao.decision.data;

import ao.decision.attr.Attribute;
import ao.decision.context.HoldemContext;
import ao.decision.context.ContextDomain;
import ao.holdem.def.state.env.TakenAction;

/**
 *
 */
public class HoldemExample
        extends Example<TakenAction>
        implements HoldemContext
{
    public HoldemExample(HoldemContext context,
                         Attribute<TakenAction> targetAttribute)
    {
        super(context, targetAttribute);
    }

    public ContextDomain domain()
    {
        return ((HoldemContext) deleget()).domain();
    }
}
