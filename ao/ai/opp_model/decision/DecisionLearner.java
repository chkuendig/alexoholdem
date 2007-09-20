package ao.ai.opp_model.decision;

import ao.ai.opp_model.decision.data.DataSet;

/**
 *
 */
public interface DecisionLearner<T>
        extends Predictor<T>
{
    void train(DataSet<T> ds);
}
