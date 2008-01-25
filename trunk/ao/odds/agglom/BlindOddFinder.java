package ao.odds.agglom;

import ao.holdem.model.card.Community;
import ao.odds.agglom.Odds;

/**
 *
 */
public interface BlindOddFinder
{
    //--------------------------------------------------------------------
    public BlindOdds compute(Community community,
                             int       activePlayers);


    //--------------------------------------------------------------------
    public static interface BlindOdds
    {
        public Odds sum();
        public Odds min();
        public Odds max();
    }
}
