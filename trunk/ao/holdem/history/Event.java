package ao.holdem.history;

import ao.holdem.def.state.env.TakenAction;

/**
 *
 */
public class Event
{
    public PlayerHandle2 player()
    {
        return null;
    }

    public TakenAction action()
    {
        return null;
    }

    // position of player (starting at 1, in order of cards received)
    public int position()
    {
        return 0;
    }
}
