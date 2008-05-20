package ao.simple.kuhn.player;

import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.KuhnCard;
import ao.simple.kuhn.KuhnPlayer;
import ao.simple.kuhn.state.KuhnState;

/**
 *
 */
public class AlwaysPass implements KuhnPlayer
{
    public KuhnAction act(KuhnCard hole, KuhnState state)
    {
        return KuhnAction.PASS;
    }

    public void handEnded() {}
}
