package ao.holdem.abs.bucket.index.detail.river;

import ao.Infrastructure;
import ao.holdem.abs.odds.eval.CanonEval;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.hole.CanonHole;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.turn.TurnRivers;
import ao.holdem.canon.enumeration.BitFilter;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.data.LongBitSet;
import ao.util.io.Dirs;
import ao.util.pass.Traverser;
import ao.util.persist.PersistentInts;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: Feb 10, 2009
 * Time: 5:43:45 PM
 */
public enum RiverEvalLookup
{;
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(RiverEvalLookup.class);

    private static final File dir      = Dirs.get(Infrastructure.path(
            "lookup/canon/detail/river"));
    private static final File strRepF  = new File(dir, "str_rep.char");
    private static final File winProbF = new File(dir, "winProb.char");
    private static final File flagF    = new File(dir, "flag.byte");

    private static final int  chunk    = 20 * 1000 * 1000;



    //--------------------------------------------------------------------
    static
    {
        if (! getFlag())
        {
            computeEvalDetails();
            setFlag();
        }
    }

    private static boolean getFlag()
    {
        return flagF.canRead();
    }
    private static void setFlag()
    {
        PersistentInts.persist(new int[]{ 1 }, flagF);
    }


    //--------------------------------------------------------------------
    private static void computeEvalDetails()
    {
        LOG.debug("computing");

        long offset    = strRepF.length() / (Character.SIZE / 8);
        long remaining = (River.CANONS + 1) - offset;
        long chunks    = remaining / chunk + 1;
        for (long c = 0; c < chunks; c++)
        {
            computeEvalDetails(offset + c * chunk);
        }
    }

    private static void computeEvalDetails(long from)
    {
        LOG.debug("chunk from " + from);
        Stopwatch timer = new Stopwatch();

        long toExcluding =
                Math.min(River.CANONS, from + chunk);
        int  count       = (int)(toExcluding - from);
        if (count == 0) return;

        LongBitSet allowHoles  = new LongBitSet( CanonHole.CANONS);
        LongBitSet allowFlops  = new LongBitSet( Flop.CANONS);
        LongBitSet allowTurns  = new LongBitSet( Turn.CANONS);
        LongBitSet allowRivers = new LongBitSet(River.CANONS);
        computeAllowedEvalDetails(
                from, toExcluding,
                allowHoles, allowFlops, allowTurns, allowRivers);

        short[] strengths  = new short[ count ];
        byte [] represents = new byte [ count ];
        char [] winProbs   = new char [ count ];

        computeEvalDetailsForAllowed(
                from, strengths, represents, winProbs,
                allowHoles, allowFlops, allowTurns, allowRivers);
        appendEvalDetails(strengths, represents, winProbs);

        LOG.debug("chunk from " + from + " took " + timer.timing());
    }

    private static void appendEvalDetails(
            short[] strengths, byte[] represents, char[] winProbs)
    {
        try { doAppendEvalDetails(strengths, represents, winProbs); }
        catch (IOException e) { throw new Error(e); }
    }
    private static void doAppendEvalDetails(
            short[] strengths, byte[] represents, char[] winProbs)
              throws IOException
    {
        LOG.debug("appending");

        DataOutputStream strRepOut  =
                new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(strRepF, true)));
        DataOutputStream winProbOut =
                new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(winProbF, true)));
        for (int i = 0; i < strengths.length; i++) {
            strRepOut.writeChar(
                    encode(strengths [i],
                           represents[i]));
            winProbOut.writeChar(winProbs[i]);
        }
        strRepOut.close();
        winProbOut.close();
    }

    private static void computeEvalDetailsForAllowed(
            final long       offset,
            final short[]    strengths,
            final byte []    represents,
            final char []    winProbs,
            final LongBitSet allowHoles,
            final LongBitSet allowFlops,
            final LongBitSet allowTurns,
            final LongBitSet allowRivers)
    {
        LOG.debug("computing details for allowed");

//        int     cores = Runtime.getRuntime().availableProcessors();
//        int freeCores = Math.max(1, cores - 1);
//
//        final ThreadPoolExecutor exec = new ThreadPoolExecutor(
//                1,
//                freeCores,
//                5, TimeUnit.SECONDS,
//                new ArrayBlockingQueue<Runnable>(1));
//        final ExecutorService exec = Executors.newFixedThreadPool(
//                freeCores);
//        final ExecutorService exec = Executors.newCachedThreadPool();

        final OddFinder odds = new PreciseHeadsUpOdds();
        HandEnum.rivers(
                new BitFilter<CanonHole>(allowHoles),
                new BitFilter<Flop>     (allowFlops),
                new BitFilter<Turn>     (allowTurns),
                new BitFilter<River>    (allowRivers),
                new Traverser<River>() {
            public void traverse(final River river) {
                final int index = (int)(river.canonIndex() - offset);

                if (represents[ index ] == 0) {
                    represents[ index ] = 1;

//                    exec.submit(new Runnable() {
//                        public void run() {
                            strengths[ index ] = CanonEval.eval(river);
                            winProbs [ index ] =
                                    ProbabilityEncoding.encodeWinProb(
                                            CanonEval.vsRandom(river, odds));
//                        }});
                } else {
                    represents[ index ]++;
                }

//                if (Rand.nextBoolean(100000)) {
//                    System.out.println(
//                        ((ThreadPoolExecutor) exec).getActiveCount() + "\t" +
//                        ((ThreadPoolExecutor) exec).getPoolSize() + "\t" +
//                        ((ThreadPoolExecutor) exec).getQueue().size());
//                }

//                else if (strengths[ index ] != river.eval()) {
//                    LOG.error("inconcistent " + river);
//                }
            }});

//        LOG.debug("awaiting chunk termination");
//        exec.shutdown();
//        try {
//            exec.awaitTermination(100, TimeUnit.DAYS);
//        } catch (InterruptedException e) {
//            throw new Error( e );
//        }
    }


    private static void computeAllowedEvalDetails(
            final long       from,
            final long       toExcluding,
            final LongBitSet allowHoles,
            final LongBitSet allowFlops,
            final LongBitSet allowTurns,
            final LongBitSet allowRivers)
    {
        LOG.debug("computing allowed");

        for (long river = from; river < toExcluding; river++)
        {
            int turn = TurnRivers.turnFor( river );

            CanonFlopDetail flopDetail = FlopDetails.containing(turn);
            int flop = (int) flopDetail.canonIndex();
            int hole = (int) flopDetail.holeDetail().canonIndex();

            allowHoles .set( hole );
            allowFlops .set( flop );
            allowTurns .set( turn );
            allowRivers.set( river);
        }
    }


    //--------------------------------------------------------------------
    public static void traverse(
            final CanonRange      allowRivers[],
            final VsRandomVisitor traverser)
    {
        int traverseFrom = 0;
        for (int i = 1; i < allowRivers.length; i++) {
            long gap = allowRivers[i - 1].distanceTo(allowRivers[ i ]);
            if (gap > 1024 * 1024) {
                traverse(allowRivers,
                         traverseFrom, i,
                         traverser);
                traverseFrom = i;
            }
        }
        traverse(allowRivers,
                 traverseFrom, allowRivers.length,
                 traverser);
    }
    private static void traverse(
            final CanonRange      allowRivers[],
            final int             from,
            final int             toExclusive,
            final VsRandomVisitor traverser)
    {
        final int nextAllowed[] = {from};

        traverse(allowRivers[from].from(),
                 allowRivers[toExclusive - 1].toInclusive()
                         - allowRivers[from].from() + 1,
                 new VsRandomVisitor() {
             public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {

                 while (allowRivers[ nextAllowed[0] ]
                          .toInclusive() < canonIndex) {
                     nextAllowed[0]++;
                 }

                 if (allowRivers[ nextAllowed[0] ]
                         .contains(canonIndex)) {
                     traverser.traverse(
                             canonIndex, strengthVsRandom, represents);
                 }
             }});
    }


    public static void traverse(
            long            offset,
            long            count,
            VsRandomVisitor traverser)
    {
        try {
            doTraverse(offset, count, traverser);
        } catch (IOException e) {
            throw new Error(
                    "offset: " + offset + ", " +
                    "count: "  + count, e);
        }
    }

    // for possible speed-ups, see:
    //   http://nadeausoftware.com/articles/
    //      2008/02/java_tip_how_read_files_quickly
    private static void doTraverse(
            long            offset,
            long            count,
            VsRandomVisitor traverser) throws IOException
    {
        DataInputStream winProbIn =
                new DataInputStream(new BufferedInputStream(
                        new FileInputStream(winProbF)));
        DataInputStream strRepIn =
                new DataInputStream(new BufferedInputStream(
                        new FileInputStream(strRepF)));

        if (offset > 0) {
            long toSkip = offset * (Character.SIZE / 8);
            if (toSkip != winProbIn.skip( toSkip )) {
                throw new Error("unable to skip");
            }
            if (toSkip != strRepIn.skip( toSkip )) {
                throw new Error("unable to skip");
            }
        }

        for (long i = 0; i < count; i++) {
            long river   = offset + i;
            char winProb = winProbIn.readChar();
            char strRep  = strRepIn .readChar();
            traverser.traverse(
                    river,
                    ProbabilityEncoding.decodeWinProb(winProb),
                    decodeRep(strRep));
        }

        strRepIn .close();
        winProbIn.close();
    }


    //--------------------------------------------------------------------
    public static void traverse(
            final LongBitSet allowRivers,
            final AbsVisitor traverser)
    {
        for (CanonRange r : partitionAllowed(allowRivers))
        {
            traverse(r.from(),
                     r.count(),
                     new AbsVisitor() {
                 public void traverse(
                         long canonIndex, short strength, byte count) {
                     if (allowRivers.get(canonIndex)) {
                         traverser.traverse(canonIndex, strength, count);
                     }
                 }});
        }
    }

    public static void traverse(AbsVisitor traverser)
    {
        traverse(0, River.CANONS, traverser);
    }
    public static void traverse(
            long               offset,
            long               count,
            AbsVisitor traverser)
    {
        try {
            doTraverse(offset, count, traverser);
        } catch (IOException e) {
            throw new Error( e );
        }
    }

    // for possible speed-ups, see:
    //   http://nadeausoftware.com/articles/
    //      2008/02/java_tip_how_read_files_quickly
    private static void doTraverse(
            long       offset,
            long       count,
            AbsVisitor traverser) throws IOException
    {
        DataInputStream strRepIn =
                new DataInputStream(new BufferedInputStream(
                        new FileInputStream(strRepF),
                        1024 * 1024));

        if (offset > 0)
        {
            long strSkip = offset * (Character.SIZE / 8);
            if (strSkip != strRepIn.skip( strSkip )) {
                throw new Error("unable to skip");
            }
        }

        for (long i = 0; i < count; i++)
        {
            long river = offset + i;
            char strRep = strRepIn.readChar();
            traverser.traverse(
                    river,
                    decodeStrength(strRep),
                    decodeRep(strRep));
        }

        strRepIn.close();
    }


    //--------------------------------------------------------------------
    private static CanonRange[] partitionAllowed(LongBitSet allowRivers)
    {
        List<CanonRange> partitions = new ArrayList<CanonRange>();

        long startOfGap = -1;
        long lastSet    = -1;
        for (long i = allowRivers.nextSetBit(0);
                  i >= 0;
                  i = allowRivers.nextSetBit(i + 1)) {
            if (startOfGap != -1) {
                long distance = i - lastSet - 1;
                if (distance > 8192) {
                    partitions.add(CanonRange.newFromCount(
                            startOfGap,
                            (lastSet - startOfGap + 1)));
                    startOfGap = i;
                }
            } else {
                startOfGap = i;
            }
            lastSet = i;
        }
        if (startOfGap != -1) {
            partitions.add(CanonRange.newFromCount(
                    startOfGap,
                    (lastSet - startOfGap + 1)));
        }

        return partitions.toArray(new CanonRange[partitions.size()]);
    }


    //--------------------------------------------------------------------
    private static char encode(short strength, byte count) {
        int countIndex =
                  (count ==  4) ? 0
                : (count == 12) ? 1 : 2;
        return (char)(strength << 2 | countIndex);
    }

    private static byte decodeRep(char strRep) {
        int repIndex = strRep & 3;
        return (byte)(  repIndex == 0 ? 4
                      : repIndex == 1 ? 12 : 24);
    }
    private static short decodeStrength(char strRep) {
        return (short)(strRep >>> 3);
    }


    //--------------------------------------------------------------------
    public static interface AbsVisitor
    {
        public void traverse(
                long  canonIndex,
                short strength,
                byte  count);
    }
    public static interface VsRandomVisitor
    {
        public void traverse(
                long   canonIndex,
                double strengthVsRandom,
                byte   represents);
    }
}
