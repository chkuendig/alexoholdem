package ao.bucket.abstraction.community;

import ao.bucket.abstraction.set.BucketSet;

/**
 * Date: Dec 26, 2008
 * Time: 4:20:04 PM
 */
public interface CommunityBucketLookup
{
    //--------------------------------------------------------------------
    public BucketSet buckets(
            BucketSet onTopOf,
            char      flopBucketCount);
}
