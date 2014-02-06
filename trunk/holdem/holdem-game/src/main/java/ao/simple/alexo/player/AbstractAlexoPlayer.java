package ao.simple.alexo.player;

import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.AlexoPlayer;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoRound;
import ao.simple.alexo.state.AlexoState;

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
