package ao.odds;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.odds.Odds;

/**
 * 
 */
public interface OddFinder
{
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents);
}
