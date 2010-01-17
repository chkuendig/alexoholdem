package ao.simple.alexo.player;

import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoState;

/**
 *
 */
public class AlwaysCall extends AbstractAlexoPlayer
{
    public AlexoAction act(AlexoState state, AlexoCardSequence cards)
    {
        return AlexoAction.CHECK_CALL;
    }
}
