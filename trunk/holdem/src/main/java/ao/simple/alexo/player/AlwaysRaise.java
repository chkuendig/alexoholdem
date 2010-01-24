package ao.simple.alexo.player;

import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoState;

/**
 *
 */
public class AlwaysRaise extends AbstractAlexoPlayer
{
    public AlexoAction act(AlexoState state, AlexoCardSequence cards)
    {
        return state.validActions().contains( AlexoAction.BET_RAISE )
                ? AlexoAction.BET_RAISE
                : AlexoAction.CHECK_CALL;
    }
}
