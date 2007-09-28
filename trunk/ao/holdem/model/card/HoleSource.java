package ao.holdem.model.card;

import ao.persist.PlayerHandle;

/**
 *
 */
public interface HoleSource
{
    public Hole holeFor(PlayerHandle player);
}
