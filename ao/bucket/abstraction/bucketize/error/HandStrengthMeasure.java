package ao.bucket.abstraction.bucketize.error;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.linear.IndexedStrengthList;

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
            byte              nBuckets)
    {
        IndexedStrengthList details =
                IndexedStrengthList.strengths(branch);

        double error = 0;
        int    total = 0;
        for (byte bucket = 0; bucket < nBuckets; bucket++)
        {
            double sum   = 0;
            int    count = 0;
            for (int i = 0; i < details.length(); i++)
            {
                if (bucket != branch.get(details.index(i))) continue;

                sum   += details.strengthNorm(i) *
                         details.represents(i);
                count += details.represents(i);
            }

            double mean  = sum / count;
            for (int i = 0; i < details.length(); i++)
            {
                if (bucket != branch.get(details.index(i))) continue;

                error += Math.pow(details.strengthNorm(i) - mean, 2) *
                         details.represents(i);
                total += details.represents(i);
            }
        }

        return Math.sqrt(error / total);
    }
}
