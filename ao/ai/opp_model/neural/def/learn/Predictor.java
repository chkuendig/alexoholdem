package ao.ai.opp_model.neural.def.learn;

import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.observation.Observation;

/**
 *
 */
public interface Predictor<C extends PredictionContext>
{
    public Observation predict(C context);
}
