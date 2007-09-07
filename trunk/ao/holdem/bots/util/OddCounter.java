package ao.holdem.bots.util;

import ao.holdem.def.model.cards.Community;
import ao.util.stats.Combo;

/**
 *
 */
public class OddCounter
{
    private OddCounter() {}

    public static long combos(Community community,
                              int       activeOpponents)
    {
        int oppCards         = 2 * activeOpponents;
        int unknownCommunity = (5 - community.knownCount());
        int choose           = oppCards + unknownCommunity;

        int knownCount       = 2 + community.knownCount();
        int unknownCount     = 52 - knownCount;

        return Combo.choose(unknownCount, choose);
    }
}
