package ao.ai.opp_model.model.data;

import ao.ai.opp_model.model.context.ContextDomain;

/**
 *
 */
public interface Domained
{
    public boolean isApplicableTo(ContextDomain domain);
}
