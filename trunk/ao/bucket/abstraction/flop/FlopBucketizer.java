package ao.bucket.abstraction.flop;

/**
 * Date: Oct 17, 2008
 * Time: 1:47:45 PM
 */
public interface FlopBucketizer
{
    //--------------------------------------------------------------------
    int[][] bucketize(int numBuckets);


    //--------------------------------------------------------------------
    public static interface Factory
    {
        public FlopBucketizer newInstance(short canonHoles[]);
    }
}
