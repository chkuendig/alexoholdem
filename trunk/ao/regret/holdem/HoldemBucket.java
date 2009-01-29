package ao.regret.holdem;

import ao.bucket.Bucket;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:44:17 PM
 */
public interface HoldemBucket extends Bucket<HoldemBucket>
{
    public HoldemBucket nextBucket(byte index);
    public byte index();
}
