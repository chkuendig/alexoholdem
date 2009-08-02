package ao.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.util.BitSet;

/**
 * User: alex
 * Date: 9-Jul-2009
 * Time: 10:20:38 PM
 */
public class CompactRiverCounts
{
    //--------------------------------------------------------------------
    public static void main(String[] args) {
        for (int i = 0;
                 i < CompactRiverCounts.NUM_COUNTS;
                 i++) {
            System.out.println(
                    i + "\t" + CompactRiverCounts.count(i));
        }
    }


    //--------------------------------------------------------------------
    private CompactRiverCounts() {}

    private static final Logger LOG =
            Logger.getLogger(CompactRiverCounts.class);


    //--------------------------------------------------------------------
    private static final String storeFile = Infrastructure.path(
                        "lookup/eval/river/compact_count.byte");

    private static final byte   COUNTS[]  = retrieveOrComputeCounts();


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
                    0, RiverLookup.CANONS)},
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
        return (byte)(count(index) / 4);
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
