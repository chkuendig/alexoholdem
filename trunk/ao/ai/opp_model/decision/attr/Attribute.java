package ao.ai.opp_model.decision.attr;

/**
 *
 */
public class Attribute<T> implements Comparable<Attribute>
{
    //--------------------------------------------------------------------
    private static volatile int nextKey = 0;


    //--------------------------------------------------------------------
    private final T               value;
    private final AttributeSet<T> attributeSet;
    private final int             key;


    //--------------------------------------------------------------------
    public Attribute(AttributeSet<T> valSet, T val)
    {
        value        = val;
        attributeSet = valSet;
        key          = nextKey++;
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
    public String toFullString()
    {
        return attributeSet.type() + " :: " + value.toString();
    }

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
