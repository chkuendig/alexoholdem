package ao.regret.holdem;

import ao.bucket.Bucket;

import java.util.Collection;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:44:17 PM
 */
public class HoldemBucket implements Bucket<HoldemBucket>
{
    public double against(HoldemBucket otherTerminal)
    {
        return 0;
    }

    public Collection<HoldemBucket> nextBuckets()
    {
        return null;
    }

    public HoldemBucket nextBucket(byte index)
    {
        return null;
    }


    public char absoluteIndex()
    {
        return 0;
    }
}
