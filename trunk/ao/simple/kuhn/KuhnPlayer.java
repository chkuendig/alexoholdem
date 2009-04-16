package ao.simple.kuhn;

import ao.simple.kuhn.state.KuhnState;

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
