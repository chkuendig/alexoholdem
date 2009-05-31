package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonRange;
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
public class PercentileAbs implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HandStrengthAbs.class);


    //--------------------------------------------------------------------
    public boolean bucketize(BucketTree.Branch branch, byte nBuckets)
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

    public boolean bucketize(
            BucketTree.Branch   branch,
            IndexedStrengthList details,
            byte                numBuckets) {
        return bucketize(branch, numBuckets);
    }


    //--------------------------------------------------------------------
    private void bucketizeByList(BucketTree.Branch branch, byte nBuckets)
    {
        LOG.debug("bucketizing " + branch.round() + " into " + nBuckets);

        CanonDetail[] details = branch.details();
        Arrays.sort(details, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                         a.strength(), b.strength());
            }
        });

        BucketAllocator alloc = new BucketAllocator(
                          details.length, (char) nBuckets);
        for (CanonDetail detail : details)
        {
            branch.set(detail.canonIndex(),
                       (byte) alloc.nextBucket(1));
        }
    }


    //--------------------------------------------------------------------
    private void bucketizeRiver(
            final BucketTree.Branch branch, final byte nBuckets)
    {
        assert nBuckets > 0;

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

        BucketAllocator alloc =
                new BucketAllocator(nRivers, (char) nBuckets);
        for (IndexedStrength river : rivers)
        {
            branch.set(river.index(),
                       (byte) alloc.nextBucket(1));
        }
    }


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
