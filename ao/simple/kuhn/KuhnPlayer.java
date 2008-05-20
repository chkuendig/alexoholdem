package ao.simple.kuhn;

import ao.simple.kuhn.state.KuhnState;

/**
 *
 */
public interface KuhnPlayer
{
    public KuhnAction
            act(KuhnCard  hole,
                KuhnState state);

//    public void handEnded();
}
