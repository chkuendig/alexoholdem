package ao.ai.opp_model.decision.attr;

import java.util.Collection;

/**
 * Attributes for this Set must be equal() based on their
 *  granularity.  So if a ContinuousAttribute is 0.15 and
 *  another is 0.151 and the granularity is 0.01 then they
 *  are equal().
 */
public class ContinuousAttributeSet<T extends Number>
        implements AttributeSet<T>
{
    //--------------------------------------------------------------------

    
    //--------------------------------------------------------------------
    public ContinuousAttributeSet()
    {

    }


    //--------------------------------------------------------------------
    public boolean isDescrete() {  return false;  }

    public boolean typeEquals(AttributeSet<?> attributeSet)
    {
        return type().equals( attributeSet.type() );
    }
    public Object type()
    {
        return null;
    }


    //--------------------------------------------------------------------
    public Attribute<T> instanceOf(T value)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public Collection<Attribute<T>> values()
    {
        return null;
    }
}
