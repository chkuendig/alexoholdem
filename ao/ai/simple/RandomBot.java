package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.state.HoldemState;
import ao.holdem.model.Hole;
import ao.holdem.model.act.EasyAction;
import ao.state.Context;
import ao.util.rand.Rand;


/**
 *
 */
public class RandomBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    protected EasyAction act(Context env, HoldemState state, Hole hole)
    {
        return Rand.fromArray( EasyAction.values() );
    }
}
