package ao.holdem.bot;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;

import java.util.List;
import java.util.Map;

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


    @Override
    public void handEnded(List<ChipStack> deltas)
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
