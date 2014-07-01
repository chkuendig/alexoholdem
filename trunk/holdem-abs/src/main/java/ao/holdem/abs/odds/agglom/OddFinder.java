package ao.holdem.abs.odds.agglom;

import ao.holdem.engine.eval.odds.Odds;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 * 
 */
public interface OddFinder
{
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents);
}
