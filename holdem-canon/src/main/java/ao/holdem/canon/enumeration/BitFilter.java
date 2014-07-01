package ao.holdem.canon.enumeration;

import ao.holdem.model.card.canon.base.CanonIndexed;
import ao.util.data.LongBitSet;
import ao.util.pass.Filter;

/**
 * Date: Feb 5, 2009
 * Time: 12:35:49 PM
 */
public class BitFilter<T extends CanonIndexed>
        implements Filter<T>
{
    //--------------------------------------------------------------------
    private final LongBitSet BITS;


    //--------------------------------------------------------------------
    public BitFilter(LongBitSet allowBits) {
        BITS = allowBits;
    }


    //--------------------------------------------------------------------
    public boolean accept(T canonIndexed)
    {
        return BITS.get(canonIndexed.packedCanonIndex());
    }
}
