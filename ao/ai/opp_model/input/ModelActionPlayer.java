package ao.ai.opp_model.input;

import ao.ai.opp_model.decision.data.DataPool;
import ao.ai.opp_model.model.context.PlayerExampleSet;
import ao.ai.opp_model.model.data.HoldemExample;
import ao.ai.opp_model.model.data.HoldemContext;
import ao.ai.opp_model.model.data.DomainedExample;
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
    public ModelActionPlayer(
            HandHistory      history,
            PlayerExampleSet addTo,
            PlayerHandle     player,
            DataPool         attributePool,
            boolean          publishActions)
    {
        super(history, addTo, player, attributePool, publishActions);
    }


    //--------------------------------------------------------------------
    protected DomainedExample makeExampleOf(
                                StateManager  env,
                                HoldemContext ctx,
                                RealAction    act,
                                DataPool      pool)
    {
        return new HoldemExample(
                        ctx,
                        pool.fromEnum(act.toSimpleAction()));
    }
}
