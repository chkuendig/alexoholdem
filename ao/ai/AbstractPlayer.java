package ao.ai;

import ao.holdem.engine.Player;

/**
 *
 */
public abstract class AbstractPlayer implements Player
{
    //--------------------------------------------------------------------
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
