package ao.bucket.abstraction;

import ao.bucket.abstraction.flop.FlopBucketLookup;
import ao.bucket.abstraction.flop.FlopBucketizerImpl;
import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.SimpleHoleBucketizer;
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
//        testFlopBucketLookup();
//        testFlopBucketizer();

//        testHoleLookup();
        testFlopLookup();
    }


    //--------------------------------------------------------------------
    public static void testHoleLookup()
    {
        LOG.info("testing hole bucket lookup");

        HoleBucketLookup lookup =
                new HoleBucketLookup(new SimpleHoleBucketizer());
        BucketSet buckets = lookup.buckets((char) 13);
        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    i                  + "\t" +
                    Hole.reify( i )[0] + "\t" +
                    (int) buckets.bucketOf(i));
        }
    }


    //--------------------------------------------------------------------
    public static void testFlopLookup()
    {
        LOG.info("testing flop bucket lookup");

        HoleBucketLookup holeLookup =
                new HoleBucketLookup(new SimpleHoleBucketizer());
        BucketSet holeBuckets = holeLookup.buckets( (char) 13 );

        char  numFlopBuckets = 1134;
        int[] counts         = new int[ numFlopBuckets ];

        FlopBucketLookup lookup =
                new FlopBucketLookup(new FlopBucketizerImpl());
        BucketSet buckets = lookup.buckets(holeBuckets, numFlopBuckets);
        for (int i = 0; i < FlopLookup.CANON_FLOP_COUNT; i++)
        {
            counts[ buckets.bucketOf(i) ]++;
        }

        for (int i = 0; i < counts.length; i++)
        {
            System.out.println(i + "\t" + counts[i]);
        }
    }
}
