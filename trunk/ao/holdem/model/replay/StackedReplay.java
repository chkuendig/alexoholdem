package ao.holdem.model.replay;

import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;

import java.util.Map;

/**
 *
 */
public class StackedReplay
{
    //--------------------------------------------------------------------
    private final Replay             REPLAY;
    private final Map<Avatar, Chips> DELTAS;


    //--------------------------------------------------------------------
    public StackedReplay(Replay             replay,
                         Map<Avatar, Chips> deltas)
    {
        REPLAY = replay;
        DELTAS = deltas;
    }


    //--------------------------------------------------------------------
    public Replay replay()
    {
        return REPLAY;
    }

    public Map<Avatar, Chips> deltas()
    {
        return DELTAS;
    }
}
