package ao.regret.holdem.parallel;

import ao.regret.holdem.InfoPart;
import ao.regret.holdem.IterativeMinimizer;
import jsr166y.forkjoin.ForkJoinPool;

/**
 * User: alex
 * Date: 2-Aug-2009
 * Time: 6:26:00 PM
 */
public class ItrParAvgStrat implements IterativeMinimizer
{
    //--------------------------------------------------------------------
    private final ForkJoinPool EXEC;
    private final InfoPart     INFO;


    //--------------------------------------------------------------------
    public ItrParAvgStrat(
            ForkJoinPool exec,
            InfoPart     info)
    {
        EXEC = exec;
        INFO = info;
    }


    //--------------------------------------------------------------------
    public void iterate(
            char[] absDealerBuckets,
            char[] absDealeeBuckets)
    {
        AvgStratPar.iterate(EXEC, INFO,
                absDealerBuckets, absDealeeBuckets);
    }


    //--------------------------------------------------------------------
    public void flush() {}
}
