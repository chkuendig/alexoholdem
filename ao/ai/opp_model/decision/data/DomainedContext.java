package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.context.ContextDomain;

/**
 *
 */
public interface DomainedContext
{
    public boolean isApplicableTo(ContextDomain domain);
}
