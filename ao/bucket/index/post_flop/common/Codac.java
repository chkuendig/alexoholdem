package ao.bucket.index.post_flop.common;

/**
 * Date: Sep 15, 2008
 * Time: 4:40:05 PM
 */
public class Codac
{
    //--------------------------------------------------------------------
    private static final int SUIT_SET_BITS = (int)
            Math.ceil(Math.log(CanonSuitSet.VALUES.length) /
                      Math.log(2));
    private static final int SUIT_SET_MASK =
            0xFFFFFFFF >>> (32 - SUIT_SET_BITS);


    //--------------------------------------------------------------------
    private Codac() {}


    //--------------------------------------------------------------------
    public static int encodeTurn(CanonSuitSet suitSet, int globalOffset)
    {
        return suitSet.ordinal() |
               globalOffset << SUIT_SET_BITS;
    }
    public static CanonSuitSet decodeTurnSet(int encoded)
    {
        int ordinal = encoded & SUIT_SET_MASK;
        return CanonSuitSet.VALUES[ ordinal ];
    }
    public static int decodeTurnOffset(int encoded)
    {
        return encoded >>> SUIT_SET_BITS;
    }
}
