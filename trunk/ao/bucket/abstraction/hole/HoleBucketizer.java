package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.set.BucketSet;


/**
 * Date: Oct 14, 2008
 * Time: 11:00:28 PM
 */
public interface HoleBucketizer
{
    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize( char nBuckets, BucketSet.Builder<T> into );


    //--------------------------------------------------------------------
    public String id();
}
