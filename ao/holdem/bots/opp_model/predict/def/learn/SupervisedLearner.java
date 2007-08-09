package ao.holdem.bots.opp_model.predict.def.learn;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;

import java.util.List;

/**
 *
 */
public interface SupervisedLearner<C extends PredictionContext,
                                   O extends Observation>
{
    public void add(List<Retrodiction<C, O>> data);

//    public void learn(int iterations, int timeoutMillis);
    
    public O    predict(C context);
}
