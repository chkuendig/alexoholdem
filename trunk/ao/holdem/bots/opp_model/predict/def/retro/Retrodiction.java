package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;

/**
 *
 */
public class Retrodiction<C extends PredictionContext>
        implements PredictionContext,
                   Observation
{
    //--------------------------------------------------------------------
    private PredictionContext CONTEXT_DELEGET;
    private HoldemObservation OBSERVATION_DELEGET;


    //--------------------------------------------------------------------
//    public Retrodiction() {}

    public Retrodiction(
            C                 context,
            HoldemObservation observation)
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
}