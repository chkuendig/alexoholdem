package ao.bucket.abstraction;

import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.SimpleHoleBucketizer;
import ao.bucket.abstraction.set.BucketSet;
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
        testBucketLookup();
    }


    //--------------------------------------------------------------------
//    public static void testFlopBucketLookup()
//    {
//        short                holeBuckets = 3;
//        short                flopBuckets = 3;
//        SimpleHoleBucketizer bucketizer  = new SimpleHoleBucketizer();
//
//        FlopBucketLookup flopBucketLookup =
//                    new FlopBucketLookup(
//                            bucketizer.id(),
//                            bucketizer.bucketize( holeBuckets ),
//                            SimpleFlopBucketizer.FACTORY);
//
//        int counts[] = new int[ holeBuckets * flopBuckets + 1 ];
//
//        for (int i = 0; i < FlopLookup.CANON_FLOP_COUNT; i++)
//        {
//            if (i % 1000 == 0)
//            {
//                LOG.info("now on " + i);
//            }
//
//            short flopBucket =
//                    flopBucketLookup.bucket(
//                            flopBuckets, i );
//            counts[ flopBucket + 1 ]++;
//        }
//
//        for (int i = 0; i < counts.length; i++)
//        {
//            System.out.println("\t" + (i - 1) + "\t" + counts[i]);
//        }
//    }


    //--------------------------------------------------------------------
//    public static void testFlopBucketizer()
//    {
//        SimpleHoleBucketizer bucketizer = new SimpleHoleBucketizer();
//        for (short holeBucket[] : bucketizer.bucketize( 3 ))
//        {
//            if (holeBucket.length == 0) continue;
//
//            SimpleFlopBucketizer flopBucketizer =
//                    new SimpleFlopBucketizer( holeBucket );
//
//            bucketizer.display(holeBucket);
//            flopBucketizer.display(
//                    flopBucketizer.bucketize( 3 ));
//        }
//    }



    //--------------------------------------------------------------------
    public static void testBucketLookup()
    {
        LOG.info("testing hole bucket lookup");

        HoleBucketLookup lookup =
                new HoleBucketLookup(new SimpleHoleBucketizer());
        BucketSet buckets = lookup.buckets( (char) 13 );
        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    i                  + "\t" +
                    Hole.reify( i )[0] + "\t" +
                    (int) buckets.bucketOf(i));
        }
    }


    //--------------------------------------------------------------------
    public static void testBucketizer()
    {
        SimpleHoleBucketizer bucketizer = new SimpleHoleBucketizer();
//        for (int i = 2; i <= 64; i++)
//        {
//            System.out.println("---------------------------------------");
            bucketizer.bucketize( (char) 20 ).display();
//        }
    }
}
