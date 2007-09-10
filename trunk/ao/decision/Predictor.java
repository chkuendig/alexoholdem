package ao.decision;

import ao.decision.data.Context;
import ao.decision.data.Histogram;

/**
 *
 */
public interface Predictor<T>
{
    Histogram<T> predict(Context context);
}
