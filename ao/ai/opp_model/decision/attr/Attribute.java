package ao.ai.opp_model.decision.attr;

/**
 *
 */
public class Attribute<T> implements Comparable<Attribute>
{
    //--------------------------------------------------------------------
    private final int             key;
    private final T               value;
    private final AttributeSet<T> attributeSet;


    //--------------------------------------------------------------------
    public Attribute(AttributeSet<T> valSet, T val, int id)
    {
        key          = id;
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
    public int compareTo(Attribute o)
    {
        return (key < o.key) ? -1 :
                (key > o.key) ? 1 : 0;
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
