package ao.holdem.model;

import ao.holdem.model.act.RealAction;
import ao.state.Context;

/**
 *
 */
public interface Player
{
    public RealAction act(Context env);
}
