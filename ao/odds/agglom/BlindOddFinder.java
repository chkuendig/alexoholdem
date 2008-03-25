package ao.odds.agglom;

import ao.holdem.v3.model.card.Community;

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
