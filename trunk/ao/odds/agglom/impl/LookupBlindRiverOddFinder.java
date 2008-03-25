package ao.odds.agglom.impl;

import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.odds.agglom.BlindOddFinder;

/**
 *
 */
public class LookupBlindRiverOddFinder implements BlindOddFinder
{
    //--------------------------------------------------------------------
    // 52 * 51 * 50 * 49 * 42

    //--------------------------------------------------------------------
    public BlindOdds compute(Community community, int activePlayers)
    {
        assert community.round() == Round.RIVER;

        return null;
    }
}
