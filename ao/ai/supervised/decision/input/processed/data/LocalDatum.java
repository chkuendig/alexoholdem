package ao.ai.supervised.decision.input.processed.data;

import ao.ai.supervised.decision.input.processed.attribute.Attribute;

/**
 *
 */
public abstract class LocalDatum
{
    //--------------------------------------------------------------------
    private final Attribute ATTRIBUTE;


    //--------------------------------------------------------------------
    public LocalDatum(Attribute attribute)
    {
        ATTRIBUTE = attribute;
    }


    //--------------------------------------------------------------------
    public Attribute attribute()
    {
        return ATTRIBUTE;
    }


    //--------------------------------------------------------------------
    // for state/value, true if the given datum is equal
    // for ValueRange true if the given datum is in range.
    public abstract boolean contains(LocalDatum datum);
//    {
//        return false;
//    }
}
