package ao.holdem.abs.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.holdem.canon.river.River;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.river.ProbabilityEncoding;
import ao.holdem.abs.bucket.index.detail.river.RiverEvalLookup;
import ao.util.data.primitive.CharList;
import ao.util.io.Dirs;
import ao.util.persist.PersistentChars;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Date: 1-Jul-2009
 * Time: 5:58:02 PM
 */
public enum CompactRiverProbabilities
{;
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CompactRiverProbabilities.class);


    //--------------------------------------------------------------------
    private static final File dir = Dirs.get(Infrastructure.path(
            "lookup/eval/river"));

    private static final File compactFile =
            new File(dir, "compact_prob.char");
    private static final File unCompactFile =
            new File(dir, "uncompact_prob.char");

    private static final char[]    COMPACT;
    private static final char[] UN_COMPACT;

    static
    {
        char[][] compactUncompact = compactUncompact();

           COMPACT = compactUncompact[ 0 ];
        UN_COMPACT = compactUncompact[ 1 ];
    }


    public  static final int    COUNT = 1950;
                                    // COMPACT[ COMPACT.length - 1 ] + 1;


    //--------------------------------------------------------------------
    private static char[][] compactUncompact()
    {
        LOG.debug("attempting to retrieve or compute");
        Stopwatch timer = new Stopwatch();

        char   compact[] = PersistentChars.retrieve(  compactFile);
        char unCompact[] = PersistentChars.retrieve(unCompactFile);
        if (compact != null && unCompact != null) {
            LOG.debug("retrieved, took " + timer);
            return new char[][]{compact, unCompact};
        }

        LOG.debug("computing");
        char[][] compactUncompact = computeCompactUncompact();
        PersistentChars.persist(compactUncompact[0],   compactFile);
        PersistentChars.persist(compactUncompact[1], unCompactFile);
        
        LOG.debug("done computing, took " + timer);
        return compactUncompact;
    }


    //--------------------------------------------------------------------
    private static char[][] computeCompactUncompact()
    {
        char     usedStrengths[] = usedStrengths();

        char     nextCompact     = 0;
        char     compact[]       = new char[ ProbabilityEncoding.COUNT ];

        CharList unCompact       = new CharList();

        for (int i = 0; i < compact.length; i++) {
            boolean used = (Arrays.binarySearch(
                                     usedStrengths, (char) i) >= 0);

            if (used) {
                compact[ i ] = nextCompact++;
                unCompact.add((char) i);
            } else {
                compact[ i ] = Character.MAX_VALUE;
            }
        }

        return new char[][]{compact, unCompact.toCharArray()};
    }


    //--------------------------------------------------------------------
    private static char[] usedStrengths()
    {
        int[] rawProbCounts = rawProbCounts();

        LOG.debug("computing used strengths");
        CharList usedStrengths = new CharList();
        for (int strength = 0;
                 strength < ProbabilityEncoding.COUNT;
                 strength++) {
            if (rawProbCounts[(char) strength] != 0) {
                usedStrengths.add((char) strength);
            }
        }
        return usedStrengths.toCharArray();
    }

    private static int[] rawProbCounts()
    {
        LOG.debug("computing raw probability counts");

        final int counts[] = new int[ ProbabilityEncoding.COUNT ];
        RiverEvalLookup.traverse(
            new CanonRange[]{CanonRange.newFromCount(
                    0, River.CANONS)},
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
    public static char compact(double nonLossProbability)
    {
        return compact(ProbabilityEncoding.encodeWinProb(
                           nonLossProbability));
    }

    public static char compact(char nonLossProbability)
    {
        assert isValidRaw( nonLossProbability );
        return COMPACT[ nonLossProbability ];
    }

    public static char nonLossProbability(char compact)
    {
        return UN_COMPACT[compact];
    }

    public static boolean isValidRaw(char nonLossProbability)
    {
        return COMPACT[ nonLossProbability ] != Character.MAX_VALUE;
    }
}
