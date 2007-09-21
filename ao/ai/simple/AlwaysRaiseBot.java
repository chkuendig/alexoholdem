package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.state.HandState;
import ao.state.StateManager;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.Hole;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    protected EasyAction act(StateManager env, HandState state, Hole hole)
    {
        return EasyAction.RAISE_OR_CALL;
    }
}
