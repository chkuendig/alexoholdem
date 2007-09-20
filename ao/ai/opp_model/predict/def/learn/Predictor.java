package ao.ai.opp_model.predict.def.learn;

import ao.ai.opp_model.predict.def.context.PredictionContext;
import ao.ai.opp_model.predict.def.observation.Observation;

/**
 *
 */
public interface Predictor<C extends PredictionContext>
{
    public Observation predict(C context);
}
