package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.state.HandState;
import ao.holdem.engine.state.StateManager;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(HandHistory history) {}

    
    //--------------------------------------------------------------------
    protected EasyAction act(StateManager env, HandState state, Hole hole)
    {
        return EasyAction.RAISE_OR_CALL;
    }
}
