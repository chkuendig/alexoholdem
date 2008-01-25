package ao.odds.agglom;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.Odds;

/**
 * 
 */
public interface OddFinder
{
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents);
}
