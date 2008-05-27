package ao.simple.alexo.player;

import ao.simple.alexo.AlexoAction;
import ao.simple.alexo.AlexoPlayer;
import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.state.AlexoState;
import ao.util.rand.Rand;

/**
 *
 */
public class AlexoRandom implements AlexoPlayer
{
    public AlexoAction act(AlexoState state, AlexoCardSequence cards)
    {
        return Rand.fromList(
                state.validActions());
    }
}
