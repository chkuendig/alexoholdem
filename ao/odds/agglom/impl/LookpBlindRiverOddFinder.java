package ao.odds.agglom.impl;

import ao.odds.agglom.BlindOddFinder;
import ao.holdem.model.card.Community;
import ao.holdem.model.BettingRound;

/**
 *
 */
public class LookpBlindRiverOddFinder implements BlindOddFinder
{
    //--------------------------------------------------------------------
    // 52 * 51 * 50 * 49 * 42

    //--------------------------------------------------------------------
    public BlindOdds compute(Community community, int activePlayers)
    {
        assert community.round() == BettingRound.RIVER;

        return null;
    }
}
