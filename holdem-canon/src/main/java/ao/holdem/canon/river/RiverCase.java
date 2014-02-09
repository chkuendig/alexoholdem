package ao.holdem.canon.river;

import ao.holdem.canon.card.CanonSuit;

import static ao.holdem.canon.card.CanonSuit.*;

/**
 * Date: Sep 16, 2008
 * Time: 8:40:01 AM
 *
 * also split cases by how many other cards are in their equivalence class
 */
/*package-private*/ enum RiverCase
{
    //--------------------------------------------------------------------
    F1(FIRST, 1),
    F2(FIRST, 2),
    F3(FIRST, 3),
    F4(FIRST, 4),
    F5(FIRST, 5),
    F6(FIRST, 6),

    S0(SECOND, 0),
    S1(SECOND, 1),
    S2(SECOND, 2),
    S3(SECOND, 3),
    S4(SECOND, 4),
    S5(SECOND, 5),

    T0(THIRD, 0),
    T1(THIRD, 1),
    T2(THIRD, 2),
    T3(THIRD, 3),
    T4(THIRD, 4),

    R0(FOURTH, 0),
    R1(FOURTH, 1),
    R2(FOURTH, 2),
    ;

    public static final RiverCase VALUES[] = values();

    private static final RiverCase INDEX[][];
    static
    {
        INDEX = new RiverCase[ CanonSuit.VALUES.length ][ 7 ];

        for (CanonSuit suit : CanonSuit.VALUES)
        {
            for (int i = 0; i < 7; i++)
            {
                INDEX[ suit.ordinal() ][ i ] =
                        findValueOf(suit, i);
            }
        }
    }


    //--------------------------------------------------------------------
    public static RiverCase valueOf(
            CanonSuit suit,
            int       precedences)
    {
        return INDEX[ suit.ordinal() ][ precedences ];
    }
    private static RiverCase findValueOf(
            CanonSuit suit,
            int       precedences)
    {
        for (RiverCase riverCase : VALUES)
        {
            if (riverCase.SUIT == suit &&
                    riverCase.PRECEDENCES == precedences)
            {
                return riverCase;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final CanonSuit SUIT;
    private final int       PRECEDENCES;


    //--------------------------------------------------------------------
    private RiverCase(CanonSuit suit,
                      int       precenences)
    {
        SUIT        = suit;
        PRECEDENCES = precenences;
    }

    public int size()
    {
        return 13 - PRECEDENCES;
    }
}
