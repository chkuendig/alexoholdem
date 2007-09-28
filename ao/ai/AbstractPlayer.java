package ao.ai;

import ao.state.HandState;
import ao.state.StateManager;
import ao.holdem.model.card.Hole;
import ao.holdem.model.Player;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.RealAction;

/**
 *
 */
public abstract class AbstractPlayer implements Player
{
    //--------------------------------------------------------------------
    public RealAction act(StateManager env)
    {
        HandState state = env.head();
        return act(env,
                   state,
                   env.cards().holeFor(
                           state.nextToAct().handle() ))
                .toRealAction( env.head() );
    }

    protected abstract EasyAction act(
            StateManager env, HandState state, Hole hole);


    //--------------------------------------------------------------------
    public boolean shiftQuitAction()
    {
        return false;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
