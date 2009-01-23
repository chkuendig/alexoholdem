package ao.bucket.index.test.exhaustive;

import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.river.River;
import ao.bucket.index.test.Gapper;
import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.odds.eval.eval7.Eval7Faster;
import static ao.util.data.Arr.swap;
import ao.util.data.AutovivifiedList;
import ao.util.math.stats.FastIntCombiner;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor2;
import ao.util.math.stats.FastIntCombiner.CombinationVisitor3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Date: Oct 1, 2008
 * Time: 7:18:52 PM
 */
public class CanonRiverTest
{
    //--------------------------------------------------------------------
    private static final int    PER_FILE   = 1 << 30;
    private static final String DIR        = "lookup/canon/test/";
    private static final String RIVER_FILE = DIR + "river.bin";
    private static final int    STEP       = Short.SIZE / 8;


    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5;//,
//                             RIVER  = 52 - 4;


    //--------------------------------------------------------------------
    private final Card   CARDS[]    = Card.values();
    private final Gapper seenHoles  = new Gapper();
    private final Gapper seenFlops  = new Gapper();
//    private       int    RIVERS[]    =
//            new long[ TurnLookup.CANONS ];

    private int shortcut = -1;


    //-----------------------------------------------------------------------
    public synchronized void testRivers()
    {
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length).combine(
                new CombinationVisitor2() {
            public void visit(int holeA, int holeB)
            {
                CanonHole hole = HoleLookup.lookup(
                        Card.VALUES[holeA], Card.VALUES[holeB]);
//                System.out.println(hole);

//                if (seenHoles.get( hole.canonIndex() )) return;
                seenHoles.set( hole.canonIndex() );

                swap(CARDS, holeB, HOLE_B );
                swap(CARDS, holeA, HOLE_A );

                long start        = System.currentTimeMillis();
                int  prevShortcut = shortcut;
                shortcut = Eval7Faster.shortcutFor(
                            hole.a(), hole.b());
                int numFlops =
                        iterateFlops(hole);
                shortcut = prevShortcut;
                long duration = (System.currentTimeMillis() - start);
                System.out.println(
                        hole     + "\t" +
                        duration + "\t" +
                        ((double) numFlops / duration));

                swap(CARDS, holeA, HOLE_A );
                swap(CARDS, holeB, HOLE_B );
            }
        });

        closeTargets();
    }

    private int iterateFlops(
            final CanonHole hole)
    {
        final int flops[] = {0};
        new FastIntCombiner(Card.INDEXES, Card.INDEXES.length - 2)
                .combine(new CombinationVisitor3() {
            public void visit(int flopA, int flopB, int flopC)
            {
                Card flopCardA = CARDS[flopA];
                Card flopCardB = CARDS[flopB];
                Card flopCardC = CARDS[flopC];

                Flop flop = hole.addFlop(
                        flopCardA, flopCardB, flopCardC);
                int index = flop.canonIndex();
//                if (seenFlops.get( index )) return;
                seenFlops.set( index );

                swap(CARDS, flopC, FLOP_C);
                swap(CARDS, flopB, FLOP_B);
                swap(CARDS, flopA, FLOP_A);

                int  prevShortcut = shortcut;
                shortcut = Eval7Faster.nextShortcut(shortcut, flopCardA);
                shortcut = Eval7Faster.nextShortcut(shortcut, flopCardB);
                shortcut = Eval7Faster.nextShortcut(shortcut, flopCardC);
                flops[0] =+
                        iterateTurnsRivers(flop);
                shortcut = prevShortcut;

                swap(CARDS, flopA, FLOP_A);
                swap(CARDS, flopB, FLOP_B);
                swap(CARDS, flopC, FLOP_C);
            }});
        return flops[0];
    }

    private int iterateTurnsRivers(Flop flop)
    {
        int count = 0;
        for (int turnCardIndex = 0;
                 turnCardIndex <= TURN;
                 turnCardIndex++)
        {
            Card turnCard = CARDS[ turnCardIndex ];
            Turn turn     = flop.addTurn( turnCard );

            int turnShortuct =
                    Eval7Faster.nextShortcut(shortcut, turnCard);
            for (int riverCardIndex = 0;
                     riverCardIndex < turnCardIndex;
                     riverCardIndex++)
            {
                Card  riverCard = CARDS[ riverCardIndex ];
                River river     = turn.addRiver( riverCard );

                process(river,
                        Eval7Faster.fastValueOf(
                                turnShortuct, riverCard));
                count++;
            }
        }
        return count;
    }


    //--------------------------------------------------------------------
    private void process(River river, short identity)
    {
        short trueIdentity     = (short) (identity + 1);
        long  canonIndex       = river.canonIndex();
        short existingIdentity = get( canonIndex );

        if (existingIdentity == 0)
        {
            set( canonIndex, trueIdentity );
        }
        else if (existingIdentity != trueIdentity)
        {
            System.out.println(
                    "MISMATCH ON:\t" +
                    river + "\t" +
                    existingIdentity + "\t" + trueIdentity);
        }
    }


    //--------------------------------------------------------------------
    private static final AutovivifiedList<RandomAccessFile>
            TARGETS = new AutovivifiedList<RandomAccessFile>();

    private static synchronized short get(long index)
    {
        try
        {
            return doGet(index);
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }
    private static short doGet(long index)
            throws IOException
    {
        long fragment =  index / PER_FILE;
        long offset   = (index % PER_FILE) * STEP;

        RandomAccessFile target = target( (int) fragment );
        if (target.length() < (offset + STEP))
        {
            return 0;
        }

        target.seek( offset );
        return target.readShort();
    }


    //--------------------------------------------------------------------
    private static synchronized void set(
            long index, short identity)
    {
        try
        {
            doSet( index, identity );
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }
    private static void doSet(
            long index, short identity) throws IOException
    {
        long fragment =  index / PER_FILE;
        long offset   = (index % PER_FILE) * STEP;

        RandomAccessFile target = target( (int) fragment );
        target.seek( offset );
        target.writeShort( identity );
    }


    //--------------------------------------------------------------------
    private static RandomAccessFile target(int fragment)
            throws FileNotFoundException
    {
        RandomAccessFile target = TARGETS.get( fragment );
        if (target == null)
        {
            target = new RandomAccessFile(
                        new File(RIVER_FILE + fragment), "rw");
            TARGETS.set( fragment, target );
        }
        return target;
    }


    //--------------------------------------------------------------------
    private static void closeTargets()
    {
        try
        {
            doCloseTargets();
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }
    private static void doCloseTargets() throws IOException
    {
        for (RandomAccessFile target : TARGETS)
        {
            target.close();
        }
        TARGETS.clear();
    }
}
