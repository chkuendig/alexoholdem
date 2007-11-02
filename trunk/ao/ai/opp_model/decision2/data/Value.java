package ao.ai.opp_model.decision2.data;

import ao.ai.opp_model.decision2.attribute.Attribute;

/**
 *
 */
public class Value
        extends Datum
        implements Comparable<Value>
{
    //--------------------------------------------------------------------
    private final double VALUE;


    //--------------------------------------------------------------------
    public Value(Attribute attribute, double value)
    {
        super(attribute);
        VALUE = value;
    }


    //--------------------------------------------------------------------
    public boolean isBetween(Value lesser, Value greater)
    {
        return lesser.VALUE <= VALUE && VALUE <= greater.VALUE;
    }


    //--------------------------------------------------------------------
    public boolean contains(Datum datum)
    {
        return datum instanceof Value &&
                VALUE == ((Value) datum).VALUE;
    }


    //--------------------------------------------------------------------
    public int compareTo(Value o)
    {
        return Double.compare(VALUE, o.VALUE);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf( VALUE );
    }
}
