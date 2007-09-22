package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.context.ActionContext;
import ao.ai.opp_model.decision.context.ContextDomain;
import ao.holdem.model.act.SimpleAction;

/**
 *
 */
public class ActionExample
        extends Example<SimpleAction>
        implements ActionContext
{
    public ActionExample(ActionContext context,
                         Attribute<SimpleAction> targetAttribute)
    {
        super(context, targetAttribute);
    }

    public ContextDomain domain()
    {
        return ((ActionContext) deleget()).domain();
    }
}
