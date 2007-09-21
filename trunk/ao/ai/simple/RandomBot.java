package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.state.HandState;
import ao.holdem.model.Hole;
import ao.holdem.model.act.EasyAction;
import ao.state.StateManager;
import ao.util.rand.Rand;


/**
 *
 */
public class RandomBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    protected EasyAction act(StateManager env, HandState state, Hole hole)
    {
        return Rand.fromArray( EasyAction.values() );
    }
}
