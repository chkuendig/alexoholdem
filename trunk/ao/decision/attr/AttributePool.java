package ao.decision.attr;

import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class AttributePool
{
    //--------------------------------------------------------------------
    private Map<Object, AttributeSet> attributes;


    //--------------------------------------------------------------------
    public AttributePool()
    {
        attributes = new HashMap<Object, AttributeSet>();
    }


    //--------------------------------------------------------------------
    public <T extends Enum<T>>
            Attribute<T> fromEnum(T enumValue)
    {
        return fromUntyped(enumValue.getDeclaringClass().getSimpleName(),
                           enumValue);
    }

    public <T> Attribute<T> fromTyped(T value)
    {
        return fromUntyped(value.getClass(), value);
    }

    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public <T> Attribute<T> fromUntyped(Object type, T value)
    {
        if (! attributes.containsKey(type))
        {
            attributes.put(type, new DescreteAttributeSet<T>(type));
        }
        return attributes.get(type).instanceOf( value );
    }


    //--------------------------------------------------------------------
    public <T> Attribute<T> fromContinuouse(Object type, Number value)
    {
        return null;
    }
}
