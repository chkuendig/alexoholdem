package ao.ai;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.State;

/**
 *
 */
public abstract class AbstractPlayer implements Player
{
    //--------------------------------------------------------------------
    @Override
    public void observe(State nextToActState)
    {
        // ignore
    }


    //--------------------------------------------------------------------
    @Override
    public boolean hasQuit()
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
