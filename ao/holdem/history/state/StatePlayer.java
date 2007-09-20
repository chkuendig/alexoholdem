package ao.holdem.history.state;

import ao.holdem.model.act.RealAction;

/**
 *
 */
public interface StatePlayer
{
    public RealAction act(RunningState env);
}
