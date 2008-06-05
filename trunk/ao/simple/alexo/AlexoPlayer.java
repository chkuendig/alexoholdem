package ao.simple.alexo;

import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoRound;
import ao.simple.alexo.state.AlexoState;

/**
 *
 */
public interface AlexoPlayer
{
    public void handEnded  (int firstToActDelta);

    public void handStarted(AlexoCardSequence cards,
                            boolean           isFirstToAct);

    public void opponentActed(
            AlexoState state,
            AlexoAction action);

    public void roundAdvanced(AlexoRound currentRound,
                              AlexoCardSequence cards);

    public AlexoAction act(AlexoState state,
                           AlexoCardSequence cards);
}
