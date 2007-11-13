package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.attribute.Attribute;

/**
 *
 */
public abstract class Datum
{
    //--------------------------------------------------------------------
    private final Attribute ATTRIBUTE;


    //--------------------------------------------------------------------
    public Datum(Attribute attribute)
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
    public abstract boolean contains(Datum datum);
//    {
//        return false;
//    }
}
