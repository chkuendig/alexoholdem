package ao.holdem.bot;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.ChipStack;

import java.util.List;

/**
 *
 */
public abstract class AbstractPlayer implements Player
{
    //--------------------------------------------------------------------
    @Override
    public void observe(ActionState nextToActState)
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
