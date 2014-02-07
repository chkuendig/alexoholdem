package ao.holdem.ai.ai.simple.alexo.player;

import ao.holdem.ai.ai.simple.alexo.AlexoAction;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

/**
 *
 */
public class AlwaysFold extends AbstractAlexoPlayer
{
    public AlexoAction act(
            AlexoState        state,
            AlexoCardSequence cards)
    {
        return AlexoAction.FOLD;
    }
}
