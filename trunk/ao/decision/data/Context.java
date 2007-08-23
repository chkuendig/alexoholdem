package ao.decision.data;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 */
public class Context
{
    //--------------------------------------------------------------------
    private Map<Object, Attribute<?>> ctx;


    //--------------------------------------------------------------------
    public Context(Collection<Attribute> attributes)
    {
        ctx = new HashMap<Object, Attribute<?>>();
        for (Attribute<?> attr : attributes)
        {
            if (ctx.put(attr.set().type(), attr) != null)
            {
                throw new Error("duplicate type " + attr.set().type());
            }
        }
    }


    //--------------------------------------------------------------------
    public Collection<Attribute<?>> attributes()
    {
        return ctx.values();
    }

    public Collection<AttributeSet<?>> attributeSets()
    {
        Collection<AttributeSet<?>> attributeSets =
                new ArrayList<AttributeSet<?>>();
        for (Attribute<?> attribute : attributes())
        {
            attributeSets.add( attribute.set() );
        }
        return attributeSets;
    }

    public Attribute<?> attribute(Object ofType)
    {
        return ctx.get( ofType );
    }

    @SuppressWarnings("unchecked")
    public <T> Attribute<T> attribute(AttributeSet<T> ofType)
    {
        return (Attribute<T>) attribute( ofType.type() );
    }
}
