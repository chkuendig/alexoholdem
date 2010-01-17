package ao.simple.kuhn.player;

import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.state.KuhnState;
import ao.util.math.rand.Rand;

/**
 *
 */
public class RandomKuhnPlayer implements KuhnPlayer
{
    //--------------------------------------------------------------------
    public void handEnded() {}


    //--------------------------------------------------------------------
    public KuhnAction act(
            KuhnState state,
            KuhnCard  hole)
    {
        return Rand.nextBoolean()
                ? KuhnAction.PASS
                : KuhnAction.BET;
    }
}
