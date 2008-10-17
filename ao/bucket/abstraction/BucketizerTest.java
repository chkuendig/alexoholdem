package ao.bucket.abstraction;

import ao.bucket.abstraction.flop.FlopBucketLookup;
import ao.bucket.abstraction.flop.SimpleFlopBucketizer;
import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.SimpleHoleBucketizer;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.card.Hole;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        testFlopBucketLookup();
//        testFlopBucketizer();
        testBucketLookup();
    }


    //--------------------------------------------------------------------
    public static void testFlopBucketLookup()
    {
        short                holeBuckets = 3;
        short                flopBuckets = 3;
        SimpleHoleBucketizer bucketizer  = new SimpleHoleBucketizer();

        short holeBucket = 0;
        for (short[] canonHoles : bucketizer.bucketize( holeBuckets ))
        {
            System.out.println("hole bucket: " + canonHoles.length);
            SimpleFlopBucketizer flopBucketizer =
                    new SimpleFlopBucketizer( canonHoles );

            FlopBucketLookup flopBucketLookup =
                    new FlopBucketLookup(
                            bucketizer.id(),
                            holeBuckets,
                            flopBucketizer);

            int counts[] = new int[ holeBuckets * flopBuckets + 1 ];
            for (int i = 0; i < FlopLookup.CANON_FLOP_COUNT; i++)
            {
                short flopBucket =
                        flopBucketLookup.bucket(
                                holeBucket,
                                flopBuckets, i );
                counts[ flopBucket + 1 ]++;
            }
            for (int i = 0; i < counts.length; i++)
            {
                System.out.println("\t" + (i - 1) + "\t" + counts[i]);
            }
        }
    }


    //--------------------------------------------------------------------
    public static void testBucketLookup()
    {
        HoleBucketLookup lookup =
                new HoleBucketLookup(new SimpleHoleBucketizer());
        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    i                    + "\t" +
                    lookup.bucket(3,  i));
        }
    }


    //--------------------------------------------------------------------
    public static void testFlopBucketizer()
    {
        SimpleHoleBucketizer bucketizer = new SimpleHoleBucketizer();
        for (short holeBucket[] : bucketizer.bucketize( 3 ))
        {
            if (holeBucket.length == 0) continue;

            SimpleFlopBucketizer flopBucketizer =
                    new SimpleFlopBucketizer( holeBucket );

            bucketizer.display(holeBucket);
            flopBucketizer.display(
                    flopBucketizer.bucketize( 3 ));
        }
    }

    //--------------------------------------------------------------------
    public static void testBucketizer()
    {
        SimpleHoleBucketizer bucketizer = new SimpleHoleBucketizer();
//        for (int i = 2; i <= 64; i++)
//        {
//            System.out.println("---------------------------------------");
            bucketizer.display(
                    bucketizer.bucketize(20) );
//        }
    }
}
