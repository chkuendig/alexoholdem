package ao.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.bucket.index.canon.river.RiverLookup;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.ProbabilityEncoding;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.util.persist.PersistentInts;

/**
 * User: alex
 * Date: 1-Jul-2009
 * Time: 9:46:54 PM
 */
public class ProbabilityCounts
{
    //--------------------------------------------------------------------
    private ProbabilityCounts() {}

    public static void main(String[] args)
    {
        for (int i = 0; i < ProbabilityEncoding.COUNT; i++)
        {
            System.out.println(
                    i + "\t" + countOf((char) i));
        }
    }


    //--------------------------------------------------------------------
    private static final String storeFile   = Infrastructure.path(
                        "lookup/canon/detail/river/prob_count.int");

    private static final int    probCount[] = probCounts();


    //--------------------------------------------------------------------
    private static int[] probCounts()
    {
        int counts[] = PersistentInts.retrieve(storeFile);
        if (counts != null) return counts;

        counts = rawProbCounts();
        PersistentInts.persist(counts, storeFile);
        return counts;
    }


    //--------------------------------------------------------------------
    private static int[] rawProbCounts()
    {
        final int counts[] = new int[ ProbabilityEncoding.COUNT ];
        RiverEvalLookup.traverse(
            new CanonRange[]{CanonRange.newFromCount(
                    0, RiverLookup.CANONS)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents)
                {
                    char strAsChar =
                            ProbabilityEncoding.encodeWinProb(
                                    strengthVsRandom);
                    counts[ strAsChar ] += represents;
                }
            });
        return counts;
    }


    //--------------------------------------------------------------------
    /**
     * @param rawProbability countOf
     * @return how many times the given probability appears
     *          in all card sequences that reach the river.
     */
    public static int countOf(char rawProbability)
    {
        return probCount[ rawProbability ];
    }
}
