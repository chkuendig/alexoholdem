package ao.bucket.abstraction;

import ao.bucket.abstraction.community.CommunityBucketLookup;
import ao.bucket.abstraction.flop.FlopBucketLookup;
import ao.bucket.abstraction.flop.FlopBucketizerImpl;
import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.HoleBucketLookupImpl;
import ao.bucket.abstraction.hole.HoleBucketizerImpl;
import ao.bucket.abstraction.set.BucketSet;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.card.Hole;
import org.apache.log4j.Logger;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        testHoleLookup();
        testFlopLookup();
    }


    //--------------------------------------------------------------------
    public static void testHoleLookup()
    {
        LOG.info("testing hole bucket lookup");

        char  nBuckets = 20;
        int[] counts   = new int[ nBuckets ];

        HoleBucketLookup lookup =
                new HoleBucketLookupImpl(new HoleBucketizerImpl());
        BucketSet buckets = lookup.buckets( nBuckets );
        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    i                  + "\t" +
                    Hole.reify( i )[0] + "\t" +
                    (int) buckets.bucketOf(i));
            counts[ buckets.bucketOf(i) ]++;
        }

        System.out.println("summary:");
        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }


    //--------------------------------------------------------------------
    public static void testFlopLookup()
    {
        LOG.info("testing flop bucket lookup");

        HoleBucketLookup holeLookup =
                new HoleBucketLookupImpl(new HoleBucketizerImpl());
        BucketSet holeBuckets = holeLookup.buckets( (char) 13 );

        char  numFlopBuckets = 1134;
        int[] counts         = new int[ numFlopBuckets ];

        CommunityBucketLookup lookup =
                new FlopBucketLookup(new FlopBucketizerImpl());
        BucketSet buckets = lookup.buckets(holeBuckets, numFlopBuckets);
        for (int i = 0; i < FlopLookup.CANON_FLOP_COUNT; i++)
        {
            counts[ buckets.bucketOf(i) ]++;
        }

        for (int i = 0; i < counts.length; i++)
            System.out.println(i + "\t" + counts[i]);
    }
}
