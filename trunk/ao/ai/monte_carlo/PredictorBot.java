package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.StateManager;
import ao.stats.Statistic;

/**
 *
 */
public class PredictorBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private PlayerHandle     handle;
    private PredictorService predictor;



    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}


    //--------------------------------------------------------------------
    public PredictorBot(PlayerHandle     playerHandle,
                        PredictorService predictorService)
    {
        handle    = playerHandle;
        predictor = predictorService;
    }


    //--------------------------------------------------------------------
    protected EasyAction act(
            StateManager env,
            HandState    state,
            Hole         hole)
    {
        Statistic stat = env.stats().forPlayer(handle.getId());


//        HoldemContext ctx =
//                env.stats().forPlayer(handle.getId())
//                        .nextActContext(pool);
//
//        predictor.predictAction(handle);


        return null;
    }
}
