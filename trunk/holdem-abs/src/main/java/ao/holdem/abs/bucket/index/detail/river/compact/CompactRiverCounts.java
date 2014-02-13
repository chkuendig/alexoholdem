package ao.holdem.abs.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.holdem.canon.river.River;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.river.RiverEvalLookup;
import ao.util.io.Dirs;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.BitSet;

/**
 * Date: 9-Jul-2009
 * Time: 10:20:38 PM
 */
public enum CompactRiverCounts
{;
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CompactRiverCounts.class);


    //--------------------------------------------------------------------
    private static final File dir =
            Dirs.get(Infrastructure.path("lookup/eval/river"));

    private static final File storeFile =
            new File(dir, "compact_count.byte");

    private static final byte   COUNTS[]  = retrieveOrComputeCounts();
//    private static final byte   COUNTS[]  = ;


    //--------------------------------------------------------------------
    private static byte[] retrieveOrComputeCounts()
    {
        LOG.debug("retrieveOrComputeCounts");
        byte counts[] = PersistentBytes.retrieve(storeFile);
        if (counts != null) return counts;

        counts = computeCounts();
        PersistentBytes.persist(counts, storeFile);
        return counts;
    }


    //--------------------------------------------------------------------
    private static byte[] computeCounts()
    {
        LOG.debug("computeCounts");

        final BitSet used = new BitSet();
        RiverEvalLookup.traverse(
            new CanonRange[]{CanonRange.newFromCount(
                    0, River.CANONS)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents)
                {
                    used.set(represents);
                }
            });

        int  nextCount = 0;
        byte counts[]  = new byte[ used.cardinality() ];
        for (int i = used.nextSetBit(0);
                 i >= 0;
                 i = used.nextSetBit(i+1)) {
            counts[ nextCount++ ] = (byte) i;
        }
        return counts;
    }


    //--------------------------------------------------------------------
    public static int  NUM_COUNTS = COUNTS.length;

    public static byte count(int index)
    {
        return COUNTS[ index ];
    }

    public static byte normCount(int index)
    {
        return (byte)(count(index) / 4); // todo: 4 is gcd(COUNTS);
    }

    public static byte indexOf(byte count)
    {
        for (byte i = 0; i < NUM_COUNTS; i++)
        {
            if (COUNTS[ i ] == count)
            {
                return i;
            }
        }
        return -1;
    }
}
