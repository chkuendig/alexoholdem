package ao.simple.alexo;

import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoState;

/**
 *
 */
public interface AlexoPlayer
{
    public AlexoAction act(AlexoState state,
                           AlexoCardSequence cards);
}
