package ao.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.bucket.index.detail.river.ProbabilityEncoding;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 1-Jul-2009
 * Time: 8:24:12 PM
 */
public class CompactProbabilityCounts
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CompactProbabilityCounts.class);

    private CompactProbabilityCounts() {}

    public static void main(String[] args) {
        for (int i = 0; i < CompactRiverProbabilities.COUNT; i++) {
            LOG.info(i + "\t" + countOf((char) i));
        }
    }


    //--------------------------------------------------------------------
    private static final String storeFile      = Infrastructure.path(
                        "lookup/canon/detail/river/compact_count.int");

    private static final int    compactCount[] = compactCounts();


    //--------------------------------------------------------------------
    private static int[] compactCounts()
    {
        int counts[] = PersistentInts.retrieve(storeFile);
        if (counts != null) return counts;

        counts = compactProbCounts();
        PersistentInts.persist(counts, storeFile);
        return counts;
    }


    //--------------------------------------------------------------------
    private static int[] compactProbCounts()
    {
        int counts[] = new int[ CompactRiverProbabilities.COUNT ];
        for (int i = 0; i < ProbabilityEncoding.COUNT; i++)
        {
            if (! CompactRiverProbabilities.isValidRaw(
                    (char) i)) continue;

            counts[ CompactRiverProbabilities.compact((char) i) ] +=
                    ProbabilityCounts.countOf((char) i);
        }
        return counts;
    }


    //--------------------------------------------------------------------
    /**
     * @param compactProbability countOf
     * @return how many times the given compactProbability appears
     *          in all card sequences that reach the river.
     */
    public static int countOf(char compactProbability)
    {
        return compactCount[ compactProbability ];
    }
}
