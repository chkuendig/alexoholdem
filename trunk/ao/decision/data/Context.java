package ao.decision.data;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;

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
