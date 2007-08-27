package ao.decision;

import ao.decision.data.DataSet;

/**
 *
 */
public interface DecisionLearner<T>
        extends Predictor<T>
{
    void train(DataSet<T> ds);
}
