package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.ai.opp_model.decision.context.ContextDomain;
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
