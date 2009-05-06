package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.holdem.model.Round;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.BitSet;

/**
 * User: iscott
 * Date: Jan 9, 2009
 * Time: 10:24:57 AM
 */
public class HandStrengthBucketizer implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HandStrengthBucketizer.class);

    private static final double DEFAULT_MIN = 0.002014190890363928;
    private static final double DEFAULT_MAX = 1.0;

    private static       double prevMin     = DEFAULT_MIN;
    private static       double prevMax     = DEFAULT_MAX;


    //--------------------------------------------------------------------
    public boolean bucketize(Branch branch, byte nBuckets)
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

        return true;
    }


    //--------------------------------------------------------------------
    private void bucketizeByList(Branch branch, byte nBuckets)
    {
        CanonDetail[] details = branch.details();

        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  details.length + " into " + nBuckets);

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
            byte bucket = (byte)(norm * nBuckets);
            branch.set(detail.canonIndex(), bucket);

            seen.set(bucket);
        }

        if (seen.nextClearBit(0) != seen.length()) {
            final byte[] map  = new byte[ nBuckets ];
            for (byte i = 0, j = 0; i < map.length; i++) {
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
                byte bucket = (byte)(norm * nBuckets);
                byte fixed  = map[bucket];

                branch.set(detail.canonIndex(), fixed);
                seen.set(fixed);
            }

            LOG.debug(
                    "Fixed gap " + seen);
        }
    }


    //--------------------------------------------------------------------
    private void bucketizeRiver(
            final Branch branch, final byte nBuckets)
    {
        assert nBuckets > 0;
//        LOG.debug("scanning " + branch.round());

        int        nRivers       = 0;
        CanonRange toBucketize[] =
                new CanonRange[ branch.parentCanons().length ];
        for (int i = 0; i < branch.parentCanons().length; i++) {

            int canonTurn = branch.parentCanons()[i];
            toBucketize[ i ] = TurnDetails.lookup(canonTurn).range();
            nRivers += toBucketize[ i ].canonIndexCount();
        }
        Arrays.sort(toBucketize);

        LOG.debug("bucketizing " + branch.round() +
                  " into " + nBuckets +
                  " (p " + branch.parentCanons().length +
                  ", c " + nRivers   +
                  ")");

        Stopwatch    time  = new Stopwatch();
        final double min[] = {Double.MAX_VALUE}, max[] = {-1};
        BitSet seen = setBucketsAndScopeStrengthRange(
                branch, nBuckets, toBucketize, min, max);
        if (seen.nextClearBit(0) != seen.length()) {
            LOG.debug("Fixing gap " + seen);
            seen.flip(0, seen.length());

            BitSet fixed = setBucketsGetBounds(
                branch, nBuckets, toBucketize,
                min, max, min[0], max[0], seen);
            LOG.debug("Fixed to " + fixed);
        }
        System.out.print("  took " + time.timing());
    }

    private BitSet setBucketsAndScopeStrengthRange(
            final Branch branch,
            final byte nBuckets,
            final CanonRange toBucketize[],
            final double setMin[], final double setMax[])
    {
        BitSet seen = setBucketsGetBounds(
                branch, nBuckets, toBucketize,
                setMin, setMax, prevMin, prevMax,
                new BitSet());

        if ((setMin[0] != prevMin ||
                setMax[0] != prevMax) &&
                (seen.nextClearBit(0) == seen.length())) {
            LOG.debug("Adjusting range to " +
                        setMin[0] + ", " + setMax[0]);
            seen = setBucketsGetBounds(
                branch, nBuckets, toBucketize,
                setMin, setMax, setMin[0], setMax[0],
                new BitSet());
        }

        prevMin = setMin[0];
        prevMax = setMax[0];

        return seen;
    }
    private BitSet setBucketsGetBounds(
            final Branch branch,
            final byte nBuckets,
            final CanonRange toBucketize[],
            final double setMim[], final double setMax[],
            final double min, final double max,
            final BitSet skip)
    {
        final byte[] map  = new byte[ nBuckets ];
        for (byte i = 0, j = 0; i < map.length; i++) {
            map[i] = j;
            if (! skip.get(i)) j++;
        }

        final BitSet seen = new BitSet();
        RiverEvalLookup.traverse(
                toBucketize,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long canonIndex, double strengthVsRandom) {

                        if (setMim[0] > strengthVsRandom) {
                            setMim[0] = strengthVsRandom;
                        }
                        if (setMax[0] < strengthVsRandom) {
                            setMax[0] = strengthVsRandom;
                        }

                        double norm = Math.min(
                                (strengthVsRandom - min)
                                        / (max    - min),
                                0.99999); // [0, 1) instead of [0, 1]

                        byte bucket = (byte)(norm * nBuckets);
                        byte fixed  = map[bucket];

                        branch.set(canonIndex, fixed);
                        seen.set(fixed);
                    }
                });
        return seen;
    }


//    //--------------------------------------------------------------------
//    private LongBitSet toRetrieve(
//            long offset, long length, Branch branch)
//    {
//        LongBitSet retrieve = new LongBitSet(length);
//        for(int canonTurn : branch.parentCanons()) {
//            TurnDetailFlyweight.CanonTurnDetail
//                    turn = TurnDetails.lookup(canonTurn);
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


//    //--------------------------------------------------------------------
//    private boolean bucketizeTenHoles(Branch branch) {
//        CanonDetail[] details = branch.details();
//
//        LOG.debug("bucketizing " + branch.round() + " branch of " +
//                  details.length + " into " + 10 + " using PokerRoom");
//
//        for (CanonDetail detail : details)
//        {
//            Hole hole = HoleLookup.lookup((int)
//                    detail.canonIndex()).reify();
//            branch.set(detail.canonIndex(),
//                       (byte)(PokerRoom.groupOf( hole ) - 1));
//        }
//        return true;
//    }


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
