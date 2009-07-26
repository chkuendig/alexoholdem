package ao.bucket.index.detail.river;

import ao.Infrastructure;
import ao.bucket.index.canon.flop.Flop;
import ao.bucket.index.canon.flop.FlopLookup;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.canon.turn.Turn;
import ao.bucket.index.canon.turn.TurnLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.enumeration.BitFilter;
import ao.bucket.index.enumeration.HandEnum;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.data.LongBitSet;
import ao.util.io.Dir;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: Feb 10, 2009
 * Time: 5:43:45 PM
 */
public class RiverEvalLookup
{
    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        final long   start      = System.currentTimeMillis();
//        final long[] totalCount = {0};
//        final long[] seen       = new long[ 25 ];
//        RiverEvalLookup.traverse(new AbsVisitor() {
//            public void traverse(
//                    long canonIndex, short strength, byte count) {
//                seen[ count ]++;
//                totalCount[0] += count;
//            }
//        });
//        System.out.println("total: " + totalCount[0]);
//        System.out.println("took: "  +
//                           (System.currentTimeMillis() - start));
//        System.out.println("distribution: "  +
//                           Arrays.toString(seen));

        final int byStrength[] = new int[ Character.MAX_VALUE + 1];
        RiverEvalLookup.traverse(
            new CanonRange[]{CanonRange.newFromTo(
                    0, RiverLookup.CANONS)},
            new VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents) {

                    char strAsChar =
                            ProbabilityEncoding.encodeWinProb(strengthVsRandom);

                    byStrength[strAsChar] += represents;

//                    River r = RiverExamples.examplesOf(
//                                             canonIndex).get(0);
//                    char verify = (char)(Character.MAX_VALUE *
//                            r.vsRandom(new PreciseHeadsUpOdds()));
//
//                    System.out.println(
//                            canonIndex       + "\t" +
//                            strengthVsRandom + "\t" +
//                            (int) strAsChar  + "\t" +
//                            (int) verify);
//                    assert strAsChar == verify;
                }
            });

        for (int count : byStrength) {
            System.out.println(count);
        }
    }


    //--------------------------------------------------------------------
    private RiverEvalLookup() {}

    private static final Logger LOG =
            Logger.getLogger(RiverEvalLookup.class);

    private static final File dir      = Dir.get(Infrastructure.path(
            "lookup/canon/detail/river"));
    private static final File strRepF  = new File(dir, "str_rep.char");
    private static final File winProbF = new File(dir, "winProb.char");
    private static final File flagF    = new File(dir, "flag.byte");

    private static final int  chunk    = 10 * 1000 * 1000;



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
        long remaining = (RiverLookup.CANONS + 1) - offset;
        long chunks    = remaining / chunk + 1;
        for (long c = 0; c < chunks; c++)
        {
            computeEvalDetails(offset + c * chunk);
        }
    }

    private static void computeEvalDetails(long from)
    {
        LOG.debug("chunk from " + from);

        long toExcluding =
                Math.min(RiverLookup.CANONS, from + chunk);
        int  count       = (int)(toExcluding - from);
        if (count == 0) return;

        LongBitSet allowHoles  = new LongBitSet(HoleLookup.CANONS);
        LongBitSet allowFlops  = new LongBitSet(FlopLookup.CANONS);
        LongBitSet allowTurns  = new LongBitSet(TurnLookup.CANONS);
        LongBitSet allowRivers = new LongBitSet(RiverLookup.CANONS);
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

        final OddFinder odds = new PreciseHeadsUpOdds();
        HandEnum.rivers(
                new BitFilter<CanonHole>(allowHoles),
                new BitFilter<Flop>     (allowFlops),
                new BitFilter<Turn>     (allowTurns),
                new BitFilter<River>    (allowRivers),
                new Traverser<River>() {
            public void traverse(River river) {
                int index = (int)(river.canonIndex() - offset);
                if (represents[ index ] == 0) {
                    strengths[ index ] = river.eval();
                    winProbs [ index ] =
                            ProbabilityEncoding.encodeWinProb(
                                    river.vsRandom(odds));
                }
//                else if (strengths[ index ] != river.eval()) {
//                    LOG.error("inconcistent " + river);
//                }
                represents[ index ]++;
            }});
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
            throw new Error( e );
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
        traverse(0, RiverLookup.CANONS, traverser);
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
                    decoreStrength(strRep),
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
    private static short decoreStrength(char strRep) {
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
