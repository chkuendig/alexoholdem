package ao.regret.holdem;

import ao.bucket.abstraction.access.odds.IBucketOdds;

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

//    private



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
                INFO, ODDS, 1.0
        ).iterate( absDealerBuckets, absDealeeBuckets );

        new AvgStrat(
                INFO
        ).iterate( absDealerBuckets, absDealeeBuckets );
    }
}
