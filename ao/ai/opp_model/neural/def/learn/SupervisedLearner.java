package ao.ai.opp_model.neural.def.learn;

import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.retro.RetroSet;

/**
 *
 */
public interface SupervisedLearner<C extends PredictionContext>
{
    public void add(RetroSet<C> data);

    public void learn(int iterations, int timeoutMillis);

    public Predictor<C> predictor();
}
