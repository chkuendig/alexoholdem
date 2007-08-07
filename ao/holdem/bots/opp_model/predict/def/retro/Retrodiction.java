package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;

/**
 *
 */
public class Retrodiction
        implements PredictionContext,
                   Observation
{
    //--------------------------------------------------------------------
    private final PredictionContext CONTEXT_DELEGET;
    private final Observation       OBSERVATION_DELEGET;


    //--------------------------------------------------------------------
    public Retrodiction(
            PredictionContext context,
            Observation       observation)
    {
        CONTEXT_DELEGET     = context;
        OBSERVATION_DELEGET = observation;
    }


    //--------------------------------------------------------------------
    public Class<? extends PredictionContext> contextClass()
    {
        return CONTEXT_DELEGET.getClass();
    }


    //--------------------------------------------------------------------
    public double[] neuralInput()
    {
        return CONTEXT_DELEGET.neuralInput();
    }

    public int neuralInputSize()
    {
        return CONTEXT_DELEGET.neuralInputSize();
    }


    //--------------------------------------------------------------------
    public double[] neuralOutput()
    {
        return OBSERVATION_DELEGET.neuralOutput();
    }

    public int neuralOutputSize()
    {
        return OBSERVATION_DELEGET.neuralOutputSize();
    }
}
