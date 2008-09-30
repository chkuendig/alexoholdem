package ao.bucket.index.detail;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.test.AutovivifiedList;
import ao.bucket.index.test.Gapper;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.OddHist;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.GeneralHistFinder;
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
    private static final String DIR       = "lookup/canon/detail/";
    private static final String HOLE_FILE = DIR + "hole.lookup";
    private static final String FLOP_FILE = DIR + "flop.lookup";


    //-----------------------------------------------------------------------
    public static void main(String[] args)
    {
//        new CanonHoleTest().testHoles();
//        new CanonHoleTest().testHolesFast();
        new CanonHoleTest().testFlops();
    }


    //-----------------------------------------------------------------------
    private final Card                       CARDS[]    = Card.values();
    private final AutovivifiedList<OddCount> HOLES      =
            new AutovivifiedList<OddCount>();
    private final AutovivifiedList<OddHist>  HOLES_FAST =
            new AutovivifiedList<OddHist>();
    private final AutovivifiedList<OddCount> FLOPS      =
            new AutovivifiedList<OddCount>();
    private       long                       FLOPS_FAST[];
    private final Gapper                     seenHoles  = new Gapper();
    private final Gapper                     seenFlops  = new Gapper();


    //-----------------------------------------------------------------------
    public synchronized void testHoles()
    {
        HOLES.clear();
        seenHoles.clear();

//        read(HOLES, OddCount.BINDING, HOLE_FILE);
//        if (! HOLES.isEmpty())
//        {
//            for (OddCount oc : HOLES)
//            {
//                System.out.println(oc);
//            }
//        }

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        Card.VALUES[holeA], Card.VALUES[holeB]);
                if (seenHoles.get(hole.canonIndex())) return;
                seenHoles.set( hole.canonIndex() );
                
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

//        write(HOLES_FAST, OddCount.BINDING, HOLE_FILE);
    }


    //-----------------------------------------------------------------------
    public synchronized void testHolesFast()
    {
        HOLES_FAST.clear();
        seenHoles.clear();

        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                Hole hole = Hole.valueOf(
                        Card.VALUES[holeA], Card.VALUES[holeB]);
//                if (seenHoles.get(hole.canonIndex())) return;
                seenHoles.set( hole.canonIndex() );
                System.out.println(hole);

                OddHist odds     = new GeneralHistFinder().compute(
                                            hole, Community.PREFLOP);
                OddHist existing = HOLES_FAST.get( hole.canonIndex() );
                if (existing == null)
                {
                    HOLES_FAST.set( hole.canonIndex(), odds );
                }
                else if (! existing.equals( odds ))
                {
                    System.out.println("ERROR AT: " + hole);
                }
            }
        });

//        write(HOLES, OddCount.BINDING, HOLE_FILE);
    }


    //-----------------------------------------------------------------------
    public synchronized void testFlops()
    {
//        FLOPS.clear();
//        seenHoles.clear();
//        seenFlops.clear();

        FLOPS_FAST = new long[ FlopLookup.CANON_FLOP_COUNT ];

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

//        write(FLOPS, OddCount.BINDING, FLOP_FILE);
//        for (int i = 0; i < FLOPS.size(); i++)
//        {
//            OddCount oddCount = FLOPS.get( i );
//            System.out.println(i + "\t" + oddCount);
//        }
    }

    public void iterateFlops(
            final Hole hole)
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

                OddHist odds     =
                        new GeneralHistFinder().compute(
                                hole,
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


    //-----------------------------------------------------------------------
//    private static void read(
//            Collection<OddCount>     to,
//            GenericBinding<OddCount> binding,
//            String                   filename)
//    {
//        try
//        {
//            doRead(to, binding, filename);
//        }
//        catch (IOException e)
//        {
//            throw new Error( e );
//        }
//    }
//    private static void doRead(
//            Collection<OddCount>     to,
//            GenericBinding<OddCount> binding,
//            String                   filename)
//                throws IOException
//    {
//        File        inFile = new File(filename);
//        InputStream in     =
//                new FileInputStream(filename);
//        byte content[];
//        try
//        {
//            content = new byte[ (int) inFile.length() ];
//            if (in.read( content ) == -1) return;
//        }
//        finally
//        {
//            in.close();
//        }
//
//        TupleInput ti = new TupleInput(content);
//        while (ti.available() > 0)
//        {
//            to.add( binding.read(ti) );
//        }
//    }
//
//
//    //-----------------------------------------------------------------------
//    private static void write(
//            List<OddCount>           counts,
//            GenericBinding<OddCount> binding,
//            String                   filename)
//    {
//        try
//        {
//            doWrite(counts, binding, filename);
//        }
//        catch (IOException e)
//        {
//            throw new Error( e );
//        }
//    }
//    private static void doWrite(
//            List<OddCount>           counts,
//            GenericBinding<OddCount> binding,
//            String                   filename) throws IOException
//    {
//        OutputStream out =
//                new FileOutputStream(filename);
//
//        TupleOutput to = new TupleOutput();
//        for (OddCount oddCount : counts)
//        {
//            binding.write(oddCount, to);
//
//            byte bytes[] = to.getBufferBytes();
//            out.write( bytes, 0, to.getBufferLength() );
//            to = new TupleOutput(bytes);
//        }
//
//        out.close();
//    }
}
