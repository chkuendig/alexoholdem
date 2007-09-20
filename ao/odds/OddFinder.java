package ao.odds;

import ao.holdem.model.Hole;
import ao.holdem.model.Community;
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
