package ao.bucket.abstraction.bucketize;

import ao.bucket.abstraction.access.tree.BucketTree.Branch;
import ao.bucket.abstraction.bucketize.linear.IndexedStrengthList;

/**
 * Date: Jan 9, 2009
 * Time: 10:01:28 AM
 */
public interface Bucketizer
{
    // returns the bucketing error, in some distance metric
    public double bucketize(Branch branch,
                            byte   numBuckets);

    // returns the error
    public double bucketize(Branch              branch,
                            IndexedStrengthList details,
                            byte                numBuckets);

    public String id();
}
