package ao.bucket.abstraction;

import ao.bucket.abstraction.hole.HoleBucketLookup;
import ao.bucket.abstraction.hole.SimpleHoleBucketizer;
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
        HoleBucketLookup lookup =
                new HoleBucketLookup(new SimpleHoleBucketizer());

        for (int i = 0; i < Hole.CANONICAL_COUNT; i++)
        {
            System.out.println(
                    i                    + "\t" +
                    lookup.bucket(3,  i));
        }

//        SimpleHoleBucketizer bucketizer = new SimpleHoleBucketizer();
//        for (short holeBucket[] : bucketizer.bucketize( 5 ))
//        {
//            if (holeBucket.length == 0) continue;
//
//            SimpleFlopBucketizer flopBucketizer =
//                    new SimpleFlopBucketizer( holeBucket );
//
//            bucketizer.display(holeBucket);
//            flopBucketizer.display(
//                    flopBucketizer.bucketize( 5 ));
//        }

//        for (int i = 2; i <= 64; i++)
//        {
//            System.out.println("---------------------------------------");
//            bucketizer.display(
//                    bucketizer.bucketize(i) );
//        }
    }
}
