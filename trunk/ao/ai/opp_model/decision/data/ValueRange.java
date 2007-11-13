package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.attribute.Continuous;

/**
 *
 */
public class ValueRange extends Datum
{
    //--------------------------------------------------------------------
    private Value[] inOrder;
    private int     from;
    private int     to;


    //--------------------------------------------------------------------
    public ValueRange(
            Continuous continuous,
            Value[]    sorted,
            int        fromIndex,
            int        toIndex)
    {
        super( continuous );

        inOrder = sorted;
        from    = fromIndex;
        to      = toIndex;
    }


    //--------------------------------------------------------------------
    public Attribute asAttribute()
    {
        return new Continuous(attribute().type(),
                              inOrder, from, to);
    }

    
    //--------------------------------------------------------------------
    public boolean contains(Datum datum)
    {
        return datum instanceof Value &&
                ((Value) datum).isBetween(inOrder[from],
                                          inOrder[to - 1]);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "[" + inOrder[from] + " .. " + inOrder[to - 1] + ")"; 
    }
}
