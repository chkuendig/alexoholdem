package ao.simple.kuhn.state;

import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;

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
        return PLAYER.act(HOLE, state);
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
