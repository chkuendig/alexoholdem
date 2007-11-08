package ao.ai.opp_model.model.data;

import ao.ai.opp_model.model.context.ContextDomain;

/**
 *
 */
public interface DomainedContext
{
    public boolean isApplicableTo(ContextDomain domain);
}
