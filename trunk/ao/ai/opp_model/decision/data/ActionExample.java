package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.context.HoldemContext;
import ao.holdem.model.act.SimpleAction;

/**
 *
 */
public class ActionExample
        extends HoldemExample<SimpleAction>
{
    public ActionExample(HoldemContext context,
                         Attribute<SimpleAction> targetAttribute)
    {
        super(context, targetAttribute);
    }
}
