package ao.holdem.engine.state.eval;

/**
 *
 */
public enum HandRank
{
    //--------------------------------------------------------------------
    HIGH_CARD      ("high card"),
    ONE_PAIR       ("pair"),
    TWO_PAIR       ("two pair"),
    THREE_OF_A_KIND("trips"),
    STRAIGHT       ("straight"),
    FLUSH          ("flush"),
    FULL_HOUSE     ("full house"),
    FOUR_OF_A_KIND ("quad"),
    STRAIGHT_FLUSH ("straight flush");


    //--------------------------------------------------------------------
    private final String name;


    //--------------------------------------------------------------------
    private HandRank(String givenName)
    {
        name = givenName;
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return name;
    }


    //------------------------------------------------------------------------
    public static HandRank fromValue(short handStrength)
    {
//            (7462 - bestFiveRank())
//            (7462 - 1277)
        if (handStrength < 1277) return HIGH_CARD;        // 1277 high card
        if (handStrength < 4137) return ONE_PAIR;         // 2860 one pair
        if (handStrength < 4995) return TWO_PAIR;         //  858 two pair
        if (handStrength < 5853) return THREE_OF_A_KIND;  //  858 three-kind
        if (handStrength < 5863) return STRAIGHT;         //   10 straights
        if (handStrength < 7140) return FLUSH;            // 1277 flushes
        if (handStrength < 7296) return FULL_HOUSE;       //  156 full house
        if (handStrength < 7452) return FOUR_OF_A_KIND;   //  156 four-kind
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
