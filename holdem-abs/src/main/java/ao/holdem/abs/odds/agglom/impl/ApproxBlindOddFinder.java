package ao.holdem.abs.odds.agglom.impl;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.abs.odds.agglom.BlindOddFinder;
import ao.holdem.abs.odds.Odds;
import ao.util.math.stats.FastCombiner;

/**
 *
 */
public class ApproxBlindOddFinder
        implements BlindOddFinder
{
    //--------------------------------------------------------------------
    private Community     prevCommunity;
    private int           prevActivePlayers = -1;
    private BlindOddsImpl prevOdds;


    //--------------------------------------------------------------------
    public synchronized BlindOddsImpl
            compute(Community community,
                    int       activePlayers)
    {
        if (activePlayers == prevActivePlayers &&
                prevCommunity.equals( community ))
        {
            return prevOdds;
        }

        prevCommunity     = community;
        prevActivePlayers = activePlayers;
        prevOdds          = doCompute(community, activePlayers);

        return prevOdds;
    }

    private BlindOddsImpl doCompute(Community community,
                           int       activePlayers)
    {
        BlindOddsImpl blindOdds =
                new BlindOddsImpl(community, activePlayers);

        FastCombiner<Card> c = new FastCombiner<Card>(Card.values());
        c.combine(blindOdds);
        return blindOdds;
    }



    //--------------------------------------------------------------------
    private static class BlindOddsImpl
            implements FastCombiner.CombinationVisitor2<Card>,
                       BlindOddFinder.BlindOdds
    {
        //----------------------------------------------------------------
        private final Community community;
        private final int       activePlayers;
        private       Odds      cumulative;
        private       Odds      max;
        private Odds min;


        //----------------------------------------------------------------
        public BlindOddsImpl(
                Community community,
                int       activePlayers)
        {
            this.community     = community;
            this.activePlayers = activePlayers;
            this.cumulative    = new Odds();
            this.max           = null;
            this.min           = null;
        }


        //----------------------------------------------------------------
        public void visit(Card holeA, Card holeB)
        {
            if (!( community.contains( holeA ) ||
                   community.contains( holeB ) ))
            {
                ApproximateOddFinder f =
                        new ApproximateOddFinder(32, 1000);

                Hole hole  = Hole.valueOf(holeA, holeB);
                Odds o     = f.compute(hole, community, activePlayers - 1);
                cumulative = cumulative.plus( o );

                if (max == null ||
                    max.strengthVsRandom() < o.strengthVsRandom())
                {
                    max = o;
                }
                if (min == null ||
                    min.strengthVsRandom() > o.strengthVsRandom())
                {
                    min = o;
                }
            }
        }


        //----------------------------------------------------------------
        public Odds sum()
        {
            return cumulative;
        }

        public Odds min()
        {
            return min;
        }

        public Odds max()
        {
            return max;
        }
    }
}
