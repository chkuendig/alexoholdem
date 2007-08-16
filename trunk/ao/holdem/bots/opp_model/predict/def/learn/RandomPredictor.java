package ao.holdem.bots.opp_model.predict.def.learn;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.util.rand.Rand;

/**
 *
 */
public class RandomPredictor<C extends PredictionContext>
        implements Predictor<C>
{
    public HoldemObservation predict(C context)
    {
        return new HoldemObservation(
                new MixedAction(Rand.nextDouble(),
                                Rand.nextDouble(),
                                Rand.nextDouble()));
    }
}
