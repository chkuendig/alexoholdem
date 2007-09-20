package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributeSet;

import java.util.Collection;

/**
 *
 */
public interface Context
{
    public Collection<Attribute> attributes();

    public Collection<AttributeSet<?>> attributeSets();

    public Attribute<?> attribute(Object ofType);

    public <T> Attribute<T> attribute(AttributeSet<T> ofType);
}
