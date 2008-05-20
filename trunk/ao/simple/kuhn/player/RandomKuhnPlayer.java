package ao.simple.kuhn.player;

import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.state.KuhnState;
import ao.util.rand.Rand;

/**
 *
 */
public class RandomKuhnPlayer implements KuhnPlayer
{
    public KuhnAction act(
            KuhnCard hole,
            KuhnState state)
    {
        return Rand.nextBoolean()
                ? KuhnAction.PASS
                : KuhnAction.BET;
    }

    public void handEnded() {}
}
