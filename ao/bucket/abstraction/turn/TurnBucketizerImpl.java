package ao.bucket.abstraction.turn;

import ao.bucket.abstraction.community.CommunityBucketizer;
import ao.bucket.abstraction.set.BucketSet;
import org.apache.log4j.Logger;

/**
 * Date: Dec 26, 2008
 * Time: 4:34:23 PM
 */
public class TurnBucketizerImpl implements CommunityBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnBucketizerImpl.class);


    //--------------------------------------------------------------------
    public <T extends BucketSet> T
            bucketize(BucketSet            onTopOf,
                      char                 numBuckets,
                      BucketSet.Builder<T> with)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return getClass().getSimpleName();
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return id();
    }
}
