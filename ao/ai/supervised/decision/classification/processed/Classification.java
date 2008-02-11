package ao.ai.supervised.decision.classification.processed;

import ao.ai.supervised.decision.input.processed.data.LocalDatum;

/**
 *
 */
public interface Classification
{         
    //--------------------------------------------------------------------
    public void add(LocalDatum datum);

    public double transmissionCost(double alpha);

    public double probabilityOf(LocalDatum datum);


    //--------------------------------------------------------------------
    public int sampleSize();
}
