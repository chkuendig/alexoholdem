package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.IBucketOdds;
import jsr166y.ForkJoinPool;

/**
 * User: alex
 * Date: 26-Apr-2009
 * Time: 11:04:26 PM
 */
public class RegretMinimizer
{
    //--------------------------------------------------------------------
    private final IBucketOdds ODDS;
    private final InfoPart    INFO;

    private final ForkJoinPool EXEC = new ForkJoinPool();



    //--------------------------------------------------------------------
    public RegretMinimizer(
            InfoPart info, IBucketOdds odds)
    {
        INFO = info;
        ODDS = odds;
    }


    //--------------------------------------------------------------------
    public void minimize(char absDealerBuckets[],
                         char absDealeeBuckets[])
    {
        new RegMin(
                INFO, ODDS, 1.07, EXEC
        ).iterate( absDealerBuckets, absDealeeBuckets );


//        AvgStrat.iterate(
//                INFO, absDealerBuckets, absDealeeBuckets, EXEC);
        new AvgStrat(
                INFO, EXEC
        ).iterate( absDealerBuckets, absDealeeBuckets );
    }
}
