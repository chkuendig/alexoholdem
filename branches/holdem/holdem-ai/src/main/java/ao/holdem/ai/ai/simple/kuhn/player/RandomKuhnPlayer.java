package ao.holdem.ai.ai.simple.kuhn.player;

import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.KuhnCard;
import ao.holdem.ai.ai.simple.kuhn.KuhnPlayer;
import ao.holdem.ai.ai.simple.kuhn.state.KuhnState;
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
