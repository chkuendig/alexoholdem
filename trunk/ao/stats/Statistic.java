package ao.stats;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.context.HoldemContext;

/**
 *
 */
public interface Statistic
{
    public HoldemContext stats(AttributePool pool);
}
