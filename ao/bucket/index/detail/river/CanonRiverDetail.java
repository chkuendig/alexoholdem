package ao.bucket.index.detail.river;

import ao.bucket.index.detail.CanonDetail;

/**
 * Date: Jan 9, 2009
 * Time: 12:40:56 PM
 */
public class CanonRiverDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    private final double STRENGTH;
    private final long   INDEX;


    //--------------------------------------------------------------------
    public CanonRiverDetail(double strength,
                            long   index)
    {
        STRENGTH = strength;
        INDEX    = index;
    }


    //--------------------------------------------------------------------
    public double strength()
    {
        return STRENGTH;
    }

    public long canonIndex()
    {
        return INDEX;
    }
}
