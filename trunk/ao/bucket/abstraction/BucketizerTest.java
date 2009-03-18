package ao.bucket.abstraction;

import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.regret.holdem.v2.InfoTree;
import ao.regret.holdem.v2.RegretMinimizer;
import ao.util.misc.Progress;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

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

        CardAbstraction abs =
                new CardAbstraction(
                        new BucketizerImpl(),
                        nHoleBuckets,
                        nFlopBuckets,
                        nTurnBuckets,
                        nRiverBuckets);

        InfoTree        info   = new InfoTree(
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);
        RegretMinimizer cfrMin = new RegretMinimizer(
                                         info, abs.odds());

        long iterations = 1000 * 1000 * 1000;
        Progress prog = new Progress(iterations);
        for (Iterator<char[][]> it = abs.sequence().iterator(iterations);
             it.hasNext();)
        {
            char[][] jbs = it.next();

            cfrMin.minimize(
                    jbs[0], jbs[1]);

            prog.checkpoint();
//            System.out.println(toString(jbs));
        }

        info.displayFirstAct();
    }

    private static String toString(char[][] jbs)
    {
        int asInt[][] = new int[2][4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                asInt[i][j] = jbs[i][j];
            }
        }
        return Arrays.deepToString( asInt );
    }
}
