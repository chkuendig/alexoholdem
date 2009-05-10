package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.IBucketOdds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final ExecutorService EXEC =
            Executors.newFixedThreadPool(8);



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
                INFO, ODDS, 1.0, EXEC
        ).iterate( absDealerBuckets, absDealeeBuckets );

        new AvgStrat(
                INFO, EXEC
        ).iterate( absDealerBuckets, absDealeeBuckets );
    }
}
