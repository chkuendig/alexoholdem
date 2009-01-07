package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.set.BucketSet;

/**
 * Date: Jan 7, 2009
 * Time: 8:32:21 AM
 */
public interface HoleBucketLookup
{
    //--------------------------------------------------------------------
    BucketSet buckets(char totalHoleBuckets);
}
