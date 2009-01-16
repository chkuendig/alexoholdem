package ao.bucket.abstraction.bucketize;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.tree.BucketTree.Branch;
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
    public void bucketize(Branch branch, byte nBuckets)
    {
        assert nBuckets > 0;
        if (branch.isBucketized()) return;

        CanonDetail[][] details = branch.details();
        int detailCount = countDetails(details);

        LOG.debug("bucketizing " + branch.round() + " branch of " +
                  detailCount + " into " + nBuckets);

        BucketAllocator alloc = new BucketAllocator(
                          detailCount, (char) nBuckets);
        for (CanonDetail canonDetail :
                sortCanonDetail(details, detailCount))
        {
            branch.set(canonDetail.canonIndex(),
                       (byte) alloc.nextBucket(1));
        }
    }

    private int countDetails(CanonDetail[][] details)
    {
        int count = 0;
        for (CanonDetail[] detailList : details)
            count += detailList.length;
        return count;
    }


    //--------------------------------------------------------------------
    private CanonDetail[] sortCanonDetail(
            CanonDetail[][] details,
            int             detailCount)
    {
        int           i    = 0;
        CanonDetail[] flat = new CanonDetail[ detailCount ];
        for (CanonDetail[] detailList : details) {
            for (CanonDetail detail : detailList) {
                flat[ i++ ] = detail;
            }
        }

        Arrays.sort(flat, new Comparator<CanonDetail>() {
            public int compare(CanonDetail a, CanonDetail b) {
                return Double.compare(
                        a.strengthVsRandom(),
                        b.strengthVsRandom());
            }
        });

        return flat;
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
