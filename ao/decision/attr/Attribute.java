package ao.decision.attr;

/**
 *
 */
public class Attribute<T>
{
    //--------------------------------------------------------------------
    private T               value;
    private AttributeSet<T> attributeSet;


    //--------------------------------------------------------------------
    public Attribute(AttributeSet<T> valSet, T val)
    {
        value        = val;
        attributeSet = valSet;
    }


    //--------------------------------------------------------------------
    public AttributeSet<T> set()
    {
        return attributeSet;
    }


    //--------------------------------------------------------------------
    public T value()
    {
        return value;
    }
}
