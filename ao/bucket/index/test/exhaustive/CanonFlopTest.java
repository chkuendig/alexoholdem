package ao.bucket.index.test.exhaustive;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.test.Gapper;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.odds.agglom.hist.GeneralHistFinder;
import ao.odds.agglom.hist.StrengthHist;
import static ao.util.data.Arr.swap;
import ao.util.math.stats.FastIntCombiner;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor3;

/**
 * Date: Sep 30, 2008
 * Time: 9:09:48 PM
 */
public class CanonFlopTest
{
    //--------------------------------------------------------------------
    private final Card   CARDS[]    = Card.values();
    private final Gapper seenHoles  = new Gapper();
    private final Gapper seenFlops  = new Gapper();
    private       long   FLOPS_FAST[];


    //-----------------------------------------------------------------------
    public synchronized void testFlops()
    {
        FLOPS_FAST = new long[ FlopLookup.CANONS];

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                CanonHole hole = HoleLookup.lookup(
                        Card.VALUES[holeA], Card.VALUES[holeB]);
                System.out.println(hole);

//                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(CARDS, holeB, 51  );
                swap(CARDS, holeA, 51-1);

                iterateFlops(hole);

                swap(CARDS, holeA, 51-1);
                swap(CARDS, holeB, 51  );
            }
        });
    }

    private void iterateFlops(
            final CanonHole hole)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        CARDS[flopA], CARDS[flopB], CARDS[flopC]);
                int index = flop.canonIndex();
//                if (seenFlops.get( index )) return;
                seenFlops.set( index );

                StrengthHist odds     =
                        new GeneralHistFinder().compute(
                                hole.reify(),
                                new Community(
                                        CARDS[flopA],
                                        CARDS[flopB],
                                        CARDS[flopC]));
                long existing = FLOPS_FAST[ index ];
                if (existing == 0)
                {
                    FLOPS_FAST[ index ] = odds.secureHashCode();
                }
                else if (existing != odds.secureHashCode())
                {
                    System.out.println("ERROR AT: " + flop);
                }
            }});
    }    
}
