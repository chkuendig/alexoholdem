package ao.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.util.persist.PersistentChars;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 9-Jul-2009
 * Time: 10:16:28 PM
 */
public class MemProbCounts
{
    //--------------------------------------------------------------------
    public static void main(String[] args) {
        long totalCount = 0;
        for (long r = 0; r < RiverLookup.CANONS; r++) {
            totalCount += MemProbCounts.riverCount(r);
        }
        System.out.println(totalCount);

        RiverEvalLookup.traverse(
            new CanonRange[]{new CanonRange(0, RiverLookup.CANONS)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents)
                {
                    byte rep = MemProbCounts.riverCount(canonIndex);
                    if (rep != represents)
                    {
                        System.out.println(canonIndex +" err: " +
                                rep + " vs " + represents);
                    }
                }
            });
    }


    //--------------------------------------------------------------------
    private MemProbCounts() {}

    private static final Logger LOG =
            Logger.getLogger(MemProbCounts.class);


    //--------------------------------------------------------------------
    private static final String storeFileA = Infrastructure.path(
                        "lookup/eval/river/compact_prob_count_a.char");
    private static final String storeFileB = Infrastructure.path(
                        "lookup/eval/river/compact_prob_count_b.char");

    private static final char PROB_MASK   = 0x7FF;
    private static final char COUNT_SHIFT = 11;

    private static final char PROB_COUNT_A[] =
            retrieveOrCreate(storeFileA, 0, Integer.MAX_VALUE - 1);
    private static final char PROB_COUNT_B[] =
            retrieveOrCreate(storeFileB,
                             Integer.MAX_VALUE,
                             RiverLookup.CANONS - 1);


    //--------------------------------------------------------------------
    private static char[] retrieveOrCreate(
            String file, long from, long to)
    {
        LOG.debug("retrieveOrCreate " + from + "\t" + to);

        Stopwatch t   = new Stopwatch();
        char cached[] = PersistentChars.retrieve(file);
        if (cached != null)
        {
            LOG.debug("took " + t.timing());
            return cached;
        }

        cached = computeProbCounts(from, to);
        PersistentChars.persist(cached, file);
        return cached;
    }


    //--------------------------------------------------------------------
    private static char[] computeProbCounts(
            final long from,
            final long to)
    {
        LOG.debug("computeProbCounts");

        final int  len          = (int) (to - from) + 1;
        final char probCounts[] = new char[ len ];

        RiverEvalLookup.traverse(
            new CanonRange[]{new CanonRange(from, len)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents)
                {
                    probCounts[ (int) (canonIndex - from) ] =
                            encode(strengthVsRandom, represents);
                }
            });

        return probCounts;
    }


    //--------------------------------------------------------------------
    private static char encode(
            double strengthVsRandom,
            byte   represents)
    {
        char compactStr =
                CompactRiverProbabilities.compact(strengthVsRandom);
        byte compactRep = CompactRiverCounts.indexOf(represents);

        return (char)(compactStr |
                      compactRep << COUNT_SHIFT);
    }

    private static char decodeProb(char probCount)
    {
        return (char) (probCount & PROB_MASK);
    }

    private static byte decodeRep(char probCount)
    {
        return (byte)(probCount >>> COUNT_SHIFT);
    }


    //--------------------------------------------------------------------
    public static char compactProb(long riverIndex)
    {
        if (riverIndex < Integer.MAX_VALUE) {
            return decodeProb(PROB_COUNT_A[(int) riverIndex]);
        } else {
            return decodeProb(PROB_COUNT_B[
                    (int) (riverIndex - Integer.MAX_VALUE)]);
        }
    }

    public static byte compactCount(long riverIndex)
    {
        if (riverIndex < Integer.MAX_VALUE) {
            return decodeRep(PROB_COUNT_A[(int) riverIndex]);
        } else {
            return decodeRep(PROB_COUNT_B[
                    (int) (riverIndex - Integer.MAX_VALUE)]);
        }
    }

    public static byte riverCount(long riverIndex)
    {
        return CompactRiverCounts.count(
                compactCount(riverIndex));
    }
}
