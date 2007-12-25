package ao.ai.opp_model.decision.classification.raw;

import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.input.processed.data.DataPool;
import ao.ai.opp_model.decision.input.raw.example.Datum;

/**
 *
 */
public class Prediction
{
    //--------------------------------------------------------------------
    private final Classification DELEGET;
    private final DataPool       POOL;


    //--------------------------------------------------------------------
    public Prediction(Classification classification,
                      DataPool       pool)
    {
        DELEGET = classification;
        POOL    = pool;
    }


    //--------------------------------------------------------------------
    public double probabilityOf(Datum datum)
    {
        return DELEGET.probabilityOf( datum.toDatum(POOL) );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return DELEGET.toString();
    }
}
