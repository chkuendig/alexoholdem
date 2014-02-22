package ao.holdem.bot.main;

import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder;
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.holdem.bot.regret.HoldemAbstraction;

/**
 * 21/02/14 6:55 AM
 */
public class BucketTest
{
    public static void main(String[] args)
    {
        int  nHoleBuckets  =   5;
        char nFlopBuckets  =  25;
        char nTurnBuckets  = 125;
        char nRiverBuckets = 625;

        HoldemAbstraction abs = new HoldemAbstraction(
                new FastBucketTreeBuilder(
                        new KMeansBucketizer()),
                nHoleBuckets,
                nFlopBuckets,
                nTurnBuckets,
                nRiverBuckets);

        abs.tree(false);
    }
}
