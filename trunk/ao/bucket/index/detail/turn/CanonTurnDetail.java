package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.card.Card;

/**
 * Date: Jan 9, 2009
 * Time: 12:38:16 PM
 */
public class CanonTurnDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    private final int   CANON_INDEX;
    private final Card  EXAMPLE;
    private final byte  REPRESENTS;
    private final float STRENGTH;
    private final int   FIRST_CANON_RIVER;
    private final byte  CANON_RIVER_COUNT;


    //--------------------------------------------------------------------
    public CanonTurnDetail(
            int   canonIndex,
            Card  example,
            byte  represents,
            float strength,
            int   firstCanonRiver,
            byte  canonRiverCount)
    {
        CANON_INDEX       = canonIndex;
        EXAMPLE           = example;
        REPRESENTS        = represents;
        STRENGTH          = strength;
        FIRST_CANON_RIVER = firstCanonRiver;
        CANON_RIVER_COUNT = canonRiverCount;
    }


    //--------------------------------------------------------------------
    public long canonIndex()
    {
        return 0;
    }


    //--------------------------------------------------------------------
    public Card example()
    {
        return null;
    }


    //--------------------------------------------------------------------
    public byte represents()
    {
        return 0;
    }


    //--------------------------------------------------------------------
    public double strengthVsRandom()
    {
        return 0;
    }


    //--------------------------------------------------------------------
    public long firstCanonRiver()
    {
        return 0;
    }

    public byte canonRiverCount()
    {
        return 0;
    }
}
