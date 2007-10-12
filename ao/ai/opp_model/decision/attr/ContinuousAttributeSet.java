package ao.ai.opp_model.decision.attr;

import java.util.*;

/**
 * Percentile based continuouse (numeric) attribute set.
 */
public class ContinuousAttributeSet<T extends Comparable<T>>
        implements AttributeSet<T>
{
    //--------------------------------------------------------------------
    private final Object                       type;
    private       List<ContinuousAttribute<T>> attributes;

    private ContinuousAttribute<T> sorted[];
    private int                    from;
    private int                    to;
    private int                    percision;

    
    //--------------------------------------------------------------------
    public ContinuousAttributeSet(Object attributeType)
    {
        type       = attributeType;
        attributes = new ArrayList<ContinuousAttribute<T>>();
        from       = -1;
        to         = -1;
        percision  = -1;
    }

    private ContinuousAttributeSet(
            Object                 attributeType,
            ContinuousAttribute<T> inOrder[],
            int                    fromIndex,
            int                    toIndex,
            int                    numberFolds)
    {
        type       = attributeType;
        attributes = null;
        sorted     = inOrder;
        from       = fromIndex;
        to         = toIndex;
        percision  = numberFolds;
    }


    //--------------------------------------------------------------------
    // We assign a probability 1/2 (code length 1 bit) to the cut-point
    //  being at the median observed value.  Either quartile costs 3 bits
    //  (probability 1/8), octiles cost 5 bits (probability 1/32),
    //  and so on.  This coding scheme uses the fact that
    //      1/2 + 2/8 + 4/32 + ... = 1.
    public Collection<ContinuousAttributeSet<T>> partition(int folds)
    {
        sortAttributes();

        int size = valueCount();
        Collection<ContinuousAttributeSet<T>> parts =
                new ArrayList<ContinuousAttributeSet<T>>();
        for (int fold = 0; fold < folds; fold++)
        {
            int cost   = fold*2 + 1;
            int splits = 1 << (fold + 1);
            for (int split = 1; split < splits; split += 2)
            {
                double percentile = ((double) split) / splits;
                int    pivotDelta = (int)(size * percentile);
                if (pivotDelta < 3) break;
                
                parts.add(new ContinuousAttributeSet<T>(
                        type, sorted,
                        from, from + pivotDelta,
                        cost));
                parts.add(new ContinuousAttributeSet<T>(
                        type, sorted,
                        from + pivotDelta, to,
                        cost));
            }
        }
        return parts;
    }

    @SuppressWarnings("unchecked")
    private void sortAttributes()
    {
        if (sorted != null) return;

        sorted = attributes.toArray(
                        (ContinuousAttribute<T>[])
                                new Object[attributes.size()]);
        Arrays.sort(sorted);
        attributes = null;

        from      = 0;
        to        = sorted.length;
        percision = 0;
    }


    //--------------------------------------------------------------------
    public boolean isDescrete() {  return false;  }

    public double cutValueLength()
    {
        return percision;
    }

    public boolean typeEquals(AttributeSet<?> attributeSet)
    {
        return type().equals( attributeSet.type() );
    }
    public Object type()
    {
        return type;
    }


    //--------------------------------------------------------------------
    public Attribute<T> instanceOf(T value)
    {
        assert attributes != null;
        if (sorted != null) sorted = null;

        ContinuousAttribute<T> attr =
                new ContinuousAttribute<T>(this, value);
        attributes.add(attr);
        return attr;
    }


    //--------------------------------------------------------------------
    public int valueCount()
    {
        return (attributes != null)
                ? attributes.size()
                : to - from;
    }
}
