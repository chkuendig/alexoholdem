package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.HoldemContext;

/**
 *
 */
public class HoldemExample<T>
        extends Example<T>
        implements HoldemContext
{
    public HoldemExample(Context context,
                         Attribute<T> targetAttribute)
    {
        super(context, targetAttribute);
    }

    public boolean isApplicableTo(ContextDomain domain)
    {
        return ((HoldemContext) deleget()).isApplicableTo( domain );
    }
}
