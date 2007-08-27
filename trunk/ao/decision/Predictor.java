package ao.decision;

import ao.decision.data.Histogram;
import ao.decision.data.Context;

/**
 *
 */
public interface Predictor<T>
{
    Histogram<T> predict(Context context);
}
