package ao.ai.monte_carlo;

import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.PlayerHandle;
import ao.state.StateManager;
import ao.stats.Statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * move in according to how we predict a player to move.
 */
public class BotPredictor
{
    //--------------------------------------------------------------------
    private PlayerHandle     handle;
    private PredictorService predictor;
    private List<Context>    contexts;
    private boolean          isUnfolded;
    private boolean          hasActed;


    //--------------------------------------------------------------------
    public BotPredictor(PlayerHandle     playerHandle,
                        PredictorService predictorService)
    {
        handle    = playerHandle;
        predictor = predictorService;

        contexts   = new ArrayList<Context>();
        isUnfolded = true;
        hasActed   = false;
    }


    //--------------------------------------------------------------------
    public MixedAction act(StateManager env)
    {
        Statistic stat = env.stats().forPlayer(handle.getId());
        Context   ctx  = stat.nextActContext();
        contexts.add(ctx);

        RealHistogram<SimpleAction> hist =
                predictor.predictAction(handle, ctx);

        hasActed = true;
        return MixedAction.fromHistogram( hist );
    }

    public void took(SimpleAction action)
    {
        if (action == SimpleAction.FOLD)
        {
            isUnfolded = false;
        }
    }


    //--------------------------------------------------------------------
    public boolean isUnfolded()
    {
        return isUnfolded;
    }

    public boolean hasActed()
    {
        return hasActed;
    }

    public List<Context> contexts()
    {
        return contexts;
    }

    public Context lastContext()
    {
        return contexts.get( contexts.size() - 1 );
    }
}
