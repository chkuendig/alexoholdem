package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.set.BucketSetImpl;


/**
 * Date: Oct 14, 2008
 * Time: 11:00:28 PM
 */
public interface HoleBucketizer
{
    //--------------------------------------------------------------------
    public BucketSetImpl bucketize( char nBuckets );


    //--------------------------------------------------------------------
    public String id();
}
