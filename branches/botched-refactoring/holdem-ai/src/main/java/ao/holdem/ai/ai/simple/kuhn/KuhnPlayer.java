package ao.holdem.ai.ai.simple.kuhn;

import ao.holdem.ai.ai.simple.kuhn.state.KuhnState;

/**
 *
 */
public interface KuhnPlayer
{
    public KuhnAction
            act(KuhnState state,
                KuhnCard  hole);

//    public void handEnded();
}
