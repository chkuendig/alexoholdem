package ao.holdem.model;

import ao.holdem.model.act.RealAction;
import ao.state.StateManager;

/**
 *
 */
public interface Player
{
    public RealAction act(StateManager env);

    public boolean shiftQuitAction();
}
