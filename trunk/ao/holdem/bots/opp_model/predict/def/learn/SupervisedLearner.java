package ao.holdem.bots.opp_model.predict.def.learn;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.retro.RetroSet;

/**
 *
 */
public interface SupervisedLearner<C extends PredictionContext>
{
    public void add(RetroSet<C> data);

    public void learn(int iterations, int timeoutMillis);

    public Predictor<C> predictor();
}
