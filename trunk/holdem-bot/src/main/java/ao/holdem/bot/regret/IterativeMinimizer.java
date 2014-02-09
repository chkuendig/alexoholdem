package ao.holdem.bot.regret;

/**
 * User: alex
 * Date: 2-Aug-2009
 * Time: 6:21:59 PM
 */
public interface IterativeMinimizer
{
    public void iterate(
            char absDealerBuckets[],
            char absDealeeBuckets[]);

    public void flush();
}
