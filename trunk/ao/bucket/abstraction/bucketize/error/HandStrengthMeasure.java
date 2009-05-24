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
        IndexedStrengthList strengths =
                IndexedStrengthList.strengths(branch);

        double error = 0;
        for (byte bucket = 0; bucket < nBuckets; bucket++)
        {
            double sum   = 0;
            int    count = 0;
            for (int i = 0; i < strengths.length(); i++)
            {
                if (bucket != branch.get(strengths.index(i))) continue;

                sum += strengths.strength(i);
                count++;
            }

            double mean  = sum / count;
            for (int i = 0; i < strengths.length(); i++)
            {
                if (bucket != branch.get(strengths.index(i))) continue;

                error += Math.pow(strengths.strength(i) - mean, 2);
            }
        }

        return Math.sqrt(error);
    }
}
