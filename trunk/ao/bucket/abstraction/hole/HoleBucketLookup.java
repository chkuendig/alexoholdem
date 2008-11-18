package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.abstraction.set.BucketSetImpl;
import ao.util.data.AutovivifiedList;

import java.util.List;

/**
 * Date: Oct 14, 2008
 * Time: 10:59:13 PM
 */
public class HoleBucketLookup
{
    //--------------------------------------------------------------------
    private final static String DIR = "lookup/bucket/hole/";


    //--------------------------------------------------------------------
    private final List<BucketSetImpl> CACHE;
    private final HoleBucketizer     BUCKETIZER;


    //--------------------------------------------------------------------
    public HoleBucketLookup(HoleBucketizer bucketizer)
    {
        BUCKETIZER = bucketizer;
        CACHE      = new AutovivifiedList<BucketSetImpl>();
    }


    //--------------------------------------------------------------------
    public BucketSet buckets(char totalHoleBuckets)
    {
        BucketSetImpl cache = CACHE.get( totalHoleBuckets );
        if (cache != null) return cache;

        String file = bucketFile( totalHoleBuckets );
        cache = BucketSetImpl.retrieve( file );
        if (cache == null)
        {
            cache = BUCKETIZER.bucketize( totalHoleBuckets );
            BucketSetImpl.persist(cache, file);
        }
        
        CACHE.set(totalHoleBuckets, cache);
        return cache;
    }


    //--------------------------------------------------------------------
    private String bucketFile(int totalHoleBuckets)
    {
        return DIR + BUCKETIZER.id() +
               "." + totalHoleBuckets + ".cache";
    }
}
