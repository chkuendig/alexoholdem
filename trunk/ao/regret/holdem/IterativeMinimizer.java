package ao.regret.holdem;

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
}
