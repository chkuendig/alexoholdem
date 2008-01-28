package ao.holdem.model.card;

import ao.holdem.engine.persist.PlayerHandle;

/**
 *
 */
public interface HoleSource
{
    public Hole holeFor(PlayerHandle player);
}
