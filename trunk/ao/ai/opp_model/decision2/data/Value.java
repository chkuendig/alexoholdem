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
    public Value corruptUpwards()
    {
//        VALUE += Double.MIN_VALUE;
        return new Value(attribute(), VALUE + Double.MIN_VALUE);
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
    public Value toLeast()
    {
        return new Value(attribute(), -1000000000000D);
    }

    public Value toMost()
    {
        return new Value(attribute(),  1000000000000D);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf( VALUE );
    }

    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        return !(o == null || getClass() != o.getClass()) &&
                compareTo((Value) o) == 0;
    }

    public int hashCode()
    {
        long temp = VALUE != +0.0d ? Double.doubleToLongBits(VALUE) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}
