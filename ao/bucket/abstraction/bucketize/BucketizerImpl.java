package ao.bucket.abstraction.bucketize;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.index.detail.CanonDetail;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;

/**
 * User: iscott
 * Date: Jan 9, 2009
 * Time: 10:24:57 AM
 */
public class BucketizerImpl implements Bucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerImpl.class);


    //--------------------------------------------------------------------
    public boolean bucketize(Branch branch, byte nBuckets)
    {
        assert nBuckets > 0;
//        if (branch.isBucketized()) return false;

        CanonDetail[] details = branch.details();

        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  details.length + " into " + nBuckets);

        Arrays.sort(details, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                        a.strength(),
                        b.strength());
            }
        });

        BucketAllocator alloc = new BucketAllocator(
                          details.length, (char) nBuckets);
        for (CanonDetail detail : details)
        {
            branch.set(detail.canonIndex(),
                       (byte) alloc.nextBucket(1));
        }
        return true;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return "odds_homo";
    }

    @Override
    public String toString()
    {
        return id();
    }
}
