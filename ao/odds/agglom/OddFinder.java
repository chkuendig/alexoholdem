package ao.odds.agglom;

import ao.holdem.v3.model.card.Community;
import ao.holdem.v3.model.card.Hole;

/**
 * 
 */
public interface OddFinder
{
    public Odds compute(Hole hole,
                        Community community,
                        int       activeOpponents);
}
