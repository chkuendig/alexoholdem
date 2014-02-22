package ao.holdem.abs.bucket.abstraction.bucketize.error;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.bucketize.linear.IndexedStrengthList;

/**
 * User: Alex
 * Date: 14-May-2009
 * Time: 5:45:40 PM
 */
public class HandStrengthMeasure
{
    //--------------------------------------------------------------------
    public double error(
            BucketTree.Branch branch,
            int               nBuckets)
    {
        IndexedStrengthList details =
                IndexedStrengthList.strengths(branch);
        return error(branch, details, nBuckets);
    }
    public double error(
            BucketTree.Branch   branch,
            IndexedStrengthList details,
            int                 nBuckets)
    {
        double[] sums   = new double[ nBuckets ];
        int   [] counts = new int   [ nBuckets ];

        for (int i = 0; i < details.length(); i++)
        {
            int bucket = branch.get(details.index(i));

            sums  [ bucket ] += details.realStrength(i) *
                                  details.represents(i);
            counts[ bucket ] += details.represents(i);
        }

        double[] means = new double[ nBuckets ];
        for (int i = 0; i < nBuckets; i++)
        {
            means[ i ] = (counts[ i ] == 0)
                         ? 0 : sums[ i ] / counts[ i ];
        }

        double error = 0;
        long   total = 0;
        for (int i = 0; i < details.length(); i++)
        {
            int    bucket = branch.get(details.index(i));
            double delta  = details.realStrength(i)
                              - means[bucket];

            error += delta * delta * details.represents(i);
            total += details.represents(i);
        }

        return total == 0
               ? 0 : Math.sqrt(error / total);
    }
}
