package ao.ai.opp_model.decision.attr;

import java.util.*;

/**
 * Percentile based continuouse (numeric) attribute set.
 */
public class ContinuousAttributeSet<T extends Comparable<T>>
        implements AttributeSet<T>
{
    //--------------------------------------------------------------------
    private final Object                            type;
    private       SortedSet<ContinuousAttribute<T>> attributes;
    private       ContinuousAttribute<T>            sorted[];
    private final int                               percision;

    
    //--------------------------------------------------------------------
    public ContinuousAttributeSet(Object attributeType)
    {
        type       = attributeType;
        attributes = new TreeSet<ContinuousAttribute<T>>();
        percision  = 0;
    }

    private ContinuousAttributeSet(
            Object                 attributeType,
            ContinuousAttribute<T> inOrder[],
            int                    percentilePercision)
    {
        type       = attributeType;
        attributes = null;
        sorted     = inOrder;
        percision  = percentilePercision;
    }


    //--------------------------------------------------------------------
    public Collection<ContinuousAttributeSet<T>> partition(int folds)
    {
        for (int fold = 0; fold < folds; fold++)
        {
            double percentSpan = 1.0 / Math.pow(2, fold + 1);
        }

        return null;
    }


    //--------------------------------------------------------------------
    public boolean isDescrete() {  return false;  }

    /// todo: calulate this!!
    public double cutValueLength()
    {
        return 0;
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
    public Collection<ContinuousAttribute<T>> values()
    {
        return attributes;
    }
}
