package ao.ai.opp_model.decision.attr;

/**
 *
 */
public class ContinuousAttribute<T extends Comparable<T>>
        extends Attribute<T>
        implements Comparable<ContinuousAttribute<T>>
{
    //--------------------------------------------------------------------
    private static volatile int nextKey = 0;


    //--------------------------------------------------------------------
    // arbitrator for comparing identical values
    private int key = nextKey++;


    //--------------------------------------------------------------------
    public ContinuousAttribute(
            ContinuousAttributeSet<T> valSet,
            T val)
    {
        super(valSet, val);
    }


    //--------------------------------------------------------------------
    public int compareTo(ContinuousAttribute<T> o)
    {
        int cmp = value().compareTo( o.value() );
        return cmp == 0
                ? (key - o.key)
                : cmp;
    }
}
