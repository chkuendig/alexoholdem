package ao.holdem.bots.opp_model.predict.def.learn;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;

/**
 *
 */
public interface Predictor<C extends PredictionContext>
{
    public HoldemObservation predict(C context);
}
