package ao.ai.opp_model.decision.classification.processed;

import ao.ai.opp_model.decision.input.processed.data.LocalDatum;

/**
 *
 */
public abstract class Classification
{
    //--------------------------------------------------------------------
    public abstract void add(LocalDatum datum);

    public abstract double transmissionCost(double alpha);

    public abstract double probabilityOf(LocalDatum datum);
}
