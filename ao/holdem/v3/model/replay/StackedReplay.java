package ao.holdem.v3.model.replay;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.Chips;

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
