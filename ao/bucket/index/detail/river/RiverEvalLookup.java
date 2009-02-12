package ao.bucket.index.detail.river;

import ao.bucket.index.detail.flop.FlopDetailFlyweight.CanonFlopDetail;
import ao.bucket.index.detail.flop.FlopDetails;
import ao.bucket.index.detail.turn.TurnRivers;
import ao.bucket.index.enumeration.BitFilter;
import ao.bucket.index.enumeration.HandEnum;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.bucket.index.river.River;
import ao.bucket.index.river.RiverLookup;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.util.data.LongBitSet;
import ao.util.io.Dir;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Arrays;

/**
 * Date: Feb 10, 2009
 * Time: 5:43:45 PM
 */
public class RiverEvalLookup
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        final long   start      = System.currentTimeMillis();
        final long[] totalCount = {0};
        final long[] seen       = new long[ 128 ];
        RiverEvalLookup.traverse(new RiverEvalTraverser() {
            public void traverse(
                    long canonIndex, short strength, byte count) {
                seen[ count ]++;
                totalCount[0] += count;

                if (count == 26) {
                    System.out.println(canonIndex + "\t" + strength);
                }
            }
        });
        System.out.println("total: " + totalCount[0]);
        System.out.println("took: "  +
                           (System.currentTimeMillis() - start));
        System.out.println("distribution: "  +
                           Arrays.toString(seen));
    }


    //--------------------------------------------------------------------
    private RiverEvalLookup() {}

    private static final Logger LOG =
            Logger.getLogger(RiverEvalLookup.class);

    private static final File dir   =
            Dir.get("lookup/canon/detail/river");
    private static final File strengthF = new File(dir, "str.short");
    private static final File repsF     = new File(dir, "rep.byte");
    private static final File flagF     = new File(dir, "flag.byte");

    private static final int  chunk     = 10 * 1000 * 1000;


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

        long offset    = repsF.length();
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

        computeEvalDetailsForAllowed(
                from, strengths, represents,
                allowHoles, allowFlops, allowTurns, allowRivers);
        appendEvalDetails(strengths, represents);
    }

    private static void appendEvalDetails(
            short[] strengths, byte[] represents)
    {
        try { doAppendEvalDetails(strengths, represents); }
        catch (IOException e) { throw new Error(e); }
    }
    private static void doAppendEvalDetails(
            short[] strengths, byte[] represents) throws IOException
    {
        LOG.debug("appending");

        DataOutputStream strOut =
                new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(strengthF, true)));
        DataOutputStream repOut =
                new DataOutputStream(new BufferedOutputStream(
                        new FileOutputStream(repsF, true)));

        for (int i = 0; i < strengths.length; i++) {
            strOut.writeShort( strengths [i] );
            repOut.writeByte ( represents[i] );
        }

        repOut.close();
        strOut.close();
    }

    private static void computeEvalDetailsForAllowed(
            final long       offset,
            final short[]    strengths,
            final byte []    represents,
            final LongBitSet allowHoles,
            final LongBitSet allowFlops,
            final LongBitSet allowTurns,
            final LongBitSet allowRivers)
    {
        LOG.debug("computing details for allowed");

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
                }
                else if (strengths[ index ] != river.eval())
                {
                    LOG.error("inconcistent " + river);
                }
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
//            if (flopDetail == null) {
//                System.out.println("river: " + river + ", turn: " + turn);
//            }
            int flop = (int) flopDetail.canonIndex();
            int hole = (int) flopDetail.holeDetail().canonIndex();

            allowHoles .set( hole );
            allowFlops .set( flop );
            allowTurns .set( turn );
            allowRivers.set( river);
        }
    }


    //--------------------------------------------------------------------
    public static void traverse(RiverEvalTraverser traverser)
    {
        traverse(0, RiverLookup.CANONS, traverser);
    }
    public static void traverse(
            long               offset,
            long               count,
            RiverEvalTraverser traverser)
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
            long               offset,
            long               count,
            RiverEvalTraverser traverser) throws IOException
    {
        DataInputStream strIn =
                new DataInputStream(new BufferedInputStream(
                        new FileInputStream(strengthF)));
        DataInputStream repIn =
                new DataInputStream(new BufferedInputStream(
                        new FileInputStream(repsF)));

        if (offset > 0)
        {
            long strSkip = offset * (Short.SIZE / 8);
            if (strSkip != strIn.skip( strSkip )) {
                throw new Error("unable to skip strengths");
            }

            if (offset != repIn.skip( offset )) {
                throw new Error("unable to skip represents");
            }
        }

        for (long i = 0; i < count; i++)
        {
            long river = offset + i;
            traverser.traverse(
                    river,
                    strIn.readShort(),
                    repIn.readByte());
        }

        repIn.close();
        strIn.close();
    }


    //--------------------------------------------------------------------
    public static interface RiverEvalTraverser
    {
        public void traverse(
                long  canonIndex,
                short strength,
                byte  count);
    }
}
