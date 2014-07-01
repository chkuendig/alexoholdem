package ao.holdem.abs.odds.agglom;

import ao.holdem.engine.eval.odds.Odds;
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
