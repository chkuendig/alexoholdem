package ao.holdem.engine.agglom;

import ao.holdem.model.card.Community;

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
