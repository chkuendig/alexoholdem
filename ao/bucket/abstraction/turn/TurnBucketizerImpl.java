package ao.bucket.abstraction.turn;

import ao.bucket.abstraction.alloc.BucketAllocator;
import ao.bucket.abstraction.alloc.SubBucketAllocator;
import ao.bucket.abstraction.community.CommunityBucketizer;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.turn.TurnLookup;
import ao.util.data.IntList;
import org.apache.log4j.Logger;

/**
 * Date: Dec 26, 2008
 * Time: 4:34:23 PM
 */
public class TurnBucketizerImpl implements CommunityBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnBucketizerImpl.class);


    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize(BucketSet            onTopOf,
                      char                 numBuckets,
                      BucketSet.Builder<T> with)
    {
        assert numBuckets >= 1;
        LOG.debug("bucketizing flop into " +
                  (int)(numBuckets) + " buckets");

        T buckets = with.newInstance(
                      TurnLookup.CANON_TURN_COUNT, numBuckets);

        char   bucketOffset = 0;
        char[] bucketAlloc  = new SubBucketAllocator().allocate(
                onTopOf.bucketCount(), numBuckets);
        for (char bucket = 0; bucket < numBuckets; bucket++)
        {
            bucketize(
                    buckets,
                    byMean(onTopOf.canonsOf( bucket )),
                    bucketAlloc[ bucket ],
                    bucketOffset);

            bucketOffset += bucketAlloc[ bucket ];
        }

        return buckets;
    }


    //--------------------------------------------------------------------
    private void
            bucketize(BucketSet into,
                      IntList[] meanStrata,
                      char      numBuckets,
                      char      bucketOffset)
    {
        BucketAllocator alloc = new BucketAllocator(
                IntList.sizeSum(meanStrata), numBuckets);

        for (IntList stratum : meanStrata)
        {
            if (stratum == null) continue;

            for (int i = stratum.size() - 1; i >= 0; i--)
            {
                into.add(stratum.get(i),
                         (char)(bucketOffset +
                                  alloc.nextBucket(stratum.size())));
            }
        }
    }


    //--------------------------------------------------------------------
    private IntList[] byMean(long[] forFlops)
    {
        LOG.debug("sorting turns by mean for " +
                   (forFlops == null ? "all" : forFlops.length)
                  + " canon flops");

        return null;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return getClass().getSimpleName();
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return id();
    }
}
