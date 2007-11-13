package ao.ai.opp_model.decision.classification;

import ao.ai.opp_model.decision.data.Datum;

import java.util.Collection;
import java.util.ArrayList;

/**
 *
 */
public class Distribution extends Classification
{
    //--------------------------------------------------------------------
    private Collection<Datum> data;

    //--------------------------------------------------------------------
    public Distribution()
    {
        data = new ArrayList<Datum>();
    }


    //--------------------------------------------------------------------
    public void add(Datum datum)
    {

    }

    public double transmissionCost(double alpha)
    {
        return 0;
    }

    public double probabilityOf(Datum datum)
    {
        return 0;
    }
}
