package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.card.Hole;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.HandState;
import ao.state.StateManager;
import ao.stats.Statistic;

/**
 * move in according to how we predict a player to move.
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
        Context   ctx  = stat.nextActContext();

        RealHistogram<SimpleAction> hist =
                predictor.predictAction(handle, ctx);

        MixedAction  mixedAct = MixedAction.fromHistogram( hist );
        SimpleAction randAct  = mixedAct.weightedRandom();

        return randAct == SimpleAction.FOLD
                ? EasyAction.CHECK_OR_FOLD
                : randAct == SimpleAction.CALL
                  ? EasyAction.CHECK_OR_CALL
                  : EasyAction.RAISE_OR_CALL;
    }
}
