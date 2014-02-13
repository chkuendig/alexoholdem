package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.abs.bucket.index.detail.river.ProbabilityEncoding;
import ao.holdem.abs.bucket.index.detail.river.RiverEvalLookup;
import ao.holdem.canon.river.River;

import java.io.IOException;
import java.util.Arrays;

/**
 * 13/02/14 6:45 PM
 */
public class RiverEvalLookupTest
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
                        0, River.CANONS - 1)},
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long canonIndex,
                            double strengthVsRandom,
                            byte represents) {

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

        System.out.println(Arrays.toString(byStrength));
    }
}
