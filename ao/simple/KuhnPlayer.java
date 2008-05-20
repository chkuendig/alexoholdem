package ao.simple;

import ao.simple.state.KuhnState;

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
