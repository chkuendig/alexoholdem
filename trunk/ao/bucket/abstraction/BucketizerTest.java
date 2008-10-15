package ao.bucket.abstraction;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
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

//        for (int i = 2; i <= 64; i++)
//        {
//            System.out.println("---------------------------------------");
//            bucketizer.display(
//                    bucketizer.bucketize(i) );
//        }
    }
}
