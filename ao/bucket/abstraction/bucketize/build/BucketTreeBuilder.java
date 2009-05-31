package ao.bucket.abstraction.bucketize.build;

import ao.bucket.abstraction.access.tree.BucketTree;

import java.io.File;

/**
 * User: Cross Creek Marina
 * Date: 21-May-2009
 * Time: 6:08:04 PM
 */
public interface BucketTreeBuilder
{
    //--------------------------------------------------------------------
    public BucketTree bucketize(
            File dir,
            byte numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets);
}