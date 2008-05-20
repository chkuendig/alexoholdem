package ao.simple.player;

import ao.simple.KuhnAction;
import ao.simple.KuhnCard;
import ao.simple.KuhnPlayer;
import ao.simple.state.KuhnState;
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
