package ao.ai.monte_carlo;

import ao.ai.opp_model.decision.classification.Histogram;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;

/**
 *
 */
public class HandApproximator
{
    //--------------------------------------------------------------------
    //@Inject PredictorService predictor;
    private PredictorService predictor;


    //--------------------------------------------------------------------
    public HandApproximator(PredictorService predictorService)
    {
        predictor = predictorService;
    }


    //--------------------------------------------------------------------
    public Histogram<PlayerHandle>
            aproximate(HandHistory atHeadOf)
    {
        
        return null;
    }
}
