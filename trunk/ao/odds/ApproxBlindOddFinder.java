package ao.odds;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.stats.FastCombiner;

/**
 *
 */
public class ApproxBlindOddFinder
        implements BlindOddFinder
{
    //--------------------------------------------------------------------
    public Odds compute(final Community community,
                        final int       activePlayers)
    {
        HoleAveraginvVisitor average =
                new HoleAveraginvVisitor(community, activePlayers);

        FastCombiner<Card> c = new FastCombiner<Card>(Card.values());
        c.combine(average);
        return average.cumulative();
    }



    //--------------------------------------------------------------------
    private static class HoleAveraginvVisitor
            implements FastCombiner.CombinationVisitor2<Card>
    {
        //----------------------------------------------------------------
        private final Community community;
        private final int       activePlayers;
        private       Odds      cumulative;


        //----------------------------------------------------------------
        public HoleAveraginvVisitor(
                Community community,
                int       activePlayers)
        {
            this.community     = community;
            this.activePlayers = activePlayers;
            this.cumulative    = new Odds();
        }


        //----------------------------------------------------------------
        public void visit(Card holeA, Card holeB)
        {
            if (!( community.contains( holeA ) ||
                   community.contains( holeB ) ))
            {
                ApproximateOddFinder f = new ApproximateOddFinder();

                Hole hole = new Hole(holeA, holeB);
                Odds o = f.compute(hole, community, activePlayers - 1);
                cumulative = cumulative.plus( o );
            }
        }


        //----------------------------------------------------------------
        public Odds cumulative()
        {
            return cumulative;
        }
    }
}
