package ao.holdem.abs.bucket.abstraction.bucketize.linear;

/**
 * User: alex
 * Date: 6-Feb-2010
 * Time: 1:34:12 PM
 */
public class BucketError
{
    //--------------------------------------------------------------------
    private final double[] sums;
    private final int   [] counts;

    private       double   squaredErrorSum;
    private       int      errorCount;


    //--------------------------------------------------------------------
    public BucketError(int nBuckets)
    {
        sums   = new double[ nBuckets ];
        counts = new int   [ nBuckets ];
    }


    //--------------------------------------------------------------------
    public void add(int bucket, double strength)
    {
        sums  [ bucket ] += strength;
        counts[ bucket ]++;
    }


    //--------------------------------------------------------------------
    public void check(int bucket, double strength)
    {
        double mean  = sums[bucket] / counts[bucket];
        double delta = mean - strength;

        squaredErrorSum += delta * delta;
        errorCount++;
    }


    //--------------------------------------------------------------------
    public double error()
    {
        return (errorCount == 0)
               ? 0 : Math.sqrt( squaredErrorSum / errorCount );
    }
}
