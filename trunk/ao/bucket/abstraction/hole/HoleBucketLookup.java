package ao.bucket.abstraction.hole;

import ao.holdem.model.card.Hole;
import ao.util.data.AutovivifiedList;
import ao.util.persist.PersistentBytes;

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
    private final List< byte[] > CACHE;
    private final HoleBucketizer BUCKETIZER;


    //--------------------------------------------------------------------
    public HoleBucketLookup(HoleBucketizer bucketizer)
    {
        BUCKETIZER = bucketizer;
        CACHE      = new AutovivifiedList<byte[]>();
    }


    //--------------------------------------------------------------------
    private byte[] retrieveOrCreate(int totalHoleBuckets)
    {
        byte cache[] = CACHE.get( totalHoleBuckets );
        if (cache != null) return cache;

        String file = bucketFile( totalHoleBuckets );
        cache = PersistentBytes.retrieve(file);
        if (cache == null)
        {
            cache = new byte[ Hole.CANONICAL_COUNT ];

            short buckets[][] =
                    BUCKETIZER.bucketize( totalHoleBuckets );
            for (byte bucket = 0; bucket < buckets.length; bucket++)
            {
                for (short canon : buckets[ bucket ])
                {
                    cache[ canon ] = bucket;
                }
            }
            PersistentBytes.persist(cache, file);
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


    //--------------------------------------------------------------------
    public short bucket(int totalHoleBuckets, int forCanonHole)
    {
        return retrieveOrCreate( totalHoleBuckets )[ forCanonHole ];
    }
}
