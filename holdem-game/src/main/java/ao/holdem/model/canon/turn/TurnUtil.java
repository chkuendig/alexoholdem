package ao.holdem.model.canon.turn;

/**
 * Date: Sep 15, 2008
 * Time: 4:40:05 PM
 */
/*package-private*/ class TurnUtil
{
    //--------------------------------------------------------------------
    private static final int SUIT_SET_BITS = (int)
            Math.ceil(Math.log(TurnCase.VALUES.length) /
                      Math.log(2));
    private static final int SUIT_SET_MASK =
            0xFFFFFFFF >>> (32 - SUIT_SET_BITS);


    //--------------------------------------------------------------------
    private TurnUtil() {}


    //--------------------------------------------------------------------
    public static int encodeTurn(TurnCase suitSet, int globalOffset)
    {
        return suitSet.ordinal() |
               globalOffset << SUIT_SET_BITS;
    }
    public static TurnCase decodeTurnSet(int encoded)
    {
        int ordinal = encoded & SUIT_SET_MASK;
        return TurnCase.VALUES[ ordinal ];
    }
    public static int decodeTurnOffset(int encoded)
    {
        return encoded >>> SUIT_SET_BITS;
    }
}
