package ao.stats;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributePool;

import java.util.Collection;

/**
 *
 */
public interface Statistic
{
    public Collection<Attribute<?>> stats(AttributePool pool);
}
