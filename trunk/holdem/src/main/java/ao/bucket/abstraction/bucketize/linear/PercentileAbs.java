package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.bucketize.def.ScalarBucketizer;
import ao.bucket.abstraction.bucketize.error.HandStrengthMeasure;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.range.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.holdem.model.Round;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;

/**
 * User: alex
 * Date: 5-May-2009
 * Time: 8:51:58 PM
 */
public class PercentileAbs implements ScalarBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HandStrengthAbs.class);


    //--------------------------------------------------------------------
    public double bucketize(BucketTree.Branch branch, int nBuckets)
    {
        assert nBuckets > 0;

        if (branch.round() == Round.RIVER) {
            return bucketizeRiver(branch, nBuckets);
        } else {
            return bucketizeByList(branch, nBuckets);
        }
    }

    public double bucketize(
            BucketTree.Branch   branch,
            IndexedStrengthList details,
            int                 numBuckets) {
        return bucketize(branch, numBuckets);
    }


    //--------------------------------------------------------------------
    private double bucketizeByList(BucketTree.Branch branch, int nBuckets)
    {
        LOG.debug("bucketizing " + branch.round() + " into " + nBuckets);

//        IndexedStrengthList strengthList =
//                IndexedStrengthList.strengths(branch);

        CanonDetail[] details = branch.details();
        Arrays.sort(details, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                         a.strength(), b.strength());
            }
        });

//        BucketError     error = new BucketError( nBuckets );
        BucketAllocator alloc = new BucketAllocator(
                          details.length, (char) nBuckets);
        for (CanonDetail detail : details)
        {
            long   index    = detail.canonIndex();
            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = detail.strength();

//            error.add(bucket, strength);
            branch.set(index, bucket);
        }

//        for (CanonDetail detail : details)
//        {
//            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = detail.strength();
//
//            error.check(bucket, strength);
//        }

//        return error.error();
        return new HandStrengthMeasure().error(branch, nBuckets);
    }


    //--------------------------------------------------------------------
    private double bucketizeRiver(
            final BucketTree.Branch branch, final int nBuckets)
    {
        assert nBuckets > 0;

        int        nRivers       = 0;
        CanonRange toBucketize[] =
                new CanonRange[ branch.parentCanons().length ];
        for (int i = 0; i < branch.parentCanons().length; i++) {

            int canonTurn = branch.parentCanons()[i];
            toBucketize[ i ] = TurnDetails.lookup(canonTurn).range();
            nRivers += toBucketize[ i ].count();
        }
        Arrays.sort(toBucketize);

        LOG.debug("bucketizing " + branch.round() +
                  " into " + nBuckets +
                  " (p " + branch.parentCanons().length +
                  ", c " + nRivers   +
                  ")");

        final int             nextIndex[] = {0};
        final IndexedStrength rivers   [] =
                new IndexedStrength[ nRivers ];
        RiverEvalLookup.traverse(
                toBucketize,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {

                        rivers[ nextIndex[0]++ ] = new IndexedStrength(
                                canonIndex, strengthVsRandom);
                    }
                });
        Arrays.sort(rivers);

//        BucketError     error = new BucketError( nBuckets );
        BucketAllocator alloc =
                new BucketAllocator(nRivers, (char) nBuckets);
        for (IndexedStrength river : rivers)
        {
//            long   index    = river.index();
//            int    bucket   = (int) alloc.nextBucket(1);
//            double strength = river.strength();

            branch.set(river.index(),
                       (byte) alloc.nextBucket(1));
        }
        
        return new HandStrengthMeasure().error(branch, nBuckets);
    }


    //--------------------------------------------------------------------
    public void setThorough(boolean highPercision) {}


    //--------------------------------------------------------------------
    public String id()
    {
        return "percent";
    }

    @Override public String toString()
    {
        return id();
    }
}
