package ao.holdem.ai.ai.simple.alexo.player;

import ao.holdem.ai.ai.simple.alexo.AlexoAction;
import ao.holdem.ai.ai.simple.alexo.AlexoPlayer;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoRound;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

/**
 *
 */
public abstract class AbstractAlexoPlayer
        implements AlexoPlayer
{
    public void handEnded  (int delta) {}

    public void handStarted(AlexoCardSequence cards,
                            boolean           isFirstToAct) {}

    public void opponentActed(
            AlexoState state, AlexoAction action) {}

    public void roundAdvanced(
            AlexoRound currentRound,
            AlexoCardSequence cards) {}

    
    public String toString()
    {
        return getClass().toString();
    }
}
