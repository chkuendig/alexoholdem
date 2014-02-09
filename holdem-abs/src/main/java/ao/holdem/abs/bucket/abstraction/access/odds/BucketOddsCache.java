package ao.holdem.abs.bucket.abstraction.access.odds;

import org.apache.log4j.Logger;

/**
 * User: alex
 * Date: 5-Apr-2009
 * Time: 12:54:36 PM
 */
public class BucketOddsCache implements IBucketOdds
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketOddsCache.class);


    //--------------------------------------------------------------------
//    private float CACHE[][];
    private double CACHE[][];


    //--------------------------------------------------------------------
    public BucketOddsCache(IBucketOdds odds, char nBuckets)
    {
        LOG.debug("building cache for " + (int) nBuckets);
//        CACHE = new float[ nBuckets ][];
        CACHE = new double[ nBuckets ][];
        for (char i = 0; i < nBuckets; i++) {
//            CACHE[i] = new float[ i + 1 ];
            CACHE[i] = new double[ i + 1 ];
            for (char j = 0; j <= i; j++) {
//                CACHE[i][j] = (float) odds.nonLossProb(i, j);
                CACHE[i][j] = odds.nonLossProb(i, j);
            }
        }
    }


    //--------------------------------------------------------------------
    public double nonLossProb(char index, char vsIndex) {
        if (index >= vsIndex) {
            return CACHE[ index ][ vsIndex ];
        } else {
            return 1.0 - CACHE[ vsIndex ][ index ];
        }
    }
}
