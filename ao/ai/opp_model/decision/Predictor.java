package ao.ai.opp_model.decision;

import ao.ai.opp_model.decision.data.Context;
import ao.ai.opp_model.decision.data.Histogram;

/**
 *
 */
public interface Predictor<T>
{
    Histogram<T> predict(Context context);
}
