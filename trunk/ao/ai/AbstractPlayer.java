package ao.ai;

import ao.holdem.history.state.HoldemState;
import ao.state.Context;
import ao.holdem.model.Hole;
import ao.holdem.model.Player;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.RealAction;

/**
 *
 */
public abstract class AbstractPlayer implements Player
{
    //--------------------------------------------------------------------
    public RealAction act(Context env)
    {
        HoldemState state = env.head();
        return act(env,
                   state,
                   env.cards().holeFor(
                           state.nextToAct().handle() ))
                .toRealAction( env.head() );
    }

    protected abstract EasyAction act(
            Context env, HoldemState state, Hole hole);


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
