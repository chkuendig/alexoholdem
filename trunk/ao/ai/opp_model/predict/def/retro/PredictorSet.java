package ao.ai.opp_model.predict.def.retro;

import ao.ai.opp_model.predict.def.context.PredictionContext;
import ao.ai.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.ai.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.ai.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.ai.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.ai.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.ai.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.ai.opp_model.predict.def.learn.Predictor;
import ao.ai.opp_model.predict.def.observation.Observation;


/**
 *
 */
public class PredictorSet
{
    //--------------------------------------------------------------------
    private Predictor<HoleAwareFirstact> holeAwareFirstact;
    private Predictor<HoleBlindFirstact> holeBlindFirstact;

    private Predictor<HoleAwarePreflop>  holeAwarePreflop;
    private Predictor<HoleBlindPreflop>  holeBlindPreflop;

    private Predictor<HoleAwarePostflop> holeAwarePostflop;
    private Predictor<HoleBlindPostflop> holeBlindPostflop;


    //--------------------------------------------------------------------
    public PredictorSet(
            Predictor<HoleAwareFirstact> holeAwareFirstact,
            Predictor<HoleBlindFirstact> holeBlindFirstact,
            Predictor<HoleAwarePreflop>  holeAwarePreflop,
            Predictor<HoleBlindPreflop>  holeBlindPreflop,
            Predictor<HoleAwarePostflop> holeAwarePostflop,
            Predictor<HoleBlindPostflop> holeBlindPostflop)
    {
        this.holeAwareFirstact = holeAwareFirstact;
        this.holeBlindFirstact = holeBlindFirstact;
        this.holeAwarePreflop  = holeAwarePreflop;
        this.holeBlindPreflop  = holeBlindPreflop;
        this.holeAwarePostflop = holeAwarePostflop;
        this.holeBlindPostflop = holeBlindPostflop;
    }


    //--------------------------------------------------------------------
    public Predictor<HoleAwareFirstact> holeAwareFirstact()
    {
        return holeAwareFirstact;
    }
    public Predictor<HoleBlindFirstact> holeBlindFirstact()
    {
        return holeBlindFirstact;
    }
    public Predictor<HoleAwarePreflop> holeAwarePreflop()
    {
        return holeAwarePreflop;
    }
    public Predictor<HoleBlindPreflop> holeBlindPreflop()
    {
        return holeBlindPreflop;
    }
    public Predictor<HoleAwarePostflop> holeAwarePostflop()
    {
        return holeAwarePostflop;
    }
    public Predictor<HoleBlindPostflop> holeBlindPostflop()
    {
        return holeBlindPostflop;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Observation predict(PredictionContext context)
    {
        return ((Predictor) predictor(context.predictionType()))
                                .predict(context);
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public <T extends PredictionContext>
            Predictor<T> predictor(Class<T> type)
    {
        return  (Predictor<T>)(
                (type == HoleAwareFirstact.class)
                ? holeAwareFirstact :

                (type == HoleBlindFirstact.class)
                ? holeBlindFirstact :

                (type == HoleAwarePreflop.class)
                ? holeAwarePreflop :

                (type == HoleBlindPreflop.class)
                ? holeBlindPreflop :

                (type == HoleAwarePostflop.class)
                ? holeAwarePostflop :

                (type == HoleBlindPostflop.class)
                ? holeBlindPostflop :

                null);
    }
}
