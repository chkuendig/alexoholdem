package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.state.HandState;
import ao.state.StateManager;

/**
 *
 */
public class UctBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
//    @Inject HandApproximator handApprox;
//    @Inject PredictorService predictor;

    private HandApproximator handApprox;
    private PredictorService predictor;

    
    //--------------------------------------------------------------------
    public UctBot()
    {
        predictor  = new PredictorService();
        handApprox = new HandApproximator( predictor );
    }


    //--------------------------------------------------------------------
    protected EasyAction act(
            StateManager env,
            HandState    state,
            Hole         hole)
    {



        return null;
    }

    
    //--------------------------------------------------------------------
    public void handEnded(HandHistory history)
    {
        predictor.add( history );
    }
}
