package ao.bucket.abstraction.access.odds;

/**
 * User: alex
 * Date: 5-Apr-2009
 * Time: 12:54:36 PM
 */
public class BucketOddsCache implements IBucketOdds
{
    //--------------------------------------------------------------------
    private float CACHE[][];


    //--------------------------------------------------------------------
    public BucketOddsCache(IBucketOdds odds, char nBuckets)
    {
        CACHE = new float[ nBuckets ][];
        for (char i = 0; i < nBuckets; i++) {
            CACHE[i] = new float[ i + 1 ];
            for (char j = 0; j <= i; j++) {
                CACHE[i][j] = (float) odds.nonLossProb(i, j);
            }
        }
    }


    //--------------------------------------------------------------------
    public double nonLossProb(char index, char vsIndex) {
        if (index >= vsIndex) {
            return CACHE[ index ][ vsIndex ];
        } else {
            return CACHE[ vsIndex ][ index ];
        }
    }
}
