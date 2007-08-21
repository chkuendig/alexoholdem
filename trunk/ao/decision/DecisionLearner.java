package ao.decision;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.retro.RetroSet;

/**
 *
 */
public class DecisionLearner<C extends PredictionContext>
        implements SupervisedLearner<C>
{
    
    public void add(RetroSet<C> data)
    {

    }


    public void learn(int iterations, int timeoutMillis)
    {

    }


    public Predictor<C> predictor()
    {
        return null;
    }
}
