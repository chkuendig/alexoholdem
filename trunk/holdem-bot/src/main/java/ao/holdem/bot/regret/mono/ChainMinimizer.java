package ao.holdem.bot.regret.mono;

import ao.holdem.abs.bucket.abstraction.access.odds.IBucketOdds;
import ao.holdem.bot.regret.InfoPart;
import ao.holdem.bot.regret.IterativeMinimizer;
import ao.holdem.bot.regret.serial.AvgStrat;
import ao.holdem.bot.regret.serial.RegMin;

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
