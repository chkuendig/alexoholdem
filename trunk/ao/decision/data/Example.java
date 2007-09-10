package ao.decision.data;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;

import java.util.Collection;

/**
 *
 */
public class Example<T> implements Context
{
    //--------------------------------------------------------------------
    private final Attribute<T> target;
    private final Context      deleget;


    //--------------------------------------------------------------------
    public Example(Context      context,
                   Attribute<T> targetAttribute)
    {
        assert targetAttribute.set().isDescrete();

        target  = targetAttribute;
        deleget = context;
    }


    //--------------------------------------------------------------------
    protected Context deleget()
    {
        return deleget;
    }


    //--------------------------------------------------------------------
    public Attribute<T> target()
    {
        return target;
    }


    //--------------------------------------------------------------------
    public Collection<Attribute> attributes()
    {
        return deleget.attributes();
    }

    public Collection<AttributeSet<?>> attributeSets()
    {
        return deleget.attributeSets();
    }

    public Attribute<?> attribute(Object ofType)
    {
        return deleget.attribute(ofType);
    }

    public <T> Attribute<T> attribute(AttributeSet<T> ofType)
    {
        return deleget.attribute(ofType);
    }
}
