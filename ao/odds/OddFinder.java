package ao.odds;

import ao.holdem.model.card.Hole;
import ao.holdem.model.card.Community;
import ao.odds.Odds;

/**
 * 
 */
public interface OddFinder
{
    public Odds compute(Hole hole,
                        Community community,
                        int       activeOpponents);
}
