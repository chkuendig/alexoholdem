package ao.bucket.abstraction.hole;

import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.abstraction.set.BucketSetImpl;
import ao.util.data.AutovivifiedList;

import java.util.List;

/**
 * Date: Oct 14, 2008
 * Time: 10:59:13 PM
 */
public class HoleBucketLookupImpl implements HoleBucketLookup
{
    //--------------------------------------------------------------------
    private final static String DIR = "lookup/bucket/hole/";


    //--------------------------------------------------------------------
    private final List<BucketSet> CACHE;
    private final HoleBucketizer  BUCKETIZER;


    //--------------------------------------------------------------------
    public HoleBucketLookupImpl(HoleBucketizer bucketizer)
    {
        BUCKETIZER = bucketizer;
        CACHE      = new AutovivifiedList<BucketSet>();
    }


    //--------------------------------------------------------------------
    public BucketSet buckets(char totalHoleBuckets)
    {
        BucketSet cache = CACHE.get( totalHoleBuckets );
        if (cache != null) return cache;

        BucketSet.Builder<BucketSetImpl> builder =
                new BucketSetImpl.BuilderImpl(
                        bucketDir( totalHoleBuckets ) );
        cache = builder.retrieve();
        if (cache == null)
        {
            cache = BUCKETIZER.bucketize( totalHoleBuckets, builder );
            cache.persist();
        }
        
        CACHE.set(totalHoleBuckets, cache);
        return cache;
    }


    //--------------------------------------------------------------------
    private String bucketDir(int totalHoleBuckets)
    {
        return DIR + BUCKETIZER.id() +
               "." + totalHoleBuckets + "/";
    }
}
