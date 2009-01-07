package ao.bucket.abstraction.community;

import ao.bucket.abstraction.set.BucketSet;


/**
 * Date: Oct 17, 2008
 * Time: 1:47:45 PM
 */
public interface CommunityBucketizer
{
    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize(BucketSet            onTopOf,
                      char                 numBuckets,
                      BucketSet.Builder<T> with);


    //--------------------------------------------------------------------
    public String id();
}
