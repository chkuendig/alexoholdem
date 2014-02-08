package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.bucketize.def.ScalarBucketizer;
import ao.bucket.abstraction.bucketize.error.HandStrengthMeasure;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.holdem.model.Round;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Date: Jan 9, 2009
 * Time: 10:24:57 AM
 */
public class HandStrengthAbs implements ScalarBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HandStrengthAbs.class);


    //--------------------------------------------------------------------
    public double bucketize(Branch branch, int nBuckets)
    {
        assert nBuckets > 0;
//        if (branch.isBucketized()) return false;

//        if (branch.round() == Round.PREFLOP && nBuckets == 10) {
//            return bucketizeTenHoles(branch);
//        }
        if (branch.round() == Round.RIVER) {
            bucketizeRiver(branch, nBuckets);
        } else {
            bucketizeByList(branch, nBuckets);
        }

        return new HandStrengthMeasure().error(branch, nBuckets);
    }

    public double bucketize(
            Branch              branch,
            IndexedStrengthList details,
            int                 numBuckets)
    {
        return bucketize(branch, numBuckets);
    }


    //--------------------------------------------------------------------
    private void bucketizeByList(Branch branch, int nBuckets)
    {
        CanonDetail[] details = branch.details();

        double min = Double.MAX_VALUE, max = -1;
        for (CanonDetail detail : details) {
            if (min > detail.strength()) {
                min = detail.strength();
            }
            if (max < detail.strength()) {
                max = detail.strength();
            }
        }

        BitSet seen = new BitSet();
        for (CanonDetail detail : details) {
            double norm = Math.min(
                    (detail.strength() - min) / (max - min),
                    0.999999); // make [0, 1) instead of [0, 1]
            int bucket = (int)(norm * nBuckets);
            branch.set(detail.canonIndex(), bucket);

            seen.set(bucket);
        }

        if (seen.nextClearBit(0) != seen.length()) {
            final int[] map  = new int[ nBuckets ];
            for (int i = 0, j = 0; i < map.length; i++) {
                map[i] = j;

                if (seen.get(i)) {
                    j++;
                }
            }

            seen = new BitSet();
            for (CanonDetail detail : details) {
                double norm = Math.min(
                        (detail.strength() - min) / (max - min),
                        0.999999); // make [0, 1) instead of [0, 1]
                int bucket = (int)(norm * nBuckets);
                int fixed  = map[bucket];

                branch.set(detail.canonIndex(), fixed);
                seen.set(fixed);
            }

//            LOG.debug(
//                    "Fixed gap " + seen);
        }

        LOG.debug("bucketized " + branch.round() + " branch of " +
                  details.length + " into " + seen.nextClearBit(0));
    }


    //--------------------------------------------------------------------
    private void bucketizeRiver(
            final Branch branch, final int nBuckets)
    {
        assert nBuckets > 0;
//        LOG.debug("scanning " + branch.round());

        int        nRivers       = 0;
        CanonRange toBucketize[] =
                new CanonRange[ branch.parentCanons().length ];
        for (int i = 0; i < branch.parentCanons().length; i++) {

            int canonTurn = branch.parentCanons()[i];
            toBucketize[ i ] = TurnDetails.lookup(canonTurn).range();
            nRivers += toBucketize[ i ].count();
        }
        Arrays.sort(toBucketize);

        int          count[] = new int[nBuckets];
        Stopwatch    time    = new Stopwatch();
        final double min[]   = {Double.MAX_VALUE}, max[] = {-1};
        BitSet seen = setStrengthRangeAndBuckets(
                branch, nBuckets, toBucketize, min, max, count);
        if (seen.nextClearBit(0) != seen.length()) {
//            LOG.debug("Fixing gap " + seen);
            seen.flip(0, seen.length());

            count = new int[nBuckets];
            seen  = setBuckets(
                    branch, nBuckets, toBucketize,
                    min[0], max[0], seen, count);
//            LOG.debug("Fixed to " + seen);
        }
//        System.out.print("  took " + time.timing());

        LOG.debug("bucketized " + branch.round() +
                  " into " + seen.nextClearBit(0) +
                  " (p " + branch.parentCanons().length +
                  "\tc " + nRivers   +
                  ")\tin " + time +
                  "\t[" + (int)(min[0] * 100) + ", " +
                         (int)(max[0] * 100) + "] \t" +
                  Arrays.toString(count));
    }

    private BitSet setStrengthRangeAndBuckets(
            Branch     branch,
            int        nBuckets,
            CanonRange toBucketize[],
            double     setMin[],
            double     setMax[],
            int        count[])
    {
        // initiate range
        initBounds(toBucketize, setMin, setMax);
        return setBuckets(
                branch, nBuckets, toBucketize,
                setMin[0], setMax[0],
                new BitSet(), count);
    }

    private BitSet setBuckets(
            final Branch     branch,
            final int        nBuckets,
            final CanonRange toBucketize[],
            final double     min,
            final double     max,
            final BitSet     skip,
            final int        count[])
    {
        final int[] map  = new int[ nBuckets ];
        for (int i = 0, j = 0; i < map.length; i++) {
            map[i] = j;
            if (! skip.get(i)) j++;
        }

        final BitSet seen = new BitSet();
        RiverEvalLookup.traverse(
                toBucketize,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {

                        double norm = Math.min(
                                (strengthVsRandom - min)
                                        / (max    - min),
                                0.99999); // [0, 1) instead of [0, 1]

                        int bucket = (int)(norm * nBuckets);
                        int fixed  = map[bucket];

                        branch.set(canonIndex, fixed);
                        seen.set(fixed);
                        count[fixed]++;
                    }
                });
        return seen;
    }

    private void initBounds(
            final CanonRange toBucketize[],
            final double     setMin[],
            final double     setMax[])
    {
        RiverEvalLookup.traverse(
                toBucketize,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {

                        if (setMin[0] > strengthVsRandom) {
                            setMin[0] = strengthVsRandom;
                        }
                        if (setMax[0] < strengthVsRandom) {
                            setMax[0] = strengthVsRandom;
                        }
                    }
                });
    }


//    //--------------------------------------------------------------------
//    private LongBitSet toRetrieve(
//            long offset, long length, Branch branch)
//    {
//        LongBitSet retrieve = new LongBitSet(length);
//        for(int canonTurn : branch.parentCanons()) {
//            TurnDetailFlyweight.CanonTurnDetail
//                    turn = TurnDetails.compact(canonTurn);
//
//            long upToIncluding = turn.lastCanonRiver();
//            for (long i  = turn.firstCanonRiver();
//                      i <= upToIncluding;
//                      i++) {
//                retrieve.set(i - offset);
//            }
//        }
//        return retrieve;
//    }
//
//
//    //--------------------------------------------------------------------
//    private void reportGap(
//            Branch branch, final byte nBuckets, final int[] rCount,
//            final double[] min, final double[] max, Gapper seen) {
//        seen.displayStatus();
//        System.out.println(seen);
//        System.out.println(min[0] + " .. " + max[0]);
//
//        final int     count[] = {0};
//        final boolean said [] = {false};
//        branch.details(new Traverser<CanonDetail>() {
//            public void traverse(CanonDetail detail) {
//                double norm = Math.min(
//                        (detail.strength() - min[0]) / (max[0] - min[0]),
//                        0.999999); // make [0, 1) instead of [0, 1]
//                byte bucket = (byte)(Math.pow(norm, 2) * nBuckets);
//
//                if ((count[0]++ % (rCount[0] / 100)) == 0) {
////                        System.out.println(
////                                detail.strength() + " " +
////                                norm + " " + bucket);
//                }
//
//                if (bucket == 0 && !said[0]) {
//                    System.out.println(norm + " " + bucket + " wtf?");
//                    said[0] = true;
//                }
//            }
//        });
//    }


    //--------------------------------------------------------------------
    public void setThorough(boolean highPercision) {}


    //--------------------------------------------------------------------
    public String id()
    {
        return "hs";
    }

    @Override
    public String toString()
    {
        return id();
    }
}
