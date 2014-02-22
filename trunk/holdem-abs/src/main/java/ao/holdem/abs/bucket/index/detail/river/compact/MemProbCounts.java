package ao.holdem.abs.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.holdem.canon.river.River;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.river.RiverEvalLookup;
import ao.util.io.Dirs;
import ao.util.time.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Date: 9-Jul-2009
 * Time: 10:16:28 PM
 */
public enum MemProbCounts
{;
    //--------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger(MemProbCounts.class);


    //--------------------------------------------------------------------
    private static final File dir        =
            Dirs.get(Infrastructure.path("lookup/eval/river"));

    private static final File storeFileDir =
            new File(dir, "compact_prob_count");

    private static final char PROB_MASK   = 0x7FF;
    private static final char COUNT_SHIFT = 11;

    private static final CharArray64 PROB_COUNT =
            retrieveOrCreate(storeFileDir);


    //--------------------------------------------------------------------
    private static CharArray64 retrieveOrCreate(File path)
    {
        LOG.info("retrieveOrCreate: {}", path);

        Stopwatch t = new Stopwatch();
        CharArray64 counts = CharArray64.read(River.CANONS, path);

        if (counts != null)
        {
            LOG.info("retrieved in: " + t.timing());
            return counts;
        }

        counts = computeProbCounts();
        counts.write(path);
        return counts;
    }


    //--------------------------------------------------------------------
    private static CharArray64 computeProbCounts()
    {
        LOG.info("computeProbCounts");

        final CharArray64 probCounts = new CharArray64(River.CANONS);

        RiverEvalLookup.traverse(
            new CanonRange[]{CanonRange.newFromCount(
                    0, River.CANONS)},
//                    0, 100)},
            new RiverEvalLookup.VsRandomVisitor() {
                public void traverse(
                        long   canonIndex,
                        double strengthVsRandom,
                        byte   represents)
                {
//                    System.out.println(
//        (int) CompactRiverProbabilities.compact(strengthVsRandom) + "\t" +
//        represents + "\t" + CompactRiverCounts.indexOf(represents));

                    assert probCounts.get(canonIndex) == 0 : canonIndex;

                    probCounts.set(canonIndex, encode(strengthVsRandom, represents));

//                    assert decodeProb(probCounts[ index ]) ==
//                            CompactRiverProbabilities.compact(
//                                    strengthVsRandom)
//                            : canonIndex + "\t" + index;
//                    assert decodeRep(encode(
//                            strengthVsRandom, represents)) ==
//                                CompactRiverCounts.indexOf(represents)
//                            : canonIndex + "\t" + index;
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
        return (byte) (probCount >>> COUNT_SHIFT);
    }


    //--------------------------------------------------------------------
    public static char compactNonLossProbability(long riverIndex)
    {
        return CompactRiverProbabilities.nonLossProbability(
                 compactProb( riverIndex ));
    }
    public static char compactProb(long riverIndex)
    {
        return decodeProb(PROB_COUNT.get(riverIndex));
    }

    public static byte compactCount(long riverIndex)
    {
        return decodeRep(PROB_COUNT.get(riverIndex));
    }

    public static byte riverCount(long riverIndex)
    {
        return CompactRiverCounts.count(
                compactCount(riverIndex));
    }
    public static byte normRiverCount(long riverIndex)
    {
        return CompactRiverCounts.normCount(
                compactCount(riverIndex));
    }
}
