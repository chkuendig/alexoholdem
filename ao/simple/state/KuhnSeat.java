package ao.simple.state;

import ao.simple.KuhnAction;
import ao.simple.KuhnCard;
import ao.simple.KuhnPlayer;

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
