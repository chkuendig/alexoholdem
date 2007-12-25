package ao.ai.opp_model.input;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.Datum;
import ao.ai.opp_model.decision.input.raw.example.Example;
import ao.holdem.model.act.RealAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

/**
 * A player that extracts action examples from a HandHistory
 */
public class ModelActionPlayer extends InputPlayer
{
    //--------------------------------------------------------------------
    public static class Factory
                            implements InputPlayerFactory
    {
        public InputPlayer newInstance(
                HandHistory  history,
                Classifier   addTo,
                PlayerHandle player,
                boolean      publishActions)
        {
            return new ModelActionPlayer(
                            history, addTo, player, publishActions);
        }
    }


    //--------------------------------------------------------------------
    public ModelActionPlayer(
            HandHistory  history,
            Classifier addTo,
            PlayerHandle player,
            boolean      publishActions)
    {
        super(history, addTo, player, publishActions);
    }


    //--------------------------------------------------------------------
    protected Example makeExampleOf(
            StateManager env,
            Context      ctx,
            RealAction   act)
    {
        return ctx.withTarget(
                new Datum(act.toSimpleAction()) );
    }
}
