package ao.bucket.abstraction;

import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.regret.holdem.InfoTree;
import ao.regret.holdem.RegretMinimizer;
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
        byte nHoleBuckets  = 6;
        char nFlopBuckets  = 144;
        char nTurnBuckets  = 432;
        char nRiverBuckets = 1296;
//        byte nHoleBuckets  = 13;
//        char nFlopBuckets  = 567;
//        char nTurnBuckets  = 1854;
//        char nRiverBuckets = 5786;

        if (args.length > 1)
        {
            nHoleBuckets  = (byte) Integer.parseInt(args[0]);
            nFlopBuckets  = (char) Integer.parseInt(args[1]);
            nTurnBuckets  = (char) Integer.parseInt(args[2]);
            nRiverBuckets = (char) Integer.parseInt(args[3]);
        }

        test(nHoleBuckets,
             nFlopBuckets,
             nTurnBuckets,
             nRiverBuckets);
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

        final HoldemAbstraction abs =
                new HoldemAbstraction(
                        new BucketizerImpl(),
                        nHoleBuckets,
                        nFlopBuckets,
                        nTurnBuckets,
                        nRiverBuckets);

//        BucketOdds odds = abs.odds();
//        for (Iterator<char[][]> it = abs.sequence().iterator(1000);
//             it.hasNext();)
//        {
//            char[][] jbs = it.next();
//            System.out.println(
//                    odds.nonLossProb(jbs[0][3], jbs[1][3]));
//        }


        InfoTree info   = abs.info();
        RegretMinimizer cfrMin = new RegretMinimizer(
                                         info, abs.odds());

        long i          = 0;
        long iterations = 100 * 1000 * 1000;
        Progress prog = new Progress(iterations);
        for (Iterator<char[][]> it = abs.sequence().iterator(iterations);
             it.hasNext();)
        {
            if (i++ % (100 * 1000) == 0) {
                System.out.println(" " + (i - 1));
                info.displayFirstAct();
                abs.flushInfo();
            }

            char[][] jbs = it.next();
            cfrMin.minimize(
                    jbs[0], jbs[1]);

            prog.checkpoint();
        }

//        new DealerTest().roundRobin(new HashMap<Avatar, Player>(){{
////            put(Avatar.local("duane"), new DuaneBot());
////            put(Avatar.local("raise"), new AlwaysRaiseBot());
////            put(Avatar.local("random"), new RandomBot());
////            put(Avatar.local("math"), new MathBot());
//            put(Avatar.local("human"), new ConsoleBot());
//            put(Avatar.local("cfr-1296"), new CfrBot(abs));
//
//        }});
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
