package ao.odds;

import ao.holdem.model.card.Community;

/**
 *
 */
public interface BlindOddFinder
{
    public Odds compute(Community community,
                        int       activePlayers);
}
