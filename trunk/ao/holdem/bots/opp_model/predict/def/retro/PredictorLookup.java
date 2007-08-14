package ao.holdem.bots.opp_model.predict.def.retro;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PredictorLookup
{
    //--------------------------------------------------------------------
    private Map<Serializable, Predictor> PREDICTORS;


    //--------------------------------------------------------------------
    public PredictorLookup()
    {
        PREDICTORS = new HashMap<Serializable, Predictor>();
    }


    //--------------------------------------------------------------------
    public Predictor predictor(Serializable key)
    {
        Predictor predictor = PREDICTORS.get(key);
        if (predictor == null)
        {
            predictor = new Predictor();
            PREDICTORS.put(key, predictor);
        }
        return predictor;
    }
//    public Future<Predictor> train(
//            Serializable key,
//            RetroSet<O>  retrodictions)
//    {
//
//
//        for (Class<? extends PredictionContext> context :
//                retrodictions.contexts())
//        {
//            for (Retrodiction<?, O> retro:
//                    retrodictions.cases(context))
//            {
////                predictor
//            }
//        }
//
//
//        return null;
//    }
    
    public void reset(Serializable key)
    {
        PREDICTORS.remove( key );
    }
}
