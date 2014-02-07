package ao.holdem.ai.ai.simple.alexo.player;

import ao.holdem.ai.ai.simple.alexo.AlexoAction;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;
import ao.util.math.rand.Rand;

/**
 *
 */
public class AlexoRandom extends AbstractAlexoPlayer
{
    public AlexoAction act(AlexoState state, AlexoCardSequence cards)
    {
        return Rand.fromList(
                state.validActions());
    }
}
