package ao.holdem.ai.abstraction.alloc;

/**
 * Date: Jan 7, 2009
 * Time: 2:15:35 PM
 */
public class BucketAllocator
{
    //--------------------------------------------------------------------
    private char   nextBucket;
    private char   lastBucket;
    private int    cummFill;
    private double perBucket;


    //--------------------------------------------------------------------
    public BucketAllocator(
            long canonCount,
            char bucketCount)
    {
        cummFill   = 0;
        nextBucket = 0;
        lastBucket = (char)(bucketCount - 1);
        perBucket  = ((double) canonCount) / bucketCount;
    }


    //--------------------------------------------------------------------
    public char nextBucket(int meanStratumSize)
    {
        double delta = cummFill - perBucket * (nextBucket + 1);

        if (nextBucket != lastBucket)
        {
            if (delta < 0)
            {
                double overflow = delta + meanStratumSize;
                if (overflow > meanStratumSize / 2)
                {
                    nextBucket++;
                }
            }
            else
            {
                nextBucket++;
            }
        }

        cummFill += meanStratumSize;
        return nextBucket;
    }
}
