package ao.ai.opp_model.decision.classification.raw;

import ao.ai.opp_model.decision.classification.Histogram;
import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.classification.processed.Distribution;
import ao.ai.opp_model.decision.classification.processed.Frequency;
import ao.ai.opp_model.decision.input.processed.data.DataPool;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.data.State;
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
    @SuppressWarnings("unchecked")
    public Histogram toHistogram()
    {
        if (DELEGET instanceof Distribution) return null;

        Histogram<LocalDatum> localHist =
                ((Frequency) DELEGET).asHistogram();

        Histogram rawHist = new Histogram();
        for (LocalDatum clazz : localHist.classes())
        {
            rawHist.put(((State) clazz).state(),
                        localHist.countOf(clazz));
        }
        return rawHist;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return DELEGET.toString();
    }
}
