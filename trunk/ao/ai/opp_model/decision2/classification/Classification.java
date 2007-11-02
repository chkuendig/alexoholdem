package ao.ai.opp_model.decision2.classification;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.data.Datum;

/**
 *
 */
public abstract class Classification
{
    //--------------------------------------------------------------------
    public static Classification forType(Attribute attribute)
    {
        if (attribute.isSingleUse())
        {
            return new Histogram();
        }
        else
        {
            return new Distribution();
        }
    }


    //--------------------------------------------------------------------
    public abstract void add(Datum datum);

    public abstract double transmissionCost(double alpha);
}
