package ao.ai.opp_model.neural.def.retro;

import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.observation.Observation;

/**
 *
 */
public class Retrodiction<C extends PredictionContext>
        implements PredictionContext,
                   Observation
{
    //--------------------------------------------------------------------
    private C           CONTEXT_DELEGET;
    private Observation OBSERVATION_DELEGET;


    //--------------------------------------------------------------------
//    public Retrodiction() {}

    public Retrodiction(
            C           context,
            Observation observation)
    {
        CONTEXT_DELEGET     = context;
        OBSERVATION_DELEGET = observation;
    }


    //--------------------------------------------------------------------
//    public PredictionContext getContext()
//    {
//        return CONTEXT_DELEGET;
//    }
//    public void setContext(PredictionContext context)
//    {
//        this.CONTEXT_DELEGET = context;
//    }
//
//    public HoldemObservation getObservation()
//    {
//        return OBSERVATION_DELEGET;
//    }
//    public void setObservation(HoldemObservation observation)
//    {
//        this.OBSERVATION_DELEGET = observation;
//    }


    //--------------------------------------------------------------------

    public Class<? extends PredictionContext> predictionType()
    {
        return CONTEXT_DELEGET.predictionType();
    }

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


    //--------------------------------------------------------------------
    public  Retrodiction<C> asObserved(Observation observation)
    {
        return new Retrodiction<C>(CONTEXT_DELEGET, observation);
    }
}
