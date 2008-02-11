package ao.ai.supervised.decision.input.processed.data;

import ao.ai.supervised.decision.input.processed.attribute.Attribute;
import ao.ai.supervised.decision.input.processed.attribute.Continuous;
import ao.ai.supervised.decision.input.processed.attribute.SortedList;

/**
 * Note: perhaps this class should be internal to Continuous?
 */
public class ValueRange extends LocalDatum
{
    //--------------------------------------------------------------------
    private SortedList<Value> inOrder;
    private double            from;
    private double            to;
    private boolean           roundToUp;


    //--------------------------------------------------------------------
    public ValueRange(
            Continuous        continuous,
            SortedList<Value> uniques,
            double            fromPercentile,
            double            toPercentile,
            boolean           roundToUpOrDown)
    {
        super( continuous );

        inOrder   = uniques;
        from      = fromPercentile;
        to        = toPercentile;
        roundToUp = roundToUpOrDown;
    }


    //--------------------------------------------------------------------
    public Attribute asAttribute()
    {
        return new Continuous(attribute().type(),
                              inOrder, from, to);
    }

    
    //--------------------------------------------------------------------
    public boolean contains(LocalDatum datum)
    {
        return datum instanceof Value &&
                (roundToUp)
                 ? ((Value) datum).isBetween(     from(), to() )
                 : ((Value) datum).isBetweenOpen( from(), to() );
    }

    private Value from()
    {
        return inOrder.getPercentileDown(from);
    }
    private Value to()
    {
        return (roundToUp)
                ? inOrder.getPercentileUp(to)
                : inOrder.getPercentileDown(to);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + from() +
                     " .. " +
                     to()   + (roundToUp ? "]" : ")");
    }
}
