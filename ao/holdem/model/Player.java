package ao.holdem.model;

import ao.holdem.model.act.RealAction;
import ao.holdem.engine.persist.HandHistory;
import ao.holdem.engine.state.StateManager;

/**
 *
 */
public interface Player
{
    public RealAction act(StateManager env);

    public boolean shiftQuitAction();

    public void handEnded(HandHistory history);
}
