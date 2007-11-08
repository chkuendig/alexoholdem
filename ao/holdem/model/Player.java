package ao.holdem.model;

import ao.holdem.model.act.RealAction;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.util.Map;

/**
 *
 */
public interface Player
{
    public RealAction act(StateManager env);

    public boolean shiftQuitAction();

    public void handEnded(Map<PlayerHandle, Money> deltas);
}
