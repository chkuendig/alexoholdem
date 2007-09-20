package ao.ai.opp_model.neural.def.learn;

import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.observation.Observation;
import ao.ai.opp_model.neural.def.observation.ObservationImpl;
import ao.util.rand.Rand;

/**
 *
 */
public class RandomPredictor<C extends PredictionContext>
        implements Predictor<C>
{
    private final int observationCount;

    public RandomPredictor(int count)
    {
        observationCount = count;
    }

    public Observation predict(C context)
    {
        double ob[] = new double[ observationCount ];
        for (int i = 0; i < ob.length; i++)
        {
            ob[i] = Rand.nextDouble();
        }

        return new ObservationImpl(ob);
    }
}
