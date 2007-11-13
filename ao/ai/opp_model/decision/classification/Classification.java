package ao.ai.opp_model.decision.classification;

import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.data.Datum;

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

    public abstract double probabilityOf(Datum datum);
}
