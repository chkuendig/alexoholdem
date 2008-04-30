package ao.simple.player;

import ao.simple.KuhnAction;
import ao.simple.KuhnCard;
import ao.simple.KuhnPlayer;
import ao.simple.state.KuhnState;

/**
 *
 */
public class AlwaysBet implements KuhnPlayer
{
    public KuhnAction act(KuhnCard hole, KuhnState state)
    {
        return KuhnAction.BET;
    }
}
