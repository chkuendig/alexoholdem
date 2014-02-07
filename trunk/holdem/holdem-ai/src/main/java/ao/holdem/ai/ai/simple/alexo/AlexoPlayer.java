package ao.holdem.ai.ai.simple.alexo;

import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoRound;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

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
