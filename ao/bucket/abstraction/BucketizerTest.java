package ao.bucket.abstraction;

import ao.bucket.abstraction.access.BucketFlyweight;
import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.abstraction.bucketize.BucketManager;
import ao.bucket.abstraction.bucketize.BucketizerImpl;

import java.io.IOException;
import java.util.Arrays;

/**
 * Date: Oct 14, 2008
 * Time: 8:13:57 PM
 */
public class BucketizerTest
{
//    //--------------------------------------------------------------------
//    private static final Logger LOG =
//            Logger.getLogger(BucketizerTest.class);


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        double  holeBranch = 6,
//                flopBranch = 24,
//                turnBranch = 3,
//               riverBranch = 3;
        double  holeBranch = 20,
                flopBranch = 56.72,
                turnBranch = 6.55,
               riverBranch = 6.63;

        if (args.length > 1)
        {
             holeBranch = Double.parseDouble(args[0]);
             flopBranch = Double.parseDouble(args[1]);
             turnBranch = Double.parseDouble(args[2]);
            riverBranch = Double.parseDouble(args[3]);
        }

        System.out.println("Branching: " +
            Arrays.toString(new double[]{
                    holeBranch, flopBranch, turnBranch, riverBranch}));

        test((byte) (holeBranch),
             (char) (holeBranch * flopBranch),
             (char) (holeBranch * flopBranch * turnBranch),
             (char) (holeBranch * flopBranch * turnBranch * riverBranch));

//        test((byte) (20),
//             (char) (1134),
//             (char) (7430),
//             (char) (49263));
    }


    //--------------------------------------------------------------------
    public static void test(
            byte nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets) throws IOException
    {
        System.out.println("testing: " +
                Arrays.asList((int) nHoleBuckets, (int) nFlopBuckets,
                              (int) nTurnBuckets, (int) nRiverBuckets));

        BucketManager manager =
                new BucketManager( new BucketizerImpl() );

        BucketTree buckets = manager.bucketize(
                                nHoleBuckets,
                                nFlopBuckets,
                                nTurnBuckets,
                                nRiverBuckets);
        BucketFlyweight map = buckets.map();


//        FileWriter out = new FileWriter("test/str.txt");
//        for (char i = 0; i < nRiverBuckets; i++)
//        {
//            out.append(map.odds().strength(i).toString())
//               .append("\n");
//        }
//        out.close();

        for (char i = 0; i < Math.min(nRiverBuckets, 32); i++)
        {
            System.out.println(
                    map.odds().strength(i).length());
        }

//        System.out.println(map.root());
    }
}
