package ao.ai.opp_model.decision.classification.processed;

import ao.ai.opp_model.decision.input.processed.data.LocalDatum;

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
