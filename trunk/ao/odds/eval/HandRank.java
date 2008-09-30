package ao.odds.eval;

/**
 *
 */
public enum HandRank
{
    //--------------------------------------------------------------------
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH;


    //--------------------------------------------------------------------
    public static HandRank fromValue(int val)
    {
//            (7462 - bestFiveRank())
//            (7462 - 1277)
        if (val < 1277) return HIGH_CARD;        // 1277 high card
        if (val < 4137) return ONE_PAIR;         // 2860 one pair
        if (val < 4995) return TWO_PAIR;         //  858 two pair
        if (val < 5853) return THREE_OF_A_KIND;  //  858 three-kind
        if (val < 5863) return STRAIGHT;         //   10 straights
        if (val < 7140) return FLUSH;            // 1277 flushes
        if (val < 7296) return FULL_HOUSE;       //  156 full house
        if (val < 7452) return FOUR_OF_A_KIND;   //  156 four-kind
        return STRAIGHT_FLUSH;                   //   10 straight-flushes


//            if (val > 6185) return HIGH_CARD;        // 1277 high card
//            if (val > 3325) return ONE_PAIR;         // 2860 one pair
//            if (val > 2467) return TWO_PAIR;         //  858 two pair
//            if (val > 1609) return THREE_OF_A_KIND;  //  858 three-kind
//            if (val > 1599) return STRAIGHT;         //   10 straights
//            if (val > 322)  return FLUSH;            // 1277 flushes
//            if (val > 166)  return FULL_HOUSE;       //  156 full house
//            if (val > 10)   return FOUR_OF_A_KIND;   //  156 four-kind
//            return STRAIGHT_FLUSH;                   //   10 straight-flushes
    }
}
