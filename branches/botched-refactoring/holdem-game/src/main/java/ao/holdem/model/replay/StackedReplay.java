package ao.holdem.model.replay;

import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;

import java.util.Map;

/**
 *
 */
public class StackedReplay
{
    //--------------------------------------------------------------------
    private final Replay                 REPLAY;
    private final Map<Avatar, ChipStack> DELTAS;


    //--------------------------------------------------------------------
    public StackedReplay(
            Replay                 replay,
            Map<Avatar, ChipStack> deltas)
    {
        REPLAY = replay;
        DELTAS = deltas;
    }


    //--------------------------------------------------------------------
    public Replay replay()
    {
        return REPLAY;
    }

    public Map<Avatar, ChipStack> deltas()
    {
        return DELTAS;
    }
}
