package ao.holdem.history.state;

import ao.holdem.def.state.env.RealAction;

/**
 *
 */
public interface StatePlayer
{
    public RealAction act(CumulativeState state);
}
