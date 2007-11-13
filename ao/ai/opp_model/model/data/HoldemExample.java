package ao.ai.opp_model.model.data;

import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.example.ExampleImpl;
import ao.ai.opp_model.model.context.ContextDomain;

/**
 *
 */
public class HoldemExample
        extends ExampleImpl
        implements DomainedExample
{
    public HoldemExample(HoldemContext context,
                         Datum targetAttribute)
    {
        super(context, targetAttribute);
    }

    public boolean isApplicableTo(ContextDomain domain)
    {
        return ((HoldemContext) contextDeleget())
                    .isApplicableTo(domain);
    }
}
