package ao.holdem.ai.ai.simple.kuhn.state;

import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.KuhnCard;
import ao.holdem.ai.ai.simple.kuhn.KuhnPlayer;

/**
 *
 */
public class KuhnSeat
{
    //--------------------------------------------------------------------
    private final KuhnPlayer PLAYER;
    private final KuhnCard   HOLE;


    //--------------------------------------------------------------------
    public KuhnSeat(KuhnPlayer player,
                    KuhnCard   hole)
    {
        PLAYER = player;
        HOLE   = hole;
    }


    //--------------------------------------------------------------------
    public KuhnAction act(KuhnState state)
    {
        return PLAYER.act(state, HOLE);
    }


    //--------------------------------------------------------------------
    public KuhnPlayer player()
    {
        return PLAYER;
    }

    public KuhnCard hole()
    {
        return HOLE;
    }
}
