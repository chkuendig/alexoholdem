package ao.ai.opp_model.decision.context;

import ao.ai.opp_model.decision.data.Context;

/**
 *
 */
public interface HoldemContext extends Context
{
    public boolean isApplicableTo(ContextDomain domain);
}
