package ao.ai.opp_model.model.data;

import ao.ai.opp_model.model.context.ContextDomain;
import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.ExampleImpl;

/**
 *
 */
public class ActionExample
        extends ExampleImpl
        implements Context, DomainedContext
{
    public ActionExample(HoldemContext context,
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
