package ao.regret.holdem;

import ao.bucket.Bucket;
import ao.odds.agglom.Odds;

import java.util.Collection;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:44:17 PM
 */
public class HoldemBucket implements Bucket<HoldemBucket>
{
    public Odds against(HoldemBucket otherTerminal)
    {
        return null;
    }

    public Collection<HoldemBucket> nextBuckets()
    {
        return null;
    }
}
