package ao.holdem.engine.eval;

import ao.holdem.model.card.Card;

/**
 * See
 *  http://www.suffecool.net/poker/evaluator.html
 */
public enum Eval5
{;
    //--------------------------------------------------------------------
    private static final int PRIMES[] =
            {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};


    //--------------------------------------------------------------------

    // from 0 (lowest) to 7461 (highest)
    public static short valueOf(Card... fiveCards)
    {
        assert fiveCards.length == 5;
        return valueOf(fiveCards[0], fiveCards[1], fiveCards[2], fiveCards[3], fiveCards[4]);
    }

    // from 0 (lowest) to 7461 (highest)
    public static short valueOf(
            Card c1, Card c2, Card c3, Card c4, Card c5)
    {
        return valueOf(asCactusKevsFormat(c1),
                       asCactusKevsFormat(c2),
                       asCactusKevsFormat(c3),
                       asCactusKevsFormat(c4),
                       asCactusKevsFormat(c5));
    }

    // takes cards formatted asCactusKevsFormat.
    // from 0 (lowest) to 7461 (highest)
    public static short valueOf(
            int ck1, int ck2, int ck3, int ck4, int ck5)
    {
        return (short)(HandStrength.COUNT -
                         kevsValueOf(ck1, ck2, ck3, ck4, ck5));
    }


    //--------------------------------------------------------------------
    //from 7462 (lowest) .. 1 (highest)
    private static short kevsValueOf(
            int ck1, int ck2, int ck3, int ck4, int ck5)
    {
        int index = (ck1 | ck2 | ck3 | ck4 | ck5) >> 16;

        // check for flushes and straight flushes
        if (suitsEqual(ck1, ck2, ck3, ck4, ck5))
        {
            return Eval5Lookup.flushes( index );
        }

        // check for straights and high card hands
        short high = Eval5Lookup.unique5( index );
        if (high != 0) return high;

        return Eval5Lookup.remainingHands(
                (ck1 & 0xff) * (ck2 & 0xff) * (ck3 & 0xff) *
                (ck4 & 0xff) * (ck5 & 0xff));
    }

    private static boolean suitsEqual(
            int c1, int c2, int c3, int c4, int c5)
    {
        return (c1 & c2 & c3 & c4 & c5 & 0xf000) > 0;
    }


    //--------------------------------------------------------------------
    public static int asCactusKevsFormat(Card card)
    {
        return asCactusKevsFormat(
                card.rank().ordinal(),
                card.suit().ordinal());
    }

    public static int asCactusKevsFormat(
            int rankOrdinal, int suitOrdinal)
    {
        //        +--------+--------+--------+--------+
        //        |xxxbbbbb|bbbbbbbb|cdhsrrrr|xxpppppp|
        //        +--------+--------+--------+--------+
        //        p = prime number of rank (deuce=2,trey=3,four=5,...,ace=41)
        //        r = rank of card (deuce=0,trey=1,four=2,five=3,...,ace=12)
        //        cdhs = suit of card (bit turned on based on suit of card)
        //        b = bit turned on depending on rank of card

        int rankMask  = 1 << rankOrdinal;
        int rankPrime = PRIMES[ rankOrdinal ];
        int suitMask  = 1 << (3 - suitOrdinal);

        return rankMask    << 16 |
               suitMask    << 12 |
               rankOrdinal << 8  |
               rankPrime;
    }
}
