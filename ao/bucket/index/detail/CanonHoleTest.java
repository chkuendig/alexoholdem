package ao.bucket.index.detail;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.test.AutovivifiedList;
import ao.bucket.index.test.Gapper;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.GeneralOddFinder;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.stats.FastIntCombiner.CombinationVisitor3;

/**
 * Date: Sep 22, 2008
 * Time: 4:04:17 PM
 *
 * Varifies that all holes with a given canonical index
 *  have identical odds in heads up holdem.
 */
public class CanonHoleTest
{
    //-----------------------------------------------------------------------
    public static void main(String[] args)
    {
        new CanonHoleTest().testFlops();
    }


    //-----------------------------------------------------------------------
    private final Card                       CARDS[]   = Card.values();
    private final AutovivifiedList<OddCount> HOLES     =
            new AutovivifiedList<OddCount>();
    private final AutovivifiedList<OddCount> FLOPS     =
            new AutovivifiedList<OddCount>();
    private final Gapper                     seenHoles = new Gapper();
    private final Gapper                     seenFlops = new Gapper();


    //-----------------------------------------------------------------------
    public synchronized void testHoles()
    {
        HOLES.clear();
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        Card.VALUES[holeA], Card.VALUES[holeB]);

                Odds     odds     = new GeneralOddFinder().compute(
                                            hole, Community.PREFLOP, 1);
                OddCount oddCount = HOLES.get( hole.canonIndex() );
                if (oddCount == null)
                {
                    oddCount = new OddCount(odds);
                    HOLES.set( hole.canonIndex(), oddCount );
                }
                else if (! oddCount.oddsEqual( odds ))
                {
                    System.out.println(hole + " :: " +
                            oddCount + " vs " + odds);
                }
                oddCount.increment();

                System.out.println(hole   + "\t" +
                        hole.canonIndex() + "\t" + oddCount);
            }
        });

        for (int i = 0; i < HOLES.size(); i++)
        {
            OddCount oddCount = HOLES.get( i );
            System.out.println(i + "\t" + oddCount);
        }
    }


    //-----------------------------------------------------------------------
    public synchronized void testFlops()
    {
        FLOPS.clear();
        seenHoles.clear();
        seenFlops.clear();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
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

        for (int i = 0; i < FLOPS.size(); i++)
        {
            OddCount oddCount = FLOPS.get( i );
            System.out.println(i + "\t" + oddCount);
        }
    }

    public void iterateFlops(
            final Hole hole)
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2).combine(
                new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Flop flop = hole.addFlop(
                        CARDS[flopA], CARDS[flopB], CARDS[flopC]);
                int index = flop.canonIndex();
//                if (seenFlops.get( index )) return;
                seenFlops.set( index );

                Odds     odds     =
                        new GeneralOddFinder().compute(
                                hole,
                                new Community(
                                        CARDS[flopA],
                                        CARDS[flopB],
                                        CARDS[flopC]),
                                1);
                OddCount oddCount = FLOPS.get( index );
                if (oddCount == null)
                {
                    oddCount = new OddCount(odds);
                    FLOPS.set( index, oddCount );
                }
                else if (! oddCount.oddsEqual( odds ))
                {
                    System.out.println(flop + " :: " +
                            oddCount + " vs " + odds);
                }
                oddCount.increment();
            }});
    }
}
