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

    //--------------------------------------------------------------------
    public String toString()
    {
//        return attributeSet.type() + " :: " + value.toString();
        return value.toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return attributeSet.equals(attribute.attributeSet) &&
               value.equals(attribute.value);
    }

    public int hashCode()
    {
        int result;
        result = value.hashCode();
        result = 31 * result + attributeSet.hashCode();
        return result;
    }
}
