package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.history.state.HoldemState;
import ao.state.Context;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.Hole;

/**
 *
 */
public class AlwaysRaiseBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    protected EasyAction act(Context env, HoldemState state, Hole hole)
    {
        return EasyAction.RAISE_OR_CALL;
    }
}
