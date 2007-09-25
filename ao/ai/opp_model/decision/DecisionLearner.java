package ao.ai.opp_model.decision;

import ao.ai.opp_model.decision.attr.AttributePool;
import ao.ai.opp_model.decision.data.DataSet;

/**
 *
 */
public interface DecisionLearner<T>
        extends Predictor<T>
{
    public AttributePool pool();

    public void train(DataSet<T> ds);
}
