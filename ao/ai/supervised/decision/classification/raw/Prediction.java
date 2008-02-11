package ao.ai.supervised.decision.classification.raw;

import ao.ai.supervised.decision.classification.RealHistogram;
import ao.ai.supervised.decision.classification.processed.Classification;
import ao.ai.supervised.decision.classification.processed.Distribution;
import ao.ai.supervised.decision.classification.processed.Frequency;
import ao.ai.supervised.decision.input.processed.data.DataPool;
import ao.ai.supervised.decision.input.processed.data.LocalDatum;
import ao.ai.supervised.decision.input.processed.data.State;
import ao.ai.supervised.decision.input.raw.example.Datum;

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
    public RealHistogram toRealHistogram()
    {
        if (DELEGET instanceof Distribution) return null;

        RealHistogram<LocalDatum> localHist =
                ((Frequency) DELEGET).asRealHistogram();

        RealHistogram rawHist = new RealHistogram(sampleSize());
        for (LocalDatum clazz : localHist.classes())
        {
            rawHist.add(((State) clazz).state(),
                        localHist.valueOf(clazz));
        }
        return rawHist;
    }

    public int sampleSize()
    {
        return DELEGET.sampleSize();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return DELEGET.toString();
    }
}
