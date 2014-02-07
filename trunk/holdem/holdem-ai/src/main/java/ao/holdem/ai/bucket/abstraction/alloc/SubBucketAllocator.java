package ao.holdem.ai.bucket.abstraction.alloc;

/**
 * Date: Jan 7, 2009
 * Time: 2:00:33 PM
 */
public class SubBucketAllocator
{
    /**
     * @param parentBuckets number of buckets in previous round
     * @param numBuckets total buckets for this round
     * @return how many buckets for this round per
     *          each of the buckets in the previous round
     */
    public byte[] allocate(
            char parentBuckets,
            char numBuckets)
    {
        byte[] alloc = new byte[ parentBuckets ];

        double allocOverflow   = 0;
        double idealAllocation =
                ((double) numBuckets) / parentBuckets;

        char   bucketsUsedUp = 0;
        for (char bucketIndex = 0,
                      lastBucket  = (char) (parentBuckets - 1);
                  bucketIndex <= lastBucket;
                  bucketIndex++)
        {
            byte numSubBuckets;
            if (bucketIndex == lastBucket)
            {
                numSubBuckets = (byte) (numBuckets - bucketsUsedUp);
            }
            else
            {
                numSubBuckets =
                        (byte) Math.floor(idealAllocation);
                allocOverflow +=
                        idealAllocation - numSubBuckets;

                if (allocOverflow >= 1)
                {
                    numSubBuckets++;
                    allocOverflow--;
                }
            }

            alloc[ bucketIndex ] = numSubBuckets;
            bucketsUsedUp += numSubBuckets;
        }

        return alloc;
    }
}
