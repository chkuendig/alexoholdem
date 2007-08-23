package ao.decision.data;

import ao.decision.attr.Attribute;

import java.util.Collection;

/**
 *
 */
public class Example<T> extends Context
{
    //--------------------------------------------------------------------
    private final Attribute<T> target;


    //--------------------------------------------------------------------
    public Example(Collection<Attribute> attributes,
                   Attribute<T>          targetAttribute)
    {
        super(attributes);
        assert targetAttribute.set().isDescrete();
                
        target = targetAttribute;
    }

    
    //--------------------------------------------------------------------
    public Attribute<T> target()
    {
        return target;
    }
}
