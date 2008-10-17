package ao.bucket.index.test.detail;

import ao.bucket.index.test.Gapper;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.OddHist;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.GeneralHistFinder;
import ao.odds.agglom.impl.GeneralOddFinder;
import ao.util.data.AutovivifiedList;
import ao.util.stats.FastIntCombiner;
import ao.util.stats.FastIntCombiner.CombinationVisitor2;

import java.util.List;

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
//    private static final String DIR       = "lookup/canon/detail/";
//    private static final String HOLE_FILE = DIR + "hole.lookup";
//    private static final String FLOP_FILE = DIR + "flop.lookup";


    //-----------------------------------------------------------------------
    public static void main(String[] args)
    {
//        new CanonHoleTest().testHoles();
//        new CanonHoleTest().testHolesFast();
//        new CanonHoleTest().testFlops();

//        new CanonTurnTest().testTurns();

        new CanonRiverTest().testRivers();
    }


    //-----------------------------------------------------------------------
    private final List<SeenCount<Odds>>    HOLES      =
            new AutovivifiedList<SeenCount<Odds>>();
    private final List<SeenCount<OddHist>> HOLES_FAST =
            new AutovivifiedList<SeenCount<OddHist>>();

    private final Gapper seenHoles  = new Gapper();


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
                SeenCount<Odds> oddCount = HOLES.get( hole.canonIndex() );
                if (oddCount == null)
                {
                    oddCount = new SeenCount<Odds>(odds);
                    HOLES.set( hole.canonIndex(), oddCount );
                }
                else if (! oddCount.payloadEquals( odds ))
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

                OddHist odds = new GeneralHistFinder().compute(
                                        hole, Community.PREFLOP);
                SeenCount<OddHist> existing =
                        HOLES_FAST.get( hole.canonIndex() );
                if (existing == null)
                {
                    HOLES_FAST.set(
                            hole.canonIndex(),
                            new SeenCount<OddHist>(odds, 1));
                }
                else if (! existing.payloadEquals( odds ))
                {
                    System.out.println("ERROR AT: " + hole);
                }
                else
                {
                    existing.increment();
                }
            }
        });

//        write(HOLES, OddCount.BINDING, HOLE_FILE);
        int maxCount = 0;
        for (SeenCount<OddHist> oh : HOLES_FAST)
        {
            int count = oh.payload().maxCount();
            if (count > maxCount)
            {
                maxCount = count;
            }
        }
        System.out.println("max count: " + maxCount);
    }


    //-----------------------------------------------------------------------
    // testTurns()
    

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
