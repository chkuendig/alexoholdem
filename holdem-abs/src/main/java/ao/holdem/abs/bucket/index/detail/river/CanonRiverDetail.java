package ao.holdem.abs.bucket.index.detail.river;

import ao.holdem.abs.bucket.index.detail.CanonDetail;

/**
 * Date: Jan 9, 2009
 * Time: 12:40:56 PM
 */
public class CanonRiverDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    private final double STRENGTH;
    private final long   INDEX;
    private final byte   REPRESENTS;


    //--------------------------------------------------------------------
    public CanonRiverDetail(double strength,
                            long   index,
                            byte   represents)
    {
        STRENGTH   = strength;
        INDEX      = index;
        REPRESENTS = represents;
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

    public byte represents()
    {
        return REPRESENTS;
    }
}
