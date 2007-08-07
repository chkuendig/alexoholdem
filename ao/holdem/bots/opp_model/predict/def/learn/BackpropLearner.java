package ao.holdem.bots.opp_model.predict.def.learn;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;

import java.util.List;

/**
 *
 */
public class BackpropLearner<C extends PredictionContext,
                             O extends Observation>
        implements SupervisedLearner<C, O>
{
    //--------------------------------------------------------------------
    private final int HORIZON;


    //--------------------------------------------------------------------
    public BackpropLearner(int horizon)
    {
        HORIZON = horizon;
    }

    
    //--------------------------------------------------------------------
    public void add(List<Retrodiction<C, O>> data)
    {

    }

    public O predict(C context)
    {
        return null;
    }
}
