package ao.regret.holdem.mono;

import ao.bucket.abstraction.access.odds.IBucketOdds;
import ao.regret.holdem.InfoPart;
import ao.regret.holdem.IterativeMinimizer;
import ao.regret.holdem.parallel.ItrParAvgStrat;
import ao.regret.holdem.serial.AvgStrat;
import ao.regret.holdem.serial.RegMin;
import jsr166y.forkjoin.ForkJoinPool;

/**
 * User: alex
 * Date: 2-Aug-2009
 * Time: 6:34:40 PM
 */
public class ChainMinimizer implements IterativeMinimizer
{
    //--------------------------------------------------------------------
    public static IterativeMinimizer newMulti(
            InfoPart     info,
            IBucketOdds  odds,
            double       aggression)
    {
        return new ChainMinimizer(
                new RegMin(info, odds, aggression),
                new AvgStrat(info));
    }

    public static IterativeMinimizer newParAvgStrat(
            InfoPart     info,
            IBucketOdds  odds,
            double       aggression,
            ForkJoinPool exec)
    {
        return new ChainMinimizer(
                new RegMin(info, odds, aggression),
                new ItrParAvgStrat(exec, info));
    }


    //--------------------------------------------------------------------
    private final IterativeMinimizer A;
    private final IterativeMinimizer B;


    //--------------------------------------------------------------------
    private ChainMinimizer(
            IterativeMinimizer first,
            IterativeMinimizer last)
    {
        A = first;
        B = last;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char[] absDealerBuckets,
            char[] absDealeeBuckets)
    {
        A.iterate(absDealerBuckets, absDealeeBuckets);
        B.iterate(absDealerBuckets, absDealeeBuckets);
    }


    //--------------------------------------------------------------------
    public void flush() {
        A.flush();
        B.flush();
    }
}
